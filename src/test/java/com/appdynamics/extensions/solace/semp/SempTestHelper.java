package com.appdynamics.extensions.solace.semp;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class SempTestHelper {
    static public void noNullValuesCheck(List<Map<String,Object>> listOfMetrics) {
        for(Map<String,Object> metrics : listOfMetrics) {
            noNullValuesCheck(metrics);
        }

    }
    static public void noNullValuesCheck(Map<String, Object> metrics) {
        for(Map.Entry<String,Object> metric : metrics.entrySet()) {
            assertNotNull("KEY:{"+metric.getKey()+"} SHOULD NOT BE NULL!", metric.getKey());
            assertNotNull("KEY:{"+metric.getKey()+"} VALUE SHOULD NOT BE NULL!", metric.getValue());
        }
    }

}
