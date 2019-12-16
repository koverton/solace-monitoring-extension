package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.solace.semp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.appdynamics.extensions.solace.DerivedMetricsLogic.deriveMetrics;

/**
 * Worker task for all Solace metrics gathering. Executes all desired metrics queries
 * on the SempService that is provided to it.
 */
class SolaceGlobalMonitorTask implements AMonitorTaskRunnable {
    private static final Logger logger = LoggerFactory.getLogger(SolaceGlobalMonitorTask.class);

    SolaceGlobalMonitorTask(MetricPrinter metricPrinter, String basePrefix,
                            ServerConfigs serverConfigs, SempService svc) {
        this.metricPrinter     = metricPrinter;
        this.basePrefix        = basePrefix;
        this.serverConfigs     = serverConfigs;
        this.svc               = svc;
        logger.info("SolaceGlobalMonitorTask monitoring created.");
    }

    @Override
    public void run() {
        logger.trace("<SolaceGlobalMonitorTask.run>");

        startTimeMillis = System.currentTimeMillis();
        logger.info("SolaceGlobalMonitorTask started at {}.", startTimeMillis);
        serverConfigs.log();

        final String serverName = svc.getDisplayName();
        logger.info("Configured metricPrefix: {}, ServerName {}", basePrefix, serverName);

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

        // Run topic-endpoints checks
        checkTopicEndpoints( serverName );

        // Run bridges check
        checkBridges( serverName );

        // Derive additional metrics
        Map<String,Object> derivedMetrics = deriveMetrics(serverConfigs.getRedundancyModel(), serviceStats, redundancyStats, spoolStats);
        metricPrinter.printMetrics(derivedMetrics, basePrefix, serverName, Metrics.Derived.PREFIX);

        logger.trace("</SolaceGlobalMonitorTask.run>");
    }

    @Override
    public void onTaskComplete() {
        double seconds = (System.currentTimeMillis()-this.startTimeMillis)/1000.0;
        logger.info("SolaceGlobalMonitorTask monitoring run completed in {} seconds.", seconds);
    }

    private Boolean getIsDurable(Map<String,Object> map, String fieldName) {
        return 1 == ((Integer)map.getOrDefault(fieldName, 0));
    }

    private void checkMsgVpns(String serverName) {
        // vpn stats
        for(Map<String,Object> vpn : svc.checkMsgVpnList())
            checkMsgVpn(vpn, serverName);
        // vpn spool stats
        for(Map<String,Object> vpn : svc.checkMsgVpnSpoolList())
            checkMsgVpn(vpn, serverName);
    }

