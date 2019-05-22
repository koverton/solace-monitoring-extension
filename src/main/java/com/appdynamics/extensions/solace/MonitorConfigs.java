package com.appdynamics.extensions.solace;

/**
 * <p>Static metric ID names for all available metrics.
 */
public class MonitorConfigs {
    public enum ExclusionPolicy { WHITELIST, BLACKLIST }
    public enum RedundancyModel { STANDALONE, REDUNDANT }

    public final static String REDUNDANCY = "redundancy";

    public final static String VPN_EXCLUSION_POLICY = "vpnExclusionPolicy";
    public final static String QUEUE_EXCLUSION_POLICY = "queueExclusionPolicy";
    public final static String TOPIC_ENDPOINT_EXCLUSION_POLICY = "topicEndpointExclusionPolicy";
    public final static String EXCLUDE_MSG_VPNS = "excludeMsgVpns";
    public final static String EXCLUDE_QUEUES = "excludeQueues";
    public final static String EXCLUDE_TOPIC_ENDPOINTS = "excludeTopicEndpoints";
    public final static String EXCLUDE_TEMPORARIES = "excludeTemporaries";
    public final static String EXCLUDE_COMPRESSION_METRICS = "excludeCompressionMetrics";
    public final static String EXCLUDE_TLS_METRICS = "excludeTlsMetrics";
    public final static String EXCLUDE_DISCARD_METRICS = "excludeDiscardMetrics";
    public final static String EXCLUDE_EXTENDED_STATS = "excludeExtendedStats";

    public final static String METRIC_PREFIX = "metricPrefix";
    public final static String SERVERS = "servers";

    public final static String MGMT_URL = "mgmtUrl";
    public final static String ADMIN_USER = "adminUser";
    public final static String DISPLAY_NAME = "displayName";
    public final static String ENCRYPTED_PASSWORD = "encryptedPassword";
    public final static String ENCRYPTION_KEY = "encryptionKey";
    public final static String TIMEOUT = "requestTimeout";
}
