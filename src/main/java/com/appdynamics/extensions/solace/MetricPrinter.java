package com.appdynamics.extensions.solace;

import java.util.Map;

/**
 * The MetricPrinter abstraction separates the specific monitoring system
 * method from the SolaceGlobalMonitorTask so they can be tested independently.
 */
public interface MetricPrinter {

    /**
     * Print all the metrics for a set, prefixing each according to the provided hierarchical metric path.
     * @param metrics a container of of name-&gt; value metrics with a hierarchical metric path to prefix them all.
     * @param metricPath an array of items making up the metric path to this collection, to be prefixed to each
     *                   metric as the specific monitoring system requires.
     */
    void printMetrics(Map<String,Object> metrics, String... metricPath);

}
