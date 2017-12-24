package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.solace.semp.SempService;
import com.appdynamics.extensions.util.MetricWriteHelper;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Worker task for all Solace metrics gathering. Executes all desired metrics queries
 * on the SempService that is provided to it.
 */
class SolaceGlobalMonitorTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SolaceGlobalMonitorTask.class);

    SolaceGlobalMonitorTask(MonitorConfiguration config, SempService svc) {
        this.config = config;
        this.svc    = svc;

        this.vpnFilter   = getConfigListOrNew(config, "excludeMsgVpns");
        this.queueFilter = getConfigListOrNew(config, "excludeQueues");
    }

    private List<String> getConfigListOrNew(MonitorConfiguration config, String key) {
        if (config.getConfigYml().containsKey(key))
            return (List<String>) config.getConfigYml().get(key);
        else
            return new ArrayList<>();
    }

    @Override
    public void run() {
        logger.debug("<SolaceGlobalMonitorTask.run>");
        String serverName = svc.getDisplayName();
        String metricPrefix = config.getConfigYml().get("metricPrefix")  + serverName + '|';

        // Run Service check
        Map<String,Object> serviceStats = svc.checkGlobalServiceStatus();
        printMetrics(metricPrefix+"service", serviceStats);

        // Run Redundancy check
        Map<String,Object> redundancyStats = svc.checkGlobalRedundancy();
        printMetrics(metricPrefix+"redundancy", redundancyStats);

        // Run MsgSpool check
        Map<String,Object> spoolStats = svc.checkGlobalMsgSpoolStats();
        printMetrics(metricPrefix+"msg-spool", spoolStats);

        // Run Statistics check
        Map<String,Object> clientStats = svc.checkGlobalStats();
        printMetrics(metricPrefix+"statistics", clientStats);

        // Run queues check
        for(Map<String,Object> queue : svc.checkQueueList()) {
            String vpnname= (String) queue.get("VpnName");
            if (this.vpnFilter.contains(vpnname)) {
                logger.info("NOT writing metrics for queues in the {} MsgVPN because it is in the excludedMsgVpns list.", vpnname);
                continue;
            }
            String qname = (String) queue.get("QueueName");
            if (this.queueFilter.contains(qname)) {
                logger.info("NOT writing metrics for queues in the {} queue because it is in the excludedQueues list.", qname);
                continue;
            }
            String prefix = metricPrefix
                    + "MsgVpns|" + vpnname
                    + "|Queues|"  + qname;
            queue.remove("VpnName");
            queue.remove("QueueName");
            printMetrics(prefix, queue);
        }

        // Run bridges check
        for(Map<String,Object> bridge: svc.checkGlobalBridgeList()) {
            String vpnname= (String) bridge.get("VpnName");
            String bridgename= (String) bridge.get("BridgeName");
            String prefix = metricPrefix
                    + "MsgVpns|" + vpnname
                    + "|Bridges|"  + bridgename;
            bridge.remove("VpnName");
            bridge.remove("BridgeName");
            printMetrics(prefix, bridge);
        }
        logger.debug("</SolaceGlobalMonitorTask.run>");
    }

    private void printMetrics(String prefix, Map<String,Object> metrics) {
        for(Map.Entry<String,Object> entry : metrics.entrySet()) {
            printMetric(prefix, entry.getKey(), entry.getValue());
        }
    }

    private void printMetric(String metricPrefix, String metricName, Object metricValue) {
        MetricWriteHelper metricWriter = config.getMetricWriter();

        String metricPath = metricPrefix + '|' + metricName;
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
