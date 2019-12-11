package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
import org.junit.Test;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SolaceMonitorTest {

    //@Test
    public void testSolaceMonitor() throws Exception {
        try {
            final SolaceMonitor monitor = new SolaceMonitor();
            final Map<String, String> taskArgs = new HashMap<>();

            taskArgs.put("config-file", "src/test/resources/conf/config.yml");

            while(true) {
                try {
                    monitor.execute(taskArgs, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testSolaceMonitorInvalidConfigs() {
        try {
            final SolaceMonitor monitor = new SolaceMonitor();
            final Map<String, String> taskArgs = new HashMap<>();

            taskArgs.put("config-file", "src/test/resources/conf/invalid-servers-config.yml");

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

    public class TestMonitor extends ABaseMonitor {
        protected String getDefaultMetricPrefix() {
            return "Custom Metrics|TEST";
        }
        public String getMonitorName() {
            return "TEST";
        }
        protected void doRun(TasksExecutionServiceProvider tasksExecutionServiceProvider) {
            Map<String, ?>  configs = this.configuration.getConfigYml();
            for(Map.Entry<String, ?> e : configs.entrySet()) {
                System.out.println(e.getKey() + " = " + e.getValue().toString());
            }
        }
        protected int getTaskCount() {
            return 0;
        }
    }

    @Test
    public void testSolaceMonitorInvalidFormatConfigs() {
        try {
            final ABaseMonitor monitor = new TestMonitor();
            final Map<String, String> taskArgs = new HashMap<>();

            taskArgs.put("config-file", "src/test/resources/conf/invalid-format-config.yml");

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
