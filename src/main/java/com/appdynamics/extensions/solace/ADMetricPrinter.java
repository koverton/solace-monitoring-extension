package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.MetricWriteHelper;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * <p>Implementation of {@link com.appdynamics.extensions.solace.MetricPrinter}
 * for AppDynamics MachineAgent plugin.
 */
public class ADMetricPrinter implements MetricPrinter
{
    private static final Logger logger = LoggerFactory.getLogger(ADMetricPrinter.class);
    private static final char DELIM = '|';

    @Override
    public void printMetrics(Map<String,Object> metrics, String... metricPath) {
        String prefix = makePrefix(metricPath);
        for(Map.Entry<String,Object> entry : metrics.entrySet()) {
            printMetric(prefix, entry.getKey(), entry.getValue());
        }
    }

    private void printMetric(String metricPrefix, String metricName, Object metricValue) {
        String metricPath = metricPrefix + metricName;
        if (metricValue == null) {
            logger.warn("SKIPPING null metric value for metric: {}",
                    metricPath);
            return;
        }
        if (metricValue instanceof Double)
            metricValue = ((Double)metricValue).longValue();

        if (metricWriter != null) {
            // void printMetric(String metricPath, String metricValue, String aggregationType, String timeRollup, String clusterRollup)
            metricWriter.printMetric(metricPath, metricValue.toString(),
                    MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION,
                    MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE,
                    MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE);
        }
        else {
            logger.warn("No AD MetricWriter available so skipping.");
        }

        logger.debug("Metric [{}/{}/{}] metric = {} = {}",
                MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION,
                MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE,
                MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE,
                metricPath, metricValue);
    }

    private String makePrefix(String... fields) {
        StringBuilder sb = new StringBuilder(512);
        for(int i = 0; i < fields.length; i++) {
            sb.append(fields[i]);
            sb.append(DELIM);
        }
        return sb.toString();
    }


    ADMetricPrinter(MetricWriteHelper metricWriter) {
        this.metricWriter = metricWriter;
    }

    final private MetricWriteHelper metricWriter;
}
