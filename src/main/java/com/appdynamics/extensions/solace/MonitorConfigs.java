package com.appdynamics.extensions.solace;

public class MonitorConfigs {
    public enum ExclusionPolicy { WHITELIST, BLACKLIST }

    final static String VPN_EXCLUSION_POLICY = "vpnExclusionPolicy";
    final static String QUEUE_EXCLUSION_POLICY = "queueExclusionPolicy";
    final static String TOPIC_ENDPOINT_EXCLUSION_POLICY = "topicEndpointExclusionPolicy";
    final static String EXCLUDE_MSG_VPNS = "excludeMsgVpns";
    final static String EXCLUDE_QUEUES = "excludeQueues";
    final static String EXCLUDE_TOPIC_ENDPOINTS = "excludeTopicEndpoints";
    final static String EXCLUDE_TEMPORARIES = "excludeTemporaries";
    final static String EXCLUDE_COMPRESSION_METRICS = "excludeCompressionMetrics";
    final static String EXCLUDE_TLS_METRICS = "excludeTlsMetrics";
    final static String EXCLUDE_DISCARD_METRICS = "excludeDiscardMetrics";

    final static String METRIC_PREFIX = "metricPrefix";
    final static String SERVERS = "servers";

    final static String MGMT_URL = "mgmtUrl";
    final static String ADMIN_USER = "adminUser";
    final static String DISPLAY_NAME = "displayName";
    final static String ENCRYPTED_PASSWORD = "encryptedPassword";
    final static String ENCRYPTION_KEY = "encryptionKey";
    final static String TIMEOUT = "requestTimeout";
}
