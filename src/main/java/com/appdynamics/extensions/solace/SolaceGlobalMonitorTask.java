package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.solace.semp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Worker task for all Solace metrics gathering. Executes all desired metrics queries
 * on the SempService that is provided to it.
 */
class SolaceGlobalMonitorTask implements AMonitorTaskRunnable {
    private static final Logger logger = LoggerFactory.getLogger(SolaceGlobalMonitorTask.class);

    private static final String VPNS_PREFIX = "MsgVpns";

    SolaceGlobalMonitorTask(MetricPrinter metricPrinter, String basePrefix,
                            ServerExclusionPolicies exclusionPolicies, SempService svc) {
        this.metricPrinter     = metricPrinter;
        this.basePrefix        = basePrefix;
        this.exclusionPolicies = exclusionPolicies;
        this.svc               = svc;
    }

    @Override
    public void run() {
        logger.debug("<SolaceGlobalMonitorTask.run>");

        startTimeMillis = System.currentTimeMillis();

        final String serverName = svc.getDisplayName();
        logger.debug("Configured metricPrefix: {}, ServerName {}", basePrefix, serverName);

        // Run Service check
        Map<String,Object> serviceStats = svc.checkGlobalServiceStatus();
        metricPrinter.printMetrics(serviceStats, basePrefix, serverName, Metrics.Service.PREFIX);

        // Run Redundancy check
        Map<String,Object> redundancyStats = svc.checkGlobalRedundancy();
        metricPrinter.printMetrics(redundancyStats, basePrefix, serverName, Metrics.Redundancy.PREFIX);

        // Run MsgSpool check
        Map<String,Object> spoolStats = svc.checkGlobalMsgSpoolStats();
        metricPrinter.printMetrics(spoolStats, basePrefix, serverName, Metrics.MsgSpool.PREFIX);

        // Run Statistics check
        Map<String,Object> clientStats = svc.checkGlobalStats();
        metricPrinter.printMetrics(clientStats, basePrefix, serverName, Metrics.Statistics.PREFIX);

        // Run VPN checks
        checkMsgVpns( serverName );

        // Run queues checks
        checkQueues( serverName );
        checkQueueRates( serverName );

        // Run topic-endpoints checks
        checkTopicEndpoints( serverName );
        checkTopicEndpointRates( serverName );

        // Run bridges check
        checkBridges( serverName );

        // Derive additional metrics
        Map<String,Object> derivedMetrics = deriveMetrics(serviceStats, redundancyStats, spoolStats);
        metricPrinter.printMetrics(derivedMetrics, basePrefix, serverName, Metrics.Derived.PREFIX);
        logger.debug("</SolaceGlobalMonitorTask.run>");
    }

    @Override
    public void onTaskComplete() {
        double seconds = (System.currentTimeMillis()-this.startTimeMillis)/1000.0;
        logger.info("SolaceGlobalMonitorTask monitoring run completed in %d seconds.", seconds);
    }

    private Boolean getIsDurable(Map<String,Object> map, String fieldName) {
        return 1 == ((Integer)map.getOrDefault(fieldName, 0));
    }

