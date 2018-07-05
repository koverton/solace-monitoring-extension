package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.solace.semp.*;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Worker task for all Solace metrics gathering. Executes all desired metrics queries
 * on the SempService that is provided to it.
 */
class SolaceGlobalMonitorTask implements AMonitorTaskRunnable {
    private static final Logger logger = LoggerFactory.getLogger(SolaceGlobalMonitorTask.class);

    private static final char DELIM = '|';
    private static final String VPNS_PREFIX = "MsgVpns";

    SolaceGlobalMonitorTask(MetricWriteHelper metricWriter, String basePrefix,
                            ServerExclusionPolicies exclusionPolicies, SempService svc) {
        this.metricWriter      = metricWriter;
        this.basePrefix        = basePrefix;
        this.exclusionPolicies = exclusionPolicies;
        this.svc               = svc;
    }

    @Override
    public void run() {
        logger.debug("<SolaceGlobalMonitorTask.run>");
        final String serverName = svc.getDisplayName();
        final String metricPrefix = basePrefix  + serverName + DELIM;
        logger.debug("Configured metricPrefix: " + basePrefix);
        logger.debug("Full logging metricPrefix: " + metricPrefix);

        // Run Service check
        Map<String,Object> serviceStats = svc.checkGlobalServiceStatus();
        printMetrics(metricPrefix+Metrics.Service.PREFIX, serviceStats);

        // Run Redundancy check
        Map<String,Object> redundancyStats = svc.checkGlobalRedundancy();
        printMetrics(metricPrefix+Metrics.Redundancy.PREFIX, redundancyStats);

        // Run MsgSpool check
        Map<String,Object> spoolStats = svc.checkGlobalMsgSpoolStats();
        printMetrics(metricPrefix+Metrics.MsgSpool.PREFIX, spoolStats);

        // Run Statistics check
        Map<String,Object> clientStats = svc.checkGlobalStats();
        printMetrics(metricPrefix+Metrics.Statistics.PREFIX, clientStats);

        // Run queues checks
        checkQueues( metricPrefix );
        checkQueueRates( metricPrefix );

        // Run topic-endpoints checks
        checkTopicEndpoints( metricPrefix );
        checkTopicEndpointRates( metricPrefix );

        // Run bridges check
        checkBridges( metricPrefix );

        // Derive additional metrics
        Map<String,Object> derivedMetrics = deriveMetrics(serviceStats, redundancyStats, spoolStats);
        printMetrics(metricPrefix+Metrics.Derived.PREFIX, derivedMetrics);
        logger.debug("</SolaceGlobalMonitorTask.run>");
    }

    @Override
    public void onTaskComplete() {
    }

    private Boolean getIsDurable(Map<String,Object> map, String fieldName) {
        return 1 == ((Integer)map.getOrDefault(fieldName, 0));
    }

