package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.solace.semp.SempConnector;
import com.appdynamics.extensions.solace.semp.SempService;
import com.appdynamics.extensions.solace.semp.SempServiceFactory;
import com.appdynamics.extensions.solace.semp.Sempv1Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static com.appdynamics.extensions.solace.MonitorConfigs.*;

/**
 * TOP-LEVEL AD-PLUGIN CLASS: This is the Solace Monitor class that is found and instantiated within the
 * AD MachineAgent runtime. Its responsibilities are to gather the monitor configuration YML file,
 * interact with the ServiceProvider, construct and schedule SolaceGlobalMonitorTasks.
 */
public class SolaceStandaloneMonitor extends ABaseMonitor {
    private static final Logger logger = LoggerFactory.getLogger(SolaceStandaloneMonitor.class);
    private static final String DEFAULT_PREFIX = "Custom Metrics|Solace";

    protected String getDefaultMetricPrefix() {
        return DEFAULT_PREFIX;
    }

    public String getMonitorName() {
        return this.getClass().getSimpleName();
    }

    protected void doRun(TasksExecutionServiceProvider serviceProvider) {
        // Internally, we don't want prefixes to end with delimiters, so strip off
        String baseMetricPrefix  = getBasePrefix();

        // Each Solace server monitored comes with a number of configurations
        for (Map<String, String> server : Helper.getMonitorServerList(configuration)) {
            String mgmtUrl     = server.get(MGMT_URL);
            String adminUser   = server.get(ADMIN_USER);
            String adminPass   = Helper.getPassword(server);
            String displayName = server.get(DISPLAY_NAME);
            Integer timeout    = Helper.getIntOrDefault(server, TIMEOUT, Sempv1Connector.DEFAULT_TIMEOUT);
            ServerConfigs serverConfigs = new ServerConfigs(server);

            logger.info("Adding task to poll [Server:{}, Mgmt URL:{}, Admin User:{}]",
                    displayName, mgmtUrl, adminUser);

            // Validate the required server fields
            if (!Helper.validateRequiredField(MGMT_URL, mgmtUrl, displayName))
                continue;
            if (!Helper.validateRequiredField(ADMIN_USER, adminUser, displayName))
                continue;

            try {
                SempConnector connector = new Sempv1Connector(
                        mgmtUrl,
                        adminUser,
                        adminPass,
                        displayName,
                        timeout);
                SempService sempService = SempServiceFactory.createSempService(connector, serverConfigs);
                if (sempService != null) {
                            Runnable task = new SolaceGlobalMonitorTask(
                                    new LocalMetricPrinter(),
                                    baseMetricPrefix,
                                    serverConfigs,
                                    sempService);
                            task.run();
                }
                else {
                    logger.error("Could not create SEMP Service due to exception; SKIPPED POLLING OF SERVER [{}]",
                            displayName);
                }
            }
            catch(MalformedURLException ex) {
                logger.error("MalformedURLException thrown creating and executing service request for server "+displayName, ex);
            }
        }
    }

    protected int getTaskCount() {
        return Helper.getMonitorServerList(configuration).size();
    }

    /**
     * Makes sure the configured metric prefix does NOT end with a delimiter.
     * @return Base prefix string ensured to be terminated with a symbol rather than a delimiter
     */
    private String getBasePrefix() {
        String baseMetricPrefix  = (String) configuration.getConfigYml().get(METRIC_PREFIX);
        if (baseMetricPrefix.endsWith("|"))
            return baseMetricPrefix.substring(0, baseMetricPrefix.lastIndexOf("|"));
        return baseMetricPrefix;
    }

    public static void main(String[] args) {
        try {
            final SolaceStandaloneMonitor monitor = new SolaceStandaloneMonitor();
            final Map<String, String> taskArgs = new HashMap<>();

            taskArgs.put("config-file", "src/test/resources/conf/config.yml");

            while(true) {
                try {
                    monitor.initialize(taskArgs);
                    monitor.doRun(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(3000);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

    }
}