    private void checkMsgVpn(Map<String,Object> vpn, String serverName) {
        String vpnName= (String) vpn.get(Metrics.Vpn.VpnName);
        if ( Helper.isExcluded(vpnName, serverConfigs.getVpnFilter(), serverConfigs.getVpnExclusionPolicy()) ) {
            logger.info("NOT writing metrics for the '{}' MsgVPN because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                    vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
            return;
        }
        vpn.remove(Metrics.Vpn.VpnName);
        metricPrinter.printMetrics(vpn, basePrefix, serverName, Metrics.Vpn.PREFIX, vpnName);
    }

    private void checkQueues(String serverName) {
        // HACK -- stats and rates queries do NOT include durability in the results
        Map<String,Object> durableQueueMap = new HashMap<>();
        for(Map<String,Object> queue : svc.checkQueueList()) {
            String vpnName= (String) queue.get(Metrics.Queue.VpnName);
            String qname = (String) queue.get(Metrics.Queue.QueueName);
            if ( checkQueue(queue, serverName) ) {
                Integer isDurable = (Integer)queue.getOrDefault(Metrics.Queue.IsDurable, 0);
                durableQueueMap.put(vpnName+"^"+qname, isDurable);
            }
        }
        if (!serverConfigs.getExcludeExtendedStats()) {
            for(Map<String,Object> queue : svc.checkQueueStatsList()) {
                String vpnName= (String) queue.get(Metrics.Queue.VpnName);
                String qname = (String) queue.get(Metrics.Queue.QueueName);
                Integer isDurable = (Integer)durableQueueMap.get(vpnName+"^"+qname);
                queue.put(Metrics.Queue.IsDurable, isDurable);
                checkQueue(queue, serverName);
            }
            for (Map<String, Object> queue : svc.checkQueueRatesList()) {
                String vpnName= (String) queue.get(Metrics.Queue.VpnName);
                String qname = (String) queue.get(Metrics.Queue.QueueName);
                Integer isDurable = (Integer)durableQueueMap.get(vpnName+"^"+qname);
                queue.put(Metrics.Queue.IsDurable, isDurable);
                checkQueue(queue, serverName);
            }
        }
        else {
            logger.info("SKIPPING extended queue and endpoint stats for " + serverName);
        }
    }

    private boolean checkQueue(Map<String,Object> queue, String serverName) {
        String vpnName= (String) queue.get(Metrics.Queue.VpnName);
        if ( Helper.isExcluded(vpnName, serverConfigs.getVpnFilter(), serverConfigs.getVpnExclusionPolicy()) ) {
            logger.info("NOT writing metrics for any queues in the '{}' MsgVPN because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                    vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
            return false;
        }
        String qname = (String) queue.get(Metrics.Queue.QueueName);
        if ( Helper.isExcluded(qname, serverConfigs.getQueueFilter(), serverConfigs.getQueueExclusionPolicy()) ) {
            logger.info("NOT writing metrics for queue '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                    qname, MonitorConfigs.QUEUE_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_QUEUES);
            return false;
        }
        if (serverConfigs.getExcludeTemporaries() ) {
            if (!getIsDurable(queue, Metrics.Queue.IsDurable)) {
                logger.info("NOT writing metrics for temporary queue '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' configuration.",
                        qname, MonitorConfigs.EXCLUDE_TEMPORARIES);
                return false;
            }
        }
        queue.remove(Metrics.Queue.VpnName);
        queue.remove(Metrics.Queue.QueueName);
        metricPrinter.printMetrics(queue, basePrefix, serverName, Metrics.Vpn.PREFIX, vpnName, Metrics.Queue.PREFIX, qname);
        return true;
    }

    private void checkTopicEndpoints(String serverName) {
        // HACK -- stats and rates queries do NOT include durability in the results
        Map<String,Object> durableEndpointsMap = new HashMap<>();
        for(Map<String,Object> endpoint : svc.checkTopicEndpointList()) {
            String vpnName= (String) endpoint.get(Metrics.TopicEndpoint.VpnName);
            String ename = (String) endpoint.get(Metrics.TopicEndpoint.TopicEndpointName);
            if ( checkTopicEndpoint(endpoint, serverName) ) {
                Integer isDurable = (Integer)endpoint.getOrDefault(Metrics.TopicEndpoint.IsDurable, 0);
                durableEndpointsMap.put(vpnName+"^"+ename, isDurable);
            }
        }
        if (!serverConfigs.getExcludeExtendedStats()) {
            for(Map<String,Object> endpoint : svc.checkTopicEndpointStatsList()) {
                String vpnName= (String) endpoint.get(Metrics.TopicEndpoint.VpnName);
                String ename = (String) endpoint.get(Metrics.TopicEndpoint.TopicEndpointName);
                Integer isDurable = (Integer)durableEndpointsMap.get(vpnName+"^"+ ename);
                endpoint.put(Metrics.TopicEndpoint.IsDurable, isDurable);
                checkTopicEndpoint(endpoint, serverName);
            }
            for (Map<String, Object> endpoint : svc.checkTopicEndpointRatesList()) {
                String vpnName= (String) endpoint.get(Metrics.TopicEndpoint.VpnName);
                String ename = (String) endpoint.get(Metrics.TopicEndpoint.TopicEndpointName);
                Integer isDurable = (Integer)durableEndpointsMap.get(vpnName+"^"+ename);
                endpoint.put(Metrics.TopicEndpoint.IsDurable, isDurable);
                checkTopicEndpoint(endpoint, serverName);
            }
        }
        else {
            logger.info("SKIPPING extended queue and endpoint stats for " + serverName);
        }
    }

    private boolean checkTopicEndpoint(Map<String,Object> endpoint, String serverName) {
        String vpnName= (String) endpoint.get(Metrics.TopicEndpoint.VpnName);
        if ( Helper.isExcluded(vpnName, serverConfigs.getVpnFilter(), serverConfigs.getVpnExclusionPolicy()) ) {
            logger.info("NOT writing metrics for any topic endpoints in the '{}' MsgVPN because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                    vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
            return false;
        }
        String teName = (String) endpoint.get(Metrics.TopicEndpoint.TopicEndpointName);
        if ( Helper.isExcluded(teName, serverConfigs.getTopicEndpointFilter(), serverConfigs.getTopicEndpointExclusionPolicy()) ) {
            logger.info("NOT writing metrics for topic endpoint '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                    teName, MonitorConfigs.TOPIC_ENDPOINT_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_TOPIC_ENDPOINTS);
            return false;
        }
        if (serverConfigs.getExcludeTemporaries() ) {
            if (!getIsDurable(endpoint, Metrics.TopicEndpoint.IsDurable)) {
                logger.info("NOT writing metrics for temporary topic-endpoint '{}' because it did not match the exclusion policy. If this was not expected, check your '{}' configuration.",
                        teName, MonitorConfigs.EXCLUDE_TEMPORARIES);
                return false;
            }
        }
        endpoint.remove(Metrics.TopicEndpoint.VpnName);
        endpoint.remove(Metrics.TopicEndpoint.TopicEndpointName);
        metricPrinter.printMetrics(endpoint, basePrefix, serverName,
                Metrics.Vpn.PREFIX, vpnName,
                Metrics.TopicEndpoint.PREFIX, teName);
        return true;
    }

    private void checkBridges(String serverName) {
        for(Map<String,Object> bridge: svc.checkGlobalBridgeList()) {
            String vpnName= (String) bridge.get(Metrics.Bridge.VpnName);
            if ( Helper.isExcluded(vpnName, serverConfigs.getVpnFilter(), serverConfigs.getVpnExclusionPolicy()) ) {
                logger.info("NOT writing metrics for bridges in the '{}' MsgVPN because it did not match the exclusion policy. If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            String bridgeName= (String) bridge.get(Metrics.Bridge.BridgeName);
            bridge.remove(Metrics.Bridge.VpnName);
            bridge.remove(Metrics.Bridge.BridgeName);
            metricPrinter.printMetrics(bridge, basePrefix, serverName,
                    Metrics.Vpn.PREFIX, vpnName,
                    Metrics.Bridge.PREFIX, bridgeName);
        }
    }

    final private SempService svc;
    final private MetricPrinter metricPrinter;
    final private String basePrefix;
    final private ServerConfigs serverConfigs;
    private long startTimeMillis;
}
