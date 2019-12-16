package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.MetricWriteHelper;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * <p>Implementation of {@link MetricPrinter}
 * for Standard logfiles.
 */
public class LocalMetricPrinter implements MetricPrinter
{
    private static final Logger logger = LoggerFactory.getLogger(LocalMetricPrinter.class);
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

        logger.info("{}|{}", metricPath, metricValue.toString());
    }

    private String makePrefix(String... fields) {
        StringBuilder sb = new StringBuilder(512);
        for (String field : fields) {
            sb.append(field);
            sb.append(DELIM);
        }
        return sb.toString();
    }
}
