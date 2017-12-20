package com.appdynamics.extensions.solace;

import org.junit.Test;
import java.util.Map;
import java.util.HashMap;

public class SolaceMonitorTest {

    // @Test
    public void testSolaceMonitor() throws Exception {
        SolaceMonitor monitor = new SolaceMonitor();
        Map<String, String> taskArgs = new HashMap<>();
        taskArgs.put("config-file", "src/test/resources/conf/config.yml");
        monitor.execute(taskArgs, null);
        try {
            Thread.sleep(3000);
        }
        catch(InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
