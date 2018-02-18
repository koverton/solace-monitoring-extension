package com.appdynamics.extensions.solace.semp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConnectorTest {

    private static final String err86vmr = "<rpc-reply semp-version=\"soltr/8_6VMR\">"
            + "<parse-error>invalid message: schema validation error</parse-error></rpc-reply>";
    private static final String err820 = "<rpc-reply semp-version=\"soltr/8_2_0\">"
            + "<parse-error>invalid message: schema validation error</parse-error></rpc-reply>";
    @Test
    public void versionTest() throws Exception {
        Sempv1Connector connector = new Sempv1Connector(
                "http://1.1.1.1:8080/SEMP",
                "admin",
                "admin",
                "jimmy");

        // VMR version
        assertEquals("8_6VMR", connector.getSempVersion(err86vmr).getVersionString());
        // Hardware version
        assertEquals("8_2_0", connector.getSempVersion(err820).getVersionString());
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyVersionTest() throws Exception {
        Sempv1Connector connector = new Sempv1Connector(
                "http://1.1.1.1:8080/SEMP",
                "admin",
                "admin",
                "jimmy");
        // 2) empty semp-version throws
        connector.getSempVersion("<rpc-reply semp-version=\"\">");
    }
}
