package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.solace.semp.Metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Helper class to encapsulate all derived metrics. Derived metrics are higher-order
 * metrics calculated from several other metrics. These derived metrics are version-agnostic
 * in that they depend on metrics that exist in all supported Solace schema versions.
 */
public class DerivedMetricsLogic {
    public static Map<String,Object> deriveMetrics(
            MonitorConfigs.RedundancyModel redundancyModel,
            Map<String,Object> serviceStats,
            Map<String,Object> redundancyStats,
            Map<String,Object> spoolStats) {

        if (redundancyModel.equals(MonitorConfigs.RedundancyModel.REDUNDANT))
            return deriveRedundantMetrics(serviceStats, redundancyStats, spoolStats);

        return deriveStandaloneMetrics(serviceStats, spoolStats);
    }

    private static Map<String,Object> deriveRedundantMetrics(Map<String,Object> serviceStats, Map<String,Object> redundancyStats, Map<String,Object> spoolStats) {
        Map<String,Object> metrics = new HashMap<>();

        // These metrics are used for all top-level dashboard indicators
        Integer svcPortUp = (Integer) serviceStats.get(Metrics.Service.SmfPortUp);
        //Integer redIsPrimary = (Integer) redundancyStats.get(Metrics.Redundancy.IsPrimary);
        Integer redIsActive = (Integer) redundancyStats.get(Metrics.Redundancy.IsActive);
        Integer spoolIsEnabled = (Integer) spoolStats.get(Metrics.MsgSpool.IsEnabled);
        Integer spoolIsActive = (Integer) spoolStats.get(Metrics.MsgSpool.IsActive);
        Integer spoolIsStandby = (Integer) spoolStats.get(Metrics.MsgSpool.IsStandby);
        Integer spoolDatapathUp = (Integer) spoolStats.get(Metrics.MsgSpool.IsDatapathUp);

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
        metrics.put(Metrics.Derived.DataSvcOk, dataSvcOk);

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
        metrics.put(Metrics.Derived.MsgSpoolOk, spoolOk);

        return metrics;
    }



    private static Map<String,Object> deriveStandaloneMetrics(Map<String,Object> serviceStats, Map<String,Object> spoolStats) {
        Map<String,Object> metrics = new HashMap<>();

        // These metrics are used for all top-level dashboard indicators
        Integer svcPortUp = (Integer) serviceStats.get(Metrics.Service.SmfPortUp);
        Integer spoolIsActive = (Integer) spoolStats.get(Metrics.MsgSpool.IsActive);
        Integer spoolDatapathUp = (Integer) spoolStats.get(Metrics.MsgSpool.IsDatapathUp);

        // Is Solace node UP (active is up as active, backup is up as backup)
        Integer dataSvcOk = 0;
        if (svcPortUp == 1) {
            if (spoolIsActive==1 && spoolDatapathUp==1)
                dataSvcOk = 1;
        }
        metrics.put(Metrics.Derived.DataSvcOk, dataSvcOk);

        // Is MsgSpool UP (is up as ADActive)
        Integer spoolOk = 0;
        if (spoolDatapathUp==1)
            spoolOk = 1;
        metrics.put(Metrics.Derived.MsgSpoolOk, spoolOk);

        return metrics;
    }
}
