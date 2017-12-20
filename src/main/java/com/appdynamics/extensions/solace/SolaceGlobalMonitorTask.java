package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.solace.semp.SempService;
import com.appdynamics.extensions.util.MetricWriteHelper;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

class SolaceGlobalMonitorTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SolaceGlobalMonitorTask.class);

    SolaceGlobalMonitorTask(MonitorConfiguration configuration, SempService svc) {
        this.configuration = configuration;
        this.svc = svc;
    }

    @Override
    public void run() {
        logger.debug("<SolaceGlobalMonitorTask.run>");
        String serverName = svc.getDisplayName();

        String metricPrefix = configuration.getConfigYml().get("metricPrefix")  + serverName + '|';

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
            String prefix = metricPrefix + "Queues|"
                    + queue.get("VpnName") + ":"
                    + queue.get("QueueName");
            queue.remove("VpnName");
            queue.remove("QueueName");
            printMetrics(prefix, queue);
        }
        logger.debug("</SolaceGlobalMonitorTask.run>");
    }

    private void printMetrics(String prefix, Map<String,Object> metrics) {
        for(Map.Entry<String,Object> entry : metrics.entrySet()) {
            printMetric(prefix, entry.getKey(), entry.getValue());
        }
    }

    private void printMetric(String metricPrefix, String metricName, Object metricValue) {

        String aggregation = MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION;
        String timeRollup = MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE;
        String cluster = MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE;
        String metricPath = metricPrefix + '|' + metricName;
        MetricWriteHelper metricWriter = configuration.getMetricWriter();
        if (metricValue instanceof Double)
            metricValue = ((Double)metricValue).longValue();
        metricWriter.printMetric(metricPath, metricValue.toString(), aggregation, timeRollup, cluster);


        logger.debug("Metric [{}/{}/{}] metric = {} = {}",
                aggregation, timeRollup, cluster,
                metricPath, metricValue);
    }

    private MonitorConfiguration configuration;
    private SempService svc;
}
