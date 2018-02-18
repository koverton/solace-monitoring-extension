package com.appdynamics.extensions.solace;

import org.junit.Test;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SolaceMonitorTest {

    @Test
    public void testSolaceMonitor() throws Exception {
        try {
            final SolaceMonitor monitor = new SolaceMonitor();
            final Map<String, String> taskArgs = new HashMap<>();

            taskArgs.put("config-file", "src/test/resources/conf/config.yml");

                try {
                    monitor.execute(taskArgs, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

    }
}
