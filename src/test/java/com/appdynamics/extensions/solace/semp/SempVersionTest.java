package com.appdynamics.extensions.solace.semp;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SempVersionTest {
    private static final String VMR86_SCHEMA = "soltr/8_6VMR";
    private static final String VMR86_VERSION = "8_6VMR";
    private static final String APPL802_SCHEMA = "soltr/8_2_0";
    private static final String APPL813_SCHEMA = "soltr/8_13_0";
    private static final String APPL802_VERSION = "8_2_0";
    private static final String APPL813_VERSION = "8_13_0";

    @Test
    public void validVmrStringTest() {
        SempVersion v = new SempVersion(VMR86_SCHEMA);
        assertEquals(SempVersion.Platform.VMR, v.getPlatform());
        assertEquals(VMR86_VERSION, v.getVersionString());
        assertEquals(8.06, v.getVersionNumber(), 0.005);
    }

    @Test
    public void validApplStringTest() {
        SempVersion v = new SempVersion(APPL802_SCHEMA);
        assertEquals(SempVersion.Platform.APPLIANCE, v.getPlatform());
        assertEquals(APPL802_VERSION, v.getVersionString());
        assertEquals(8.02, v.getVersionNumber(), 0.005);
    }

    @Test
    public void validTwoDigitMinorVersionTest() {
        SempVersion v = new SempVersion(APPL813_SCHEMA);
        assertEquals(SempVersion.Platform.APPLIANCE, v.getPlatform());
        assertEquals(APPL813_VERSION, v.getVersionString());
        assertEquals(8.13, v.getVersionNumber(), 0.005);
        assertTrue( v.getVersionNumber() > SempVersion.v8_2_0.getVersionNumber());
    }

    @Test
    public void emptyStringTest() {
        SempVersion v = new SempVersion("");
        assertFalse(v.isValid());
    }

    @Test
    public void notASempVersion() {
        SempVersion v = new SempVersion("My name is Jimmy");
        assertFalse(v.isValid());
    }
}
