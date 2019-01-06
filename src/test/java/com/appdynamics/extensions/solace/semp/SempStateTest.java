package com.appdynamics.extensions.solace.semp;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * These tests should work across all versions because they
 * test the resulting metrics which must all agree.
 */
public class SempStateTest {

    public static void redundancyTest(Map<String,Object> redundancy) {
        assertNotNull(redundancy);
        assertNotNull(redundancy.get(Metrics.Redundancy.ConfiguredStatus));
        assertEquals(1, redundancy.get(Metrics.Redundancy.ConfiguredStatus));
        assertNotNull(redundancy.get(Metrics.Redundancy.OperationalStatus));
        assertEquals(1, redundancy.get(Metrics.Redundancy.OperationalStatus));
        assertNotNull(redundancy.get(Metrics.Redundancy.IsActive));
    }
    public static void standaloneTest(Map<String,Object> redundancy) {
        assertNotNull(redundancy);
        assertNotNull(redundancy.get(Metrics.Redundancy.ConfiguredStatus));
        assertEquals(0, redundancy.get(Metrics.Redundancy.ConfiguredStatus));
        assertNotNull(redundancy.get(Metrics.Redundancy.OperationalStatus));
        assertEquals(0, redundancy.get(Metrics.Redundancy.OperationalStatus));
        assertNotNull(redundancy.get(Metrics.Redundancy.IsActive));
    }

    public static void derivedRedundantTest(Map<String,Object> derived) {
        assertEquals(1, derived.get(Metrics.Derived.DataSvcOk));
        assertEquals(1, derived.get(Metrics.Derived.MsgSpoolOk));
    }

    public static void msgSpoolTest(Map<String,Object> spool) {
        Integer spoolIsEnabled = (Integer) spool.get(Metrics.MsgSpool.IsEnabled);
        Integer spoolIsActive = (Integer) spool.get(Metrics.MsgSpool.IsActive);
        Integer spoolIsStandby = (Integer) spool.get(Metrics.MsgSpool.IsStandby);
        Integer spoolDatapathUp = (Integer) spool.get(Metrics.MsgSpool.IsDatapathUp);

        assertEquals(1, spoolIsEnabled.intValue());
        if (spoolIsActive==1) {
            assertEquals(1, spoolDatapathUp.intValue());
        }
        else if (spoolIsStandby==1){
            assertEquals(0, spoolDatapathUp.intValue());
        }
        else {
            fail("MsgSpool Test should return either Standby or Active, this test returned neither.");
        }
    }
}
