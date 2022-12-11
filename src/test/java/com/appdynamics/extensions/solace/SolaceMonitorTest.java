package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
import com.appdynamics.extensions.solace.semp.Sempv1Connector;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.appdynamics.extensions.solace.MonitorConfigs.*;
import static com.appdynamics.extensions.solace.MonitorConfigs.TIMEOUT;
import static org.junit.Assert.*;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


public class SolaceMonitorTest {

    @Ignore
    @Test
    public void testSolaceMonitor() throws Exception {
        Logger root = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
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

        @Override
        protected List<Map<String, ?>> getServers() {
            return (List<Map<String, ?>>) Helper.getMonitorServerList( getContextConfiguration() );
        }

        protected void doRun(TasksExecutionServiceProvider tasksExecutionServiceProvider) {
            Map<String, ?>  configs = this.getContextConfiguration().getConfigYml();
            for (Map<String, ?> server : getServers()) {
                Map<String,String> solaceServer = (Map<String,String>)server;
                ServerConfigs serverConfigs = new ServerConfigs(solaceServer);
                for(String pattern : Helper.getPolicyPatternList(serverConfigs.getQueueFilter(), serverConfigs.getQueueExclusionPolicy()) ) {
                    assertNotNull(pattern);
                    assertFalse( pattern.isEmpty() );
                }
                for(String pattern : Helper.getPolicyPatternList(serverConfigs.getTopicEndpointFilter(), serverConfigs.getTopicEndpointExclusionPolicy()) ) {
                    assertNotNull(pattern);
                    assertFalse( pattern.isEmpty() );
                }
            }
        }
        protected int getTaskCount() {
            return getServers().size();
        }
    }

    @Test
    public void testSolaceMonitorWhitelistConfigs() {
        try {
            final ABaseMonitor monitor = new TestMonitor();
            final Map<String, String> taskArgs = new HashMap<>();

            taskArgs.put("config-file", "src/test/resources/conf/citi-config.yml");

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
