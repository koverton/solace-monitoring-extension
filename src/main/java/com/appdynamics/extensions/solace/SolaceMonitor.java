package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.util.MetricWriteHelper;
import com.appdynamics.extensions.util.MetricWriteHelperFactory;
import com.appdynamics.extensions.solace.semp.SempService;
import com.appdynamics.extensions.solace.semp.SempServiceFactory;
import com.appdynamics.extensions.solace.semp.Sempv1Connector;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.List;
import java.util.Map;

public class SolaceMonitor extends AManagedMonitor {
    private static final Logger logger = LoggerFactory.getLogger(SolaceMonitor.class);

    final private static String MON_VERSION = "1.0";

    public SolaceMonitor() {
        logger.info(String.format("Using SolaceMonitor Version [%s]", MON_VERSION));
    }

    protected void initialize(Map<String, String> argsMap) {
        logger.debug("<SolaceMonitor.initialize>");
        if (configuration == null) {
            logger.info("Creating new SolaceMonitor MonitorConfiguration because it doesn't exist.");
            MetricWriteHelper metricWriter = MetricWriteHelperFactory.create(this);
            MonitorConfiguration conf = new MonitorConfiguration(
                    "Custom Metrics|Solace",
                    new TaskRunner(),
                    metricWriter);
            final String configFilePath = argsMap.get("config-file");
            conf.setConfigYml(configFilePath);
            conf.checkIfInitialized(MonitorConfiguration.ConfItem.METRIC_PREFIX,
                    MonitorConfiguration.ConfItem.CONFIG_YML,
                    MonitorConfiguration.ConfItem.EXECUTOR_SERVICE);
            this.configuration = conf;
        }
        logger.debug("</SolaceMonitor.initialize>");
    }

    private class TaskRunner implements Runnable {
        @Override
        public void run () {
            logger.debug("<SolaceMonitor.TaskRunner.run>");
            Map<String, ?> config = configuration.getConfigYml();
            List<Map> servers = (List) config.get("servers");
            if (servers != null && !servers.isEmpty()) {
                for (Map server : servers) {
                    String mgmtUrl     = (String)server.get("mgmtUrl");
                    String adminUser   = (String)server.get("adminUser");
                    String adminPass   = (String)server.get("adminPassword");
                    String displayName = (String)server.get("displayName");
                    try {
                        logger.info("Server:{}, Mgmt URL:{}, Admin User:{}",
                                    displayName, mgmtUrl, adminUser);
                        Sempv1Connector connector = new Sempv1Connector(
                                mgmtUrl,
                                adminUser,
                                adminPass,
                                displayName);
                        SempService sempService = SempServiceFactory.createSempService(connector);
                        SolaceGlobalMonitorTask task = new SolaceGlobalMonitorTask(configuration, sempService);
                        configuration.getExecutorService().execute(task);
                    }
                    catch(MalformedURLException ex) {
                        logger.error("Exception thrown creating and executing service request", ex);
                        ex.printStackTrace();
                    }
                }
            } else {
                logger.error("The stats read from the metric xml is empty. Please make sure that the metrics xml is correct");
            }
            logger.debug("</SolaceMonitor.TaskRunner.run>");
        }
    }

    @Override
    public TaskOutput execute(Map<String, String> argMap, TaskExecutionContext context) {
        logger.debug("<SolaceMonitor.execute>The raw arguments are {}", argMap);
        try {
            initialize(argMap);
            configuration.executeTask();
        }
        catch(Exception e){
            if(configuration != null && configuration.getMetricWriter() != null) {
                configuration.getMetricWriter().registerError(e.getMessage(), e);
            }
        }
        logger.debug("</SolaceMonitor.execute>");
        return null;
    }
/**
    public static void main( String[] args )
    {
        try {
            SolaceMonitor monitor = new SolaceMonitor();
            Map<String, String> taskArgs = new HashMap<>();
            taskArgs.put("config-file", "src/main/resources/conf/config.yml");
            monitor.execute(taskArgs, null);
            Thread.sleep(3000);
        }
        catch(InterruptedException ex) {
            ex.printStackTrace();
        }
        catch(TaskExecutionException tex) {
            tex.printStackTrace();
        }
        catch(JAXBException jex) {
            jex.printStackTrace();
        }
    }
**/

    private MonitorConfiguration configuration;

}