    private void checkMsgVpns(String serverName) {
        for(Map<String,Object> vpn : svc.checkMsgVpnList()) {
            String vpnName= (String) vpn.get(Metrics.Vpn.VpnName);
            if ( Helper.isExcluded(vpnName, exclusionPolicies.getVpnFilter(), exclusionPolicies.getVpnExclusionPolicy()) ) {
                logger.info("NOT writing metrics for the '{}' MsgVPN because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            vpn.remove(Metrics.Vpn.VpnName);
            metricPrinter.printMetrics(vpn, basePrefix, serverName, VPNS_PREFIX, vpnName);
        }
    }

    private void checkQueues(String serverName) {
        for(Map<String,Object> queue : svc.checkQueueList()) {
            String vpnName= (String) queue.get(Metrics.Queue.VpnName);
            if ( Helper.isExcluded(vpnName, exclusionPolicies.getVpnFilter(), exclusionPolicies.getVpnExclusionPolicy()) ) {
                logger.info("NOT writing metrics for any queues in the '{}' MsgVPN because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            String qname = (String) queue.get(Metrics.Queue.QueueName);
            if ( Helper.isExcluded(qname, exclusionPolicies.getQueueFilter(), exclusionPolicies.getQueueExclusionPolicy()) ) {
                logger.info("NOT writing metrics for queue '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                        qname, MonitorConfigs.QUEUE_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_QUEUES);
                continue;
            }
            if (exclusionPolicies.getExcludeTemporaries() ) {
                if (!getIsDurable(queue, Metrics.Queue.IsDurable)) {
                    logger.info("NOT writing metrics for temporary queue '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' configuration.",
                            qname, MonitorConfigs.EXCLUDE_TEMPORARIES);
                    continue;
                }
            }
            queue.remove(Metrics.Queue.VpnName);
            queue.remove(Metrics.Queue.QueueName);
            metricPrinter.printMetrics(queue, basePrefix, serverName,
                    VPNS_PREFIX, vpnName,
                    Metrics.Queue.PREFIX, qname);
        }
    }

    private void checkQueueRates(String serverName) {
        for(Map<String,Object> queue : svc.checkQueueRatesList()) {
            String vpnName= (String) queue.get(Metrics.Queue.VpnName);
            if ( Helper.isExcluded(vpnName, exclusionPolicies.getVpnFilter(), exclusionPolicies.getVpnExclusionPolicy()) ) {
                logger.info("NOT writing rate metrics for any queues in the '{}' MsgVPN because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            String qname = (String) queue.get(Metrics.Queue.QueueName);
            if ( Helper.isExcluded(qname, exclusionPolicies.getQueueFilter(), exclusionPolicies.getQueueExclusionPolicy()) ) {
                logger.info("NOT writing rate metrics for queue '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                        qname, MonitorConfigs.QUEUE_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_QUEUES);
                continue;
            }
            if (exclusionPolicies.getExcludeTemporaries() ) {
                if (!getIsDurable(queue, Metrics.Queue.IsDurable)) {
                    logger.info("NOT writing rate metrics for temporary queue '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' configuration.",
                            qname, MonitorConfigs.EXCLUDE_TEMPORARIES);
                    continue;
                }
            }
            queue.remove(Metrics.Queue.VpnName);
            queue.remove(Metrics.Queue.QueueName);
            metricPrinter.printMetrics(queue, basePrefix, serverName,
                    VPNS_PREFIX, vpnName,
                    Metrics.Queue.PREFIX, qname);
        }
    }

    private void checkTopicEndpoints(String serverName) {
        for(Map<String,Object> endpoint : svc.checkTopicEndpointList()) {
            String vpnName= (String) endpoint.get(Metrics.TopicEndpoint.VpnName);
            if ( Helper.isExcluded(vpnName, exclusionPolicies.getVpnFilter(), exclusionPolicies.getVpnExclusionPolicy()) ) {
                logger.info("NOT writing metrics for any topic endpoints in the '{}' MsgVPN because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            String teName = (String) endpoint.get(Metrics.TopicEndpoint.TopicEndpointName);
            if ( Helper.isExcluded(teName, exclusionPolicies.getTopicEndpointFilter(), exclusionPolicies.getTopicEndpointExclusionPolicy()) ) {
                logger.info("NOT writing metrics for topic endpoint '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                        teName, MonitorConfigs.TOPIC_ENDPOINT_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_TOPIC_ENDPOINTS);
                continue;
            }
            if (exclusionPolicies.getExcludeTemporaries() ) {
                if (!getIsDurable(endpoint, Metrics.TopicEndpoint.IsDurable)) {
                    logger.info("NOT writing metrics for temporary topic-endpoint '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' configuration.",
                            teName, MonitorConfigs.EXCLUDE_TEMPORARIES);
                    continue;
                }
            }
            endpoint.remove(Metrics.TopicEndpoint.VpnName);
            endpoint.remove(Metrics.TopicEndpoint.TopicEndpointName);
            metricPrinter.printMetrics(endpoint, basePrefix, serverName,
                    VPNS_PREFIX, vpnName,
                    Metrics.TopicEndpoint.PREFIX, teName);
        }
    }

    private void checkTopicEndpointRates(String serverName) {
        for(Map<String,Object> endpoint : svc.checkTopicEndpointRatesList()) {
            String vpnName= (String) endpoint.get(Metrics.TopicEndpoint.VpnName);
            if ( Helper.isExcluded(vpnName, exclusionPolicies.getVpnFilter(), exclusionPolicies.getVpnExclusionPolicy()) ) {
                logger.info("NOT writing rate metrics for any topic endpoints in the '{}' MsgVPN because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            String teName = (String) endpoint.get(Metrics.TopicEndpoint.TopicEndpointName);
            if ( Helper.isExcluded(teName, exclusionPolicies.getTopicEndpointFilter(), exclusionPolicies.getTopicEndpointExclusionPolicy()) ) {
                logger.info("NOT writing rate metrics for topic endpoint '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                        teName, MonitorConfigs.TOPIC_ENDPOINT_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_TOPIC_ENDPOINTS);
                continue;
            }
            if (exclusionPolicies.getExcludeTemporaries() ) {
                if (!getIsDurable(endpoint, Metrics.TopicEndpoint.IsDurable)) {
                    logger.info("NOT writing rate metrics for temporary topic-endpoint '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' configuration.",
                            teName, MonitorConfigs.EXCLUDE_TEMPORARIES);
                    continue;
                }
            }
            endpoint.remove(Metrics.TopicEndpoint.VpnName);
            endpoint.remove(Metrics.TopicEndpoint.TopicEndpointName);
            metricPrinter.printMetrics(endpoint, basePrefix, serverName,
                    VPNS_PREFIX, vpnName,
                    Metrics.TopicEndpoint.PREFIX, teName);
        }
    }

    private void checkBridges(String serverName) {
        for(Map<String,Object> bridge: svc.checkGlobalBridgeList()) {
            String vpnName= (String) bridge.get(Metrics.Bridge.VpnName);
            String bridgeName= (String) bridge.get(Metrics.Bridge.BridgeName);
            bridge.remove(Metrics.Bridge.VpnName);
            bridge.remove(Metrics.Bridge.BridgeName);
            metricPrinter.printMetrics(bridge, basePrefix, serverName,
                    VPNS_PREFIX, vpnName,
                    Metrics.Bridge.PREFIX, bridgeName);
        }
    }

    private Map<String,Object> deriveMetrics(Map<String,Object> serviceStats, Map<String,Object> redundancyStats, Map<String,Object> spoolStats) {
        Map<String,Object> metrics = new HashMap<>();

        // These metrics are used for all top-level dashboard indicators
        Integer svcPortUp = (Integer) serviceStats.get(Metrics.Service.SmfPortUp);
        //Integer redIsPrimary = (Integer) redundancyStats.get(Metrics.Redundancy.IsPrimary);
        Integer redIsActive = (Integer) redundancyStats.get(Metrics.Redundancy.IsActive);
        Integer spoolIsEnabled = (Integer) spoolStats.get(Metrics.MsgSpool.IsEnabled);
        Integer spoolIsActive = (Integer) spoolStats.get(Metrics.MsgSpool.IsActive);
        Integer spoolIsStandby = (Integer) spoolStats.get(Metrics.MsgSpool.IsStandby);
        Integer spoolDatapathUp = (Integer) spoolStats.get(Metrics.MsgSpool.IsDatapathUp);

        // Is Solace node UP (active is up as active, backup is up as backup)
        Integer dataSvcOk = 0;
        if (svcPortUp == 1) {
            if (redIsActive==1) {
                if (spoolIsActive==1 && spoolDatapathUp==1)
                    dataSvcOk = 1;
            }
            else {
                if (spoolIsStandby==1 && spoolDatapathUp==0)
                    dataSvcOk = 1;
            }
        }
        metrics.put(Metrics.Derived.DataSvcOk, dataSvcOk);

        // Is MsgSpool UP (active is up as ADActive, backup is synchronized)
        Integer spoolOk = 0;
        if (redIsActive==1) {
            if (spoolDatapathUp==1)
                spoolOk = 1;
        }
        else {
            if (spoolIsStandby==1 && spoolIsEnabled==1)
                spoolOk = 1;
        }
        metrics.put(Metrics.Derived.MsgSpoolOk, spoolOk);

        return metrics;
    }

    final private SempService svc;
    final private MetricPrinter metricPrinter;
    final private String basePrefix;
    final private ServerExclusionPolicies exclusionPolicies;
    private long startTimeMillis;
}
