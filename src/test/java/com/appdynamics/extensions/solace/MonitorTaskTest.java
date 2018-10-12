package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.solace.semp.*;
import org.junit.Test;

import java.util.Map;

public class MonitorTaskTest {
    @Test
    public void sempTaskIntegrationTest() {
        String baseMetricPrefix = "Server|Component:125573|Custom Metrics|Solace|Test";

        SempConnector connector = new MockSempConnector("7_2_2");
        ServerConfigs exclusionPolicies = new ServerConfigs(null);
        SempService sempService = SempServiceFactory.createSempService(connector, exclusionPolicies);
        new SolaceGlobalMonitorTask(
                (Map<String, Object> metrics, String... metricPath) -> {
                    String prefix = String.join("|", metricPath);
                    for(Map.Entry<String,Object> entry : metrics.entrySet()) {
                        System.out.println(prefix + '|' + entry.getKey() + " ==> " + entry.getValue());
                    }
                },
                baseMetricPrefix,
                exclusionPolicies,
                sempService)
                .run();
    }

    @Test
    public void sempDerivedMetricsTest() {
        String baseMetricPrefix = "Server|Component:125573|Custom Metrics|Solace|Test";

        SempConnector connector = new MockSempConnector("8_5_0");
        ServerConfigs exclusionPolicies = new ServerConfigs(null);
        SempService sempService = SempServiceFactory.createSempService(connector, exclusionPolicies);
        new SolaceGlobalMonitorTask(
                (Map<String, Object> metrics, String... metricPath) -> {
                    String prefix = String.join("|", metricPath);
                    for(Map.Entry<String,Object> entry : metrics.entrySet()) {
                        System.out.println(prefix + '|' + entry.getKey() + " ==> " + entry.getValue());
                    }
                },
                baseMetricPrefix,
                exclusionPolicies,
                sempService)
                .run();
    }
}
