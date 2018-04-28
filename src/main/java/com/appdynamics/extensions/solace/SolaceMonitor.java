package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
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
import java.util.regex.Pattern;

import static com.appdynamics.extensions.solace.MonitorConfigs.*;

public class SolaceMonitor extends ABaseMonitor {
    private static final Logger logger = LoggerFactory.getLogger(SolaceMonitor.class);
    private static final String DEFAULT_PREFIX = "Custom Metrics|Solace|";
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
        String baseMetricPrefix  = (String) configuration.getConfigYml().get(METRIC_PREFIX);
        List<Map<String, String>> servers = Helper.getMonitorServerList(configuration);
        MetricWriteHelper metricWriter = serviceProvider.getMetricWriteHelper();


        for (Map<String, String> server : servers) {
            String mgmtUrl     = server.get(MGMT_URL);
            String adminUser   = server.get(ADMIN_USER);
            String adminPass   = Helper.getPassword(server);
            String displayName = server.get(DISPLAY_NAME);
            Integer timeout    = Helper.getIntOrDefault(server, TIMEOUT, Sempv1Connector.DEFAULT_TIMEOUT);
            ExclusionPolicy vpnExclusionPolicy = Helper.parseExclusionPolicy(server.get(VPN_EXCLUSION_POLICY));
            List<Pattern> vpnFilter   = Helper.getRegexPatternListOrNew(server, EXCLUDE_MSG_VPNS);
            ExclusionPolicy queueExclusionPolicy = Helper.parseExclusionPolicy(server.get(QUEUE_EXCLUSION_POLICY));
            List<Pattern> queueFilter = Helper.getRegexPatternListOrNew(server, EXCLUDE_QUEUES);
            ExclusionPolicy topicEndpointExclusionPolicy = Helper.parseExclusionPolicy(server.get(TOPIC_ENDPOINT_EXCLUSION_POLICY));
            List<Pattern> topicEndpointFilter = Helper.getRegexPatternListOrNew(server, EXCLUDE_TOPIC_ENDPOINTS);

            if (logger.isDebugEnabled()) {
                logger.debug("VPN Exclusion policy: {}", vpnExclusionPolicy);
                for (Pattern excludedVpnPattern : vpnFilter)
                    logger.debug("VPN Exclusion Pattern: {}", excludedVpnPattern);
                logger.debug("Queue Exclusion policy: {}", queueExclusionPolicy.toString());
                for (Pattern excludedQueuePattern : queueFilter)
                    logger.debug("Queue Exclusion Pattern: {}", excludedQueuePattern.toString());
                logger.debug("TopicEndpoint Exclusion policy: {}", topicEndpointExclusionPolicy.toString());
                for (Pattern topicEndpointPattern : topicEndpointFilter)
                    logger.debug("Queue Exclusion Pattern: {}", topicEndpointPattern.toString());
            }
            logger.info("Adding task to poll [Server:{}, Mgmt URL:{}, Admin User:{}]",
                    displayName, mgmtUrl, adminUser);

            // Validate the required server fields
            if (!Helper.validateRequiredField(MGMT_URL, mgmtUrl, displayName))
                continue;
            if (!Helper.validateRequiredField(ADMIN_USER, adminUser, displayName))
                continue;

            try {
                Sempv1Connector connector = new Sempv1Connector(
                        mgmtUrl,
                        adminUser,
                        adminPass,
                        displayName,
                        timeout);
                SempService sempService = SempServiceFactory.createSempService(connector);
                if (sempService != null) {
                    serviceProvider.submit(displayName,
                            new SolaceGlobalMonitorTask(metricWriter,
                                    baseMetricPrefix,
                                    vpnExclusionPolicy, vpnFilter,
                                    queueExclusionPolicy, queueFilter,
                                    topicEndpointExclusionPolicy, topicEndpointFilter,
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

    public static void main( String[] args )
    {
        try {
            final SolaceMonitor monitor = new SolaceMonitor();
            final Map<String, String> taskArgs = new HashMap<>();

            taskArgs.put(CONFIG_ARG, "src/main/resources/conf/config.yml");

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    monitor.execute(taskArgs, null);
                } catch (Exception e) {
                    logger.error("Error while running the task", e);
                }
            }, 2, 10, TimeUnit.SECONDS);
        }
        catch(Exception ex) {
            logger.error("Exception while executing Solace Monitor", ex);
            ex.printStackTrace();
        }
    }

}
