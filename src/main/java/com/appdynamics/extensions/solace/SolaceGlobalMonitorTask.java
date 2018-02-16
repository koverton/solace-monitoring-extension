package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.solace.semp.*;
import com.appdynamics.extensions.util.MetricWriteHelper;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Worker task for all Solace metrics gathering. Executes all desired metrics queries
 * on the SempService that is provided to it.
 */
class SolaceGlobalMonitorTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SolaceGlobalMonitorTask.class);

    private static final char DELIM = '|';
    private static final String VPNS_PREFIX = "MsgVpns";

    SolaceGlobalMonitorTask(MonitorConfiguration config, SempService svc) {
        this.config = config;
        this.svc    = svc;

        this.vpnFilter   = getConfigListOrNew(config, MonitorConfigs.EXCLUDE_MSG_VPNS);
        if (logger.isDebugEnabled()) {
            for (String excludedVpn : vpnFilter)
                logger.debug("Excluded VPN: {}", excludedVpn);
        }
        this.queueFilter = getConfigListOrNew(config, MonitorConfigs.EXCLUDE_QUEUES);
        if (logger.isDebugEnabled()) {
            for (String excludedQueue : vpnFilter)
                logger.debug("Excluded Queue: {}", excludedQueue);
        }
    }

    private List<String> getConfigListOrNew(MonitorConfiguration config, String key) {
        if (config.getConfigYml().containsKey(key))
            return (List<String>) config.getConfigYml().get(key);
        else {
            logger.warn("No list found configured for key [{}]", key);
            return new ArrayList<>();
        }
    }

    @Override
    public void run() {
        logger.debug("<SolaceGlobalMonitorTask.run>");
        String serverName = svc.getDisplayName();
        String metricPrefix = config.getConfigYml().get(MonitorConfigs.METRIC_PREFIX)  + serverName + DELIM;
        logger.debug("Configured metricPrefix: " + config.getConfigYml().get(MonitorConfigs.METRIC_PREFIX));
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
        for(Map<String,Object> queue : svc.checkQueueList()) {
            String vpnname= (String) queue.get(QueueMetrics.VpnName);
            if (this.vpnFilter.contains(vpnname)) {
                logger.info("NOT writing metrics for queues in the {} MsgVPN because it is in the {} list.",
                        MonitorConfigs.EXCLUDE_MSG_VPNS, vpnname);
                continue;
            }
            String qname = (String) queue.get(QueueMetrics.QueueName);
            if (this.queueFilter.contains(qname)) {
                logger.info("NOT writing metrics for queues in the {} queue because it is in the {} list.",
                        MonitorConfigs.EXCLUDE_QUEUES, qname);
                continue;
            }
            String prefix = metricPrefix
                    + VPNS_PREFIX + DELIM + vpnname
                    + DELIM + QueueMetrics.PREFIX + DELIM  + qname;
            queue.remove(QueueMetrics.VpnName);
            queue.remove(QueueMetrics.QueueName);
            printMetrics(prefix, queue);
        }

        // Run bridges check
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

        // Derive additional metrics
        Map<String,Object> derivedMetrics = deriveMetrics(serviceStats, redundancyStats, spoolStats);
        printMetrics(metricPrefix+DerivedMetrics.PREFIX, derivedMetrics);
        logger.debug("</SolaceGlobalMonitorTask.run>");
    }

    private Map<String,Object> deriveMetrics(Map<String,Object> serviceStats, Map<String,Object> redundancyStats, Map<String,Object> spoolStats) {
        Map<String,Object> metrics = new HashMap<>();

        // These metrics are used for all top-level dashboard indicators
        Integer svcPortUp = (Integer) serviceStats.get(ServiceMetrics.SmfPortUp);
        Integer redIsPrimary = (Integer) redundancyStats.get(RedundancyMetrics.IsPrimary);
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
        MetricWriteHelper metricWriter = config.getMetricWriter();

        String metricPath = metricPrefix + DELIM + metricName;
        if (metricValue instanceof Double)
            metricValue = ((Double)metricValue).longValue();

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

    final private MonitorConfiguration config;
    final private SempService svc;
    final private List<String> vpnFilter;
    final private List<String> queueFilter;
}
