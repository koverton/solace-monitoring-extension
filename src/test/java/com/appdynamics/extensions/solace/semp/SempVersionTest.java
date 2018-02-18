package com.appdynamics.extensions.solace.semp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SempVersionTest {
    private static final String VMR86_SCHEMA = "soltr/8_6VMR";
    private static final String VMR86_VERSION = "8_6VMR";
    private static final String APPL82_SCHEMA = "soltr/8_2_0";
    private static final String APPL82_VERSION = "8_2_0";

    @Test
    public void validVmrStringtest() {
        SempVersion v = new SempVersion(VMR86_SCHEMA);
        assertEquals(SempVersion.Platform.VMR, v.getPlatform());
        assertEquals(VMR86_VERSION, v.getVersionString());
        assertEquals(8.6, v.getVersionNumber(), 0.01);
    }

    @Test
    public void validApplStringtest() {
        SempVersion v = new SempVersion(APPL82_SCHEMA);
        assertEquals(SempVersion.Platform.APPLIANCE, v.getPlatform());
        assertEquals(APPL82_VERSION, v.getVersionString());
        assertEquals(8.20, v.getVersionNumber(), 0.01);
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
