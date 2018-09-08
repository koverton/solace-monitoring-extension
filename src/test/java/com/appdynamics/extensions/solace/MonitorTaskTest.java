package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.solace.semp.SempService;
import com.appdynamics.extensions.solace.semp.SempServiceFactory;
import com.appdynamics.extensions.solace.semp.Sempv1Connector;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Map;

public class MonitorTaskTest {
    //@Test
    public void sempTaskIntegrationTest() throws MalformedURLException {
        String mgmtUrl = "http://192.168.56.201:8080/SEMP";
        String adminUser = "admin";
        String adminPass = "admin";
        String displayName = "primary";
        Integer timeout = 5000;
        String baseMetricPrefix = "Server|Component:125573|Custom Metrics|Solace|Test";

        Sempv1Connector connector = new Sempv1Connector(
                mgmtUrl,
                adminUser,
                adminPass,
                displayName,
                timeout);
        ServerExclusionPolicies exclusionPolicies = new ServerExclusionPolicies(null);
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
