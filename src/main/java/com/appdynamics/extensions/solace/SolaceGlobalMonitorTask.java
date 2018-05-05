package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.solace.semp.*;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

import com.appdynamics.extensions.solace.MonitorConfigs.ExclusionPolicy;

/**
 * Worker task for all Solace metrics gathering. Executes all desired metrics queries
 * on the SempService that is provided to it.
 */
class SolaceGlobalMonitorTask implements AMonitorTaskRunnable {
    private static final Logger logger = LoggerFactory.getLogger(SolaceGlobalMonitorTask.class);

    private static final char DELIM = '|';
    private static final String VPNS_PREFIX = "MsgVpns";

    SolaceGlobalMonitorTask(MetricWriteHelper metricWriter, String basePrefix,
                            MonitorConfigs.ExclusionPolicy vpnExclusionPolicy, List<Pattern> vpnFilter,
                            ExclusionPolicy queueExclusionPolicy, List<Pattern> queueFilter,
                            ExclusionPolicy topicEndpointExclusionPolicy, List<Pattern> topicEndpointFilter,
                            SempService svc) {
        this.metricWriter        = metricWriter;
        this.basePrefix          = basePrefix;
        this.vpnExclusionPolicy  = vpnExclusionPolicy;
        this.vpnFilter           = vpnFilter;
        this.queueExclusionPolicy= queueExclusionPolicy;
        this.queueFilter         = queueFilter;
        this.topicEndpointExclusionPolicy= topicEndpointExclusionPolicy;
        this.topicEndpointFilter = topicEndpointFilter;
        this.svc                 = svc;
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
        printMetrics(metricPrefix+ServiceMetrics.PREFIX, serviceStats);

        // Run Redundancy check
        Map<String,Object> redundancyStats = svc.checkGlobalRedundancy();
        printMetrics(metricPrefix+RedundancyMetrics.PREFIX, redundancyStats);

        // Run MsgSpool check
        Map<String,Object> spoolStats = svc.checkGlobalMsgSpoolStats();
        printMetrics(metricPrefix+MsgSpoolMetrics.PREFIX, spoolStats);

        // Run Statistics check
        Map<String,Object> clientStats = svc.checkGlobalStats();
        printMetrics(metricPrefix+StatisticalMetrics.PREFIX, clientStats);

        // Run queues check
        checkQueues( metricPrefix );

        // Run topic-endpoints check
        checkTopicEndpoints( metricPrefix );

        // Run bridges check
        checkBridges( metricPrefix );

        // Derive additional metrics
        Map<String,Object> derivedMetrics = deriveMetrics(serviceStats, redundancyStats, spoolStats);
        printMetrics(metricPrefix+DerivedMetrics.PREFIX, derivedMetrics);
        logger.debug("</SolaceGlobalMonitorTask.run>");
    }

    @Override
    public void onTaskComplete() {
    }

    private void checkQueues(String metricPrefix) {
        for(Map<String,Object> queue : svc.checkQueueList()) {
            String vpnName= (String) queue.get(QueueMetrics.VpnName);
            if ( Helper.isExcluded(vpnName, this.vpnFilter, vpnExclusionPolicy) ) {
                logger.info("NOT writing metrics for any queues in the '{}' MsgVPN because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            String qname = (String) queue.get(QueueMetrics.QueueName);
            if ( Helper.isExcluded(qname, this.queueFilter, queueExclusionPolicy) ) {
                logger.info("NOT writing metrics for queue '{}' because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        qname, MonitorConfigs.QUEUE_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_QUEUES);
                continue;
            }
            String prefix = metricPrefix
                    + VPNS_PREFIX + DELIM + vpnName
                    + DELIM + QueueMetrics.PREFIX + DELIM  + qname;
            queue.remove(QueueMetrics.VpnName);
            queue.remove(QueueMetrics.QueueName);
            printMetrics(prefix, queue);
        }
    }

    private void checkTopicEndpoints(String metricPrefix) {
        for(Map<String,Object> endpoint : svc.checkTopicEndpointList()) {
            String vpnName= (String) endpoint.get(TopicEndpointMetrics.VpnName);
            if ( Helper.isExcluded(vpnName, this.vpnFilter, vpnExclusionPolicy) ) {
                logger.info("NOT writing metrics for any topic endpoints in the '{}' MsgVPN because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        vpnName, MonitorConfigs.VPN_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_MSG_VPNS);
                continue;
            }
            String teName = (String) endpoint.get(TopicEndpointMetrics.TopicEndpointName);
            if ( Helper.isExcluded(teName, this.topicEndpointFilter, topicEndpointExclusionPolicy) ) {
                logger.info("NOT writing metrics for topic endpoint '{}' because it did not match the exclusion policy. " +
                                "If this was not expected, check your '{}' and '{}' configurations.",
                        teName, MonitorConfigs.TOPIC_ENDPOINT_EXCLUSION_POLICY, MonitorConfigs.EXCLUDE_TOPIC_ENDPOINTS);
                continue;
            }
            String prefix = metricPrefix
                    + VPNS_PREFIX + DELIM + vpnName
                    + DELIM + TopicEndpointMetrics.PREFIX + DELIM  + teName;
            endpoint.remove(TopicEndpointMetrics.VpnName);
            endpoint.remove(TopicEndpointMetrics.TopicEndpointName);
            printMetrics(prefix, endpoint);
        }
    }

    private void checkBridges(String metricPrefix) {
        for(Map<String,Object> bridge: svc.checkGlobalBridgeList()) {
            String vpnname= (String) bridge.get(BridgeMetrics.VpnName);
            String bridgename= (String) bridge.get(BridgeMetrics.BridgeName);
            String prefix = metricPrefix
                    + VPNS_PREFIX + DELIM + vpnname
                    + DELIM + BridgeMetrics.PREFIX + DELIM  + bridgename;
            bridge.remove(BridgeMetrics.VpnName);
            bridge.remove(BridgeMetrics.BridgeName);
            printMetrics(prefix, bridge);
        }
    }

    private Map<String,Object> deriveMetrics(Map<String,Object> serviceStats, Map<String,Object> redundancyStats, Map<String,Object> spoolStats) {
        Map<String,Object> metrics = new HashMap<>();

        // These metrics are used for all top-level dashboard indicators
        Integer svcPortUp = (Integer) serviceStats.get(ServiceMetrics.SmfPortUp);
        //Integer redIsPrimary = (Integer) redundancyStats.get(RedundancyMetrics.IsPrimary);
        Integer redIsActive = (Integer) redundancyStats.get(RedundancyMetrics.IsActive);
        Integer spoolIsEnabled = (Integer) spoolStats.get(MsgSpoolMetrics.IsEnabled);
        Integer spoolIsActive = (Integer) spoolStats.get(MsgSpoolMetrics.IsActive);
        Integer spoolIsStandby = (Integer) spoolStats.get(MsgSpoolMetrics.IsStandby);
        Integer spoolDatapathUp = (Integer) spoolStats.get(MsgSpoolMetrics.IsDatapathUp);

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
        metrics.put(DerivedMetrics.DataSvcOk, dataSvcOk);

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
        metrics.put(DerivedMetrics.MsgSpoolOk, spoolOk);

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
    final private ExclusionPolicy vpnExclusionPolicy;
    final private List<Pattern> vpnFilter;
    final private ExclusionPolicy queueExclusionPolicy;
    final private List<Pattern> queueFilter;
    final private ExclusionPolicy topicEndpointExclusionPolicy;
    final private List<Pattern> topicEndpointFilter;
}
