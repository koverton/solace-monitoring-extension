package com.appdynamics.extensions.solace;

public class MonitorConfigs {
    public enum ExclusionPolicy { WHITELIST, BLACKLIST }

    final public static String VPN_EXCLUSION_POLICY = "vpnExclusionPolicy";
    final public static String QUEUE_EXCLUSION_POLICY = "queueExclusionPolicy";
    final public static String EXCLUDE_MSG_VPNS = "excludeMsgVpns";
    final public static String EXCLUDE_QUEUES = "excludeQueues";
    final public static String METRIC_PREFIX = "metricPrefix";
    final public static String SERVERS = "servers";

    final public static String MGMT_URL = "mgmtUrl";
    final public static String ADMIN_USER = "adminUser";
    final public static String DISPLAY_NAME = "displayName";
    final public static String ENCRYPTED_PASSWORD = "encryptedPassword";
    final public static String ENCRYPTION_KEY = "encryptionKey";
    final public static String TIMEOUT = "requestTimeout";
}