    private void checkQueues(String metricPrefix) {
        for(Map<String,Object> queue : svc.checkQueueList()) {
            String vpnName= (String) queue.get(Metrics.Queue.VpnName);
            if ( Helper.isExcluded(vpnName, exclusionPolicies.getVpnFilter(), exclusionPolicies.getVpnExclusionPolicy()) ) {
                logger.info("NOT writing metrics for any queues in the '{}' MsgVPN because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            String qname = (String) queue.get(Metrics.Queue.QueueName);
            if ( Helper.isExcluded(qname, exclusionPolicies.getQueueFilter(), exclusionPolicies.getQueueExclusionPolicy()) ) {
                logger.info("NOT writing metrics for queue '{}' because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        qname, MonitorConfigs.QUEUE_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_QUEUES);
                continue;
            }
            if (exclusionPolicies.getExcludeTemporaries() ) {
                if (!getIsDurable(queue, Metrics.Queue.IsDurable)) {
                    logger.info("NOT writing metrics for temporary queue '{}' because it did not match the exclusion policy. " +
                                    "If this was not expected, check your '{}' configuration.",
                            qname, MonitorConfigs.EXCLUDE_TEMPORARIES);
                    continue;
                }
            }
            String prefix = metricPrefix
                    + VPNS_PREFIX + DELIM + vpnName
                    + DELIM + Metrics.Queue.PREFIX + DELIM  + qname;
            queue.remove(Metrics.Queue.VpnName);
            queue.remove(Metrics.Queue.QueueName);
            printMetrics(prefix, queue);
        }
    }

    private void checkQueueRates(String metricPrefix) {
        for(Map<String,Object> queue : svc.checkQueueRatesList()) {
            String vpnName= (String) queue.get(Metrics.Queue.VpnName);
            if ( Helper.isExcluded(vpnName, exclusionPolicies.getVpnFilter(), exclusionPolicies.getVpnExclusionPolicy()) ) {
                logger.info("NOT writing rate metrics for any queues in the '{}' MsgVPN because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            String qname = (String) queue.get(Metrics.Queue.QueueName);
            if ( Helper.isExcluded(qname, exclusionPolicies.getQueueFilter(), exclusionPolicies.getQueueExclusionPolicy()) ) {
                logger.info("NOT writing rate metrics for queue '{}' because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        qname, MonitorConfigs.QUEUE_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_QUEUES);
                continue;
            }
            if (exclusionPolicies.getExcludeTemporaries() ) {
                if (!getIsDurable(queue, Metrics.Queue.IsDurable)) {
                    logger.info("NOT writing rate metrics for temporary queue '{}' because it did not match the exclusion policy. " +
                                    "If this was not expected, check your '{}' configuration.",
                            qname, MonitorConfigs.EXCLUDE_TEMPORARIES);
                    continue;
                }
            }
            String prefix = metricPrefix
                    + VPNS_PREFIX + DELIM + vpnName
                    + DELIM + Metrics.Queue.PREFIX + DELIM  + qname;
            queue.remove(Metrics.Queue.VpnName);
            queue.remove(Metrics.Queue.QueueName);
            printMetrics(prefix, queue);
        }
    }

    private void checkTopicEndpoints(String metricPrefix) {
        for(Map<String,Object> endpoint : svc.checkTopicEndpointList()) {
            String vpnName= (String) endpoint.get(Metrics.TopicEndpoint.VpnName);
            if ( Helper.isExcluded(vpnName, exclusionPolicies.getVpnFilter(), exclusionPolicies.getVpnExclusionPolicy()) ) {
                logger.info("NOT writing metrics for any topic endpoints in the '{}' MsgVPN because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            String teName = (String) endpoint.get(Metrics.TopicEndpoint.TopicEndpointName);
            if ( Helper.isExcluded(teName, exclusionPolicies.getTopicEndpointFilter(), exclusionPolicies.getTopicEndpointExclusionPolicy()) ) {
                logger.info("NOT writing metrics for topic endpoint '{}' because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        teName, MonitorConfigs.TOPIC_ENDPOINT_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_TOPIC_ENDPOINTS);
                continue;
            }
            if (exclusionPolicies.getExcludeTemporaries() ) {
                if (!getIsDurable(endpoint, Metrics.TopicEndpoint.IsDurable)) {
                    logger.info("NOT writing metrics for temporary topic-endpoint '{}' because it did not match the exclusion policy. " +
                                    "If this was not expected, check your '{}' configuration.",
                            teName, MonitorConfigs.EXCLUDE_TEMPORARIES);
                    continue;
                }
            }
            String prefix = metricPrefix
                    + VPNS_PREFIX + DELIM + vpnName
                    + DELIM + Metrics.TopicEndpoint.PREFIX + DELIM  + teName;
            endpoint.remove(Metrics.TopicEndpoint.VpnName);
            endpoint.remove(Metrics.TopicEndpoint.TopicEndpointName);
            printMetrics(prefix, endpoint);
        }
    }

    private void checkTopicEndpointRates(String metricPrefix) {
        for(Map<String,Object> endpoint : svc.checkTopicEndpointRatesList()) {
            String vpnName= (String) endpoint.get(Metrics.TopicEndpoint.VpnName);
            if ( Helper.isExcluded(vpnName, exclusionPolicies.getVpnFilter(), exclusionPolicies.getVpnExclusionPolicy()) ) {
                logger.info("NOT writing rate metrics for any topic endpoints in the '{}' MsgVPN because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            String teName = (String) endpoint.get(Metrics.TopicEndpoint.TopicEndpointName);
            if ( Helper.isExcluded(teName, exclusionPolicies.getTopicEndpointFilter(), exclusionPolicies.getTopicEndpointExclusionPolicy()) ) {
                logger.info("NOT writing rate metrics for topic endpoint '{}' because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        teName, MonitorConfigs.TOPIC_ENDPOINT_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_TOPIC_ENDPOINTS);
                continue;
            }
            if (exclusionPolicies.getExcludeTemporaries() ) {
                if (!getIsDurable(endpoint, Metrics.TopicEndpoint.IsDurable)) {
                    logger.info("NOT writing rate metrics for temporary topic-endpoint '{}' because it did not match the exclusion policy. " +
                                    "If this was not expected, check your '{}' configuration.",
                            teName, MonitorConfigs.EXCLUDE_TEMPORARIES);
                    continue;
                }
            }
            String prefix = metricPrefix
                    + VPNS_PREFIX + DELIM + vpnName
                    + DELIM + Metrics.TopicEndpoint.PREFIX + DELIM  + teName;
            endpoint.remove(Metrics.TopicEndpoint.VpnName);
            endpoint.remove(Metrics.TopicEndpoint.TopicEndpointName);
            printMetrics(prefix, endpoint);
        }
    }

    private void checkBridges(String metricPrefix) {
        for(Map<String,Object> bridge: svc.checkGlobalBridgeList()) {
            String vpnname= (String) bridge.get(Metrics.Bridge.VpnName);
            String bridgename= (String) bridge.get(Metrics.Bridge.BridgeName);
            String prefix = metricPrefix
                    + VPNS_PREFIX + DELIM + vpnname
                    + DELIM + Metrics.Bridge.PREFIX + DELIM  + bridgename;
            bridge.remove(Metrics.Bridge.VpnName);
            bridge.remove(Metrics.Bridge.BridgeName);
            printMetrics(prefix, bridge);
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

    private void printMetrics(String prefix, Map<String,Object> metrics) {
        for(Map.Entry<String,Object> entry : metrics.entrySet()) {
            printMetric(prefix, entry.getKey(), entry.getValue());
        }
    }

    private void printMetric(String metricPrefix, String metricName, Object metricValue) {

        String metricPath = metricPrefix + DELIM + metricName;
        if (metricValue instanceof Double)
            metricValue = ((Double)metricValue).longValue();

        //     public void printMetric(String metricPath, String metricValue, String aggregationType, String timeRollup, String clusterRollup)
        metricWriter.printMetric(metricPath, metricValue.toString(),
                MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION,
                MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE,
                MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE);


        logger.debug("Metric [{}/{}/{}] metric = {} = {}",
                MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION,
                MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE,
                MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE,
                metricPath, metricValue);
    }

    final private SempService svc;
    final private MetricWriteHelper metricWriter;
    final private String basePrefix;
    final private ServerExclusionPolicies exclusionPolicies;
}
