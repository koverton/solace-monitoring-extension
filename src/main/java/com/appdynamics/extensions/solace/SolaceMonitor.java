package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
import com.appdynamics.extensions.solace.semp.SempConnector;
import com.appdynamics.extensions.solace.semp.SempService;
import com.appdynamics.extensions.solace.semp.SempServiceFactory;
import com.appdynamics.extensions.solace.semp.Sempv1Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.appdynamics.extensions.solace.MonitorConfigs.*;

/**
 * TOP-LEVEL AD-PLUGIN CLASS: This is the Solace Monitor class that is found and instantiated within the
 * AD MachineAgent runtime. Its responsibilities are to gather the monitor configuration YML file,
 * interact with the ServiceProvider, construct and schedule SolaceGlobalMonitorTasks.
 */
public class SolaceMonitor extends ABaseMonitor {
    private static final Logger logger = LoggerFactory.getLogger(SolaceMonitor.class);
    private static final String DEFAULT_PREFIX = "Custom Metrics|Solace";
    private static final String CONFIG_ARG = "config-file";

    @Override
    protected String getDefaultMetricPrefix() {
        return DEFAULT_PREFIX;
    }

    @Override
    public String getMonitorName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void doRun(TasksExecutionServiceProvider serviceProvider) {
        // Internally, we don't want prefixes to end with delimiters, so strip off
        String baseMetricPrefix  = getBasePrefix();
        MetricWriteHelper metricWriter = serviceProvider.getMetricWriteHelper();

        // Each Solace server monitored comes with a number of configurations
        for (Map<String, String> server : Helper.getMonitorServerList(configuration)) {
            String mgmtUrl     = server.get(MGMT_URL);
            String adminUser   = server.get(ADMIN_USER);
            String adminPass   = Helper.getPassword(server);
            String displayName = server.get(DISPLAY_NAME);
            Integer timeout    = Helper.getIntOrDefault(server, TIMEOUT, Sempv1Connector.DEFAULT_TIMEOUT);
            ServerExclusionPolicies exclusionPolicies = new ServerExclusionPolicies(server);

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
                SempService sempService = SempServiceFactory.createSempService(connector, exclusionPolicies);
                if (sempService != null) {
                    serviceProvider.submit(displayName,
                            new SolaceGlobalMonitorTask(
                                    new ADMetricPrinter(metricWriter),
                                    baseMetricPrefix,
                                    exclusionPolicies,
                                    sempService));
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

    @Override
    protected int getTaskCount() {
        return Helper.getMonitorServerList(configuration).size();
    }

    /**
     * Makes sure the configured metric prefix does NOT end with a delimiter.
     * @return
     */
    private String getBasePrefix() {
        String baseMetricPrefix  = (String) configuration.getConfigYml().get(METRIC_PREFIX);
        if (baseMetricPrefix.endsWith("|"))
            return baseMetricPrefix.substring(0, baseMetricPrefix.lastIndexOf("|"));
        return baseMetricPrefix;
    }

}
