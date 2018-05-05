package com.appdynamics.extensions.solace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.appdynamics.extensions.solace.MonitorConfigs.*;

class ServerExclusionPolicies {
    private static final Logger logger = LoggerFactory.getLogger(ServerExclusionPolicies.class);

    public ServerExclusionPolicies(Map<String, String> server) {
        this.vpnExclusionPolicy          = Helper.parseExclusionPolicy(server.get(VPN_EXCLUSION_POLICY));
        this.vpnFilter                   = Helper.getRegexPatternListOrNew(server, EXCLUDE_MSG_VPNS);
        this.queueExclusionPolicy        = Helper.parseExclusionPolicy(server.get(QUEUE_EXCLUSION_POLICY));
        this.queueFilter                 = Helper.getRegexPatternListOrNew(server, EXCLUDE_QUEUES);
        this.topicEndpointExclusionPolicy= Helper.parseExclusionPolicy(server.get(TOPIC_ENDPOINT_EXCLUSION_POLICY));
        this.topicEndpointFilter         = Helper.getRegexPatternListOrNew(server, EXCLUDE_TOPIC_ENDPOINTS);
        this.excludeTemporaries          = Helper.getBooleanOrDefault(server, EXCLUDE_TEMPORARIES, true);
        this.log();
    }

    private void log() {
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
            logger.debug("Temporary endpoint exclusion policy: {}", excludeTemporaries);
        }
    }

    public MonitorConfigs.ExclusionPolicy getVpnExclusionPolicy() {
        return vpnExclusionPolicy;
    }

    public List<Pattern> getVpnFilter() {
        return vpnFilter;
    }

    public MonitorConfigs.ExclusionPolicy getQueueExclusionPolicy() {
        return queueExclusionPolicy;
    }

    public List<Pattern> getQueueFilter() {
        return queueFilter;
    }

    public MonitorConfigs.ExclusionPolicy getTopicEndpointExclusionPolicy() {
        return topicEndpointExclusionPolicy;
    }

    public List<Pattern> getTopicEndpointFilter() {
        return topicEndpointFilter;
    }

    public Boolean getExcludeTemporaries() {
        return excludeTemporaries;
    }

    final private MonitorConfigs.ExclusionPolicy vpnExclusionPolicy;
    final private List<Pattern> vpnFilter;

    final private MonitorConfigs.ExclusionPolicy queueExclusionPolicy;
    final private List<Pattern> queueFilter;

    final private MonitorConfigs.ExclusionPolicy topicEndpointExclusionPolicy;
    final private List<Pattern> topicEndpointFilter;

    final private Boolean excludeTemporaries;
}
