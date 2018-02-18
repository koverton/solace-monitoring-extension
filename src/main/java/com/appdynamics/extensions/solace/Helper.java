package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.TaskInputArgs;
import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.crypto.CryptoUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.appdynamics.extensions.TaskInputArgs.PASSWORD;
import static com.appdynamics.extensions.TaskInputArgs.PASSWORD_ENCRYPTED;
import static com.appdynamics.extensions.solace.MonitorConfigs.ENCRYPTED_PASSWORD;
import static com.appdynamics.extensions.solace.MonitorConfigs.ENCRYPTION_KEY;
import static com.appdynamics.extensions.solace.MonitorConfigs.SERVERS;

class Helper {
    private static final Logger logger = LoggerFactory.getLogger(Helper.class);

    //
    // String and field safe conversion helpers
    //

    static boolean validateRequiredField(String name, String value, String displayName) {
        if (Strings.isNullOrEmpty(value)) {
            logger.error("Required server config field [{}] appears to be null or empty; SKIPPED POLLING OF SERVER [{}]",
                    name, displayName);
            return false;
        }
        return true;
    }

    static String convertToString(final Object field, final String defaultStr) {
        if (field == null) {
            return defaultStr;
        }
        return field.toString();
    }

    static Integer getIntOrDefault(Map<String,String> server, String fieldName, Integer defaultValue) {
        if (server.containsKey(fieldName)) {
            String val = (String)server.get(fieldName);
            try {
                return Integer.parseInt(val);
            }
            catch(NumberFormatException ex) {
                logger.warn("Could not parse valid Integer for [{}]; defaulting to [{}]",
                        fieldName, defaultValue);
            }
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    static List<Map<String,String>> getMonitorServerList(MonitorConfiguration config) {
        Object obj = config.getConfigYml().get(SERVERS);
        if (obj instanceof List)
            return (List<Map<String,String>>)obj;
        throw new ClassCastException("config.yml entry for " + SERVERS + " must be a list of Maps");
    }

    @SuppressWarnings("unchecked")
    static List<String> getConfigListOrNew(MonitorConfiguration config, String key) {
        if (config.getConfigYml().containsKey(key))
            return (List<String>) config.getConfigYml().get(key);
        else {
            logger.warn("No list found configured for key [{}]", key);
            return new ArrayList<>();
        }
    }

    //
    // Password and cryptutils helpers
    //

    /**
     * If an encryptedPassword is configured for this server, decrypt and return. If not,
     * return the plain password field configured for this server.
     * @param server -- mapping of config-string to config-value-string for a server being monitored.
     * @return returns plaintext password string from the configuration, decrypting if necessary.
     */
    static String getPassword(Map server) {
        String encryptedPassword = convertToString(server.get(ENCRYPTED_PASSWORD), "");
        String encryptionKey = convertToString(server.get(ENCRYPTION_KEY), "");
        String password;
        if (!Strings.isNullOrEmpty(encryptionKey) && !Strings.isNullOrEmpty(encryptedPassword)) {
            password = getEncryptedPassword(encryptionKey, encryptedPassword);
        } else {
            password = (String) server.get(PASSWORD);
        }
        return password;
    }

    private static String getEncryptedPassword(String encryptionKey, String encryptedPassword) {
        Map<String, String> cryptoMap = Maps.newHashMap();
        cryptoMap.put(PASSWORD_ENCRYPTED, encryptedPassword);
        cryptoMap.put(TaskInputArgs.ENCRYPTION_KEY, encryptionKey);
        return CryptoUtil.getPassword(cryptoMap);
    }
}
