package com.appdynamics.extensions.solace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.appdynamics.extensions.solace.MonitorConfigs.*;

/**
 * <p>Encapsulates all supported filtering and exclusion of metrics before writing them to the AD Controller.
 *
 * <p>ExclusionPolicy instances can support wildcard-based blacklisting and whitelisting.
 */
public class ServerConfigs {
    private static final Logger logger = LoggerFactory.getLogger(ServerConfigs.class);

    public ServerConfigs(Map<String, String> server) {
        if (server == null)
            server = new HashMap<>();
        this.redundancyModel             = Helper.parseRedundancyModel(server.get(REDUNDANCY));
        this.vpnExclusionPolicy          = Helper.parseExclusionPolicy(server.get(VPN_EXCLUSION_POLICY));
        this.vpnFilter                   = Helper.getRegexPatternListOrNew(server, EXCLUDE_MSG_VPNS);
        this.queueExclusionPolicy        = Helper.parseExclusionPolicy(server.get(QUEUE_EXCLUSION_POLICY));
        this.queueFilter                 = Helper.getRegexPatternListOrNew(server, EXCLUDE_QUEUES);
        this.topicEndpointExclusionPolicy= Helper.parseExclusionPolicy(server.get(TOPIC_ENDPOINT_EXCLUSION_POLICY));
        this.topicEndpointFilter         = Helper.getRegexPatternListOrNew(server, EXCLUDE_TOPIC_ENDPOINTS);
        this.excludeTemporaries          = Helper.getBooleanOrDefault(server, EXCLUDE_TEMPORARIES, true);
        this.excludeCompressionMetrics   = Helper.getBooleanOrDefault(server, EXCLUDE_COMPRESSION_METRICS, true);
        this.excludeTlsMetrics           = Helper.getBooleanOrDefault(server, EXCLUDE_TLS_METRICS, true);
        this.excludeDiscardMetrics       = Helper.getBooleanOrDefault(server, EXCLUDE_DISCARD_METRICS, true);
        this.excludeExtendedStats        = Helper.getBooleanOrDefault(server, EXCLUDE_EXTENDED_STATS, true);
        this.log();
    }

    public void log() {
        logger.info("Redundancy Model: {}", redundancyModel.toString());
        logger.info("VPN Exclusion policy: {}", vpnExclusionPolicy);
        for (Pattern excludedVpnPattern : vpnFilter)
            logger.info("VPN Exclusion Pattern: {}", excludedVpnPattern);
        logger.info("Queue Exclusion policy: {}", queueExclusionPolicy.toString());
        for (Pattern excludedQueuePattern : queueFilter)
            logger.info("Queue Exclusion Pattern: {}", excludedQueuePattern.toString());
        logger.info("TopicEndpoint Exclusion policy: {}", topicEndpointExclusionPolicy.toString());
        for (Pattern topicEndpointPattern : topicEndpointFilter)
            logger.info("Queue Exclusion Pattern: {}", topicEndpointPattern.toString());
        logger.info("Temporary endpoint exclusion policy: {}", excludeTemporaries);
        logger.info("Compress exclusion policy: {}", excludeCompressionMetrics);
        logger.info("TLS exclusion policy: {}", excludeTemporaries);
        logger.info("Discards exclusion policy: {}", excludeDiscardMetrics);
        logger.info("Extended Stats exclusion policy: {}", excludeExtendedStats);
    }

    public MonitorConfigs.RedundancyModel getRedundancyModel() { return redundancyModel; }

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

    public Boolean getExcludeCompressionMetrics() {
        return excludeCompressionMetrics;
    }

    public Boolean getExcludeTlsMetrics() {
        return excludeTlsMetrics;
    }

    public Boolean getExcludeDiscardMetrics() {
        return excludeDiscardMetrics;
    }

    public Boolean getExcludeExtendedStats() { return excludeExtendedStats; }

    final private MonitorConfigs.RedundancyModel redundancyModel;

    final private MonitorConfigs.ExclusionPolicy vpnExclusionPolicy;
    final private List<Pattern> vpnFilter;

    final private MonitorConfigs.ExclusionPolicy queueExclusionPolicy;
    final private List<Pattern> queueFilter;

    final private MonitorConfigs.ExclusionPolicy topicEndpointExclusionPolicy;
    final private List<Pattern> topicEndpointFilter;

    final private Boolean excludeTemporaries;
    final private Boolean excludeCompressionMetrics;
    final private Boolean excludeTlsMetrics;
    final private Boolean excludeDiscardMetrics;
    final private Boolean excludeExtendedStats;
}
