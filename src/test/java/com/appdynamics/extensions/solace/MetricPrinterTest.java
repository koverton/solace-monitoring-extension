package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.solace.semp.Metrics;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MetricPrinterTest {

    private Map<String,Object> testMap() {
        Map<String,Object> map = new HashMap<>();
        map.put(Metrics.Derived.DataSvcOk, 1);
        map.put(Metrics.Derived.MsgSpoolOk, 1);
        return map;
    }

    @Test
    public void testNullPrinter() {
        ADMetricPrinter printer = new ADMetricPrinter(null);
        printer.printMetrics(testMap(), "test/path/1");
    }
}
