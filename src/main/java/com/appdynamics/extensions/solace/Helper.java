package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.TaskInputArgs;
import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.crypto.CryptoUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.appdynamics.extensions.TaskInputArgs.PASSWORD;
import static com.appdynamics.extensions.TaskInputArgs.PASSWORD_ENCRYPTED;
import static com.appdynamics.extensions.solace.MonitorConfigs.*;

/**
 * <p>Internal helper methods for {@link com.appdynamics.extensions.solace.SolaceMonitor}.
 */
public class Helper {
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

    public static Long longOrDefault(Long value, long defaultValue) {
        if (value == null) return defaultValue;
        return value;
    }
    public static Long longOrDefault(Double value, long defaultValue) {
        if (value == null) return defaultValue;
        return value.longValue();
    }
    public static Long longOrDefault(BigInteger value, long defaultValue) {
        if (value == null) return defaultValue;
        return value.longValue();
    }


    public static Double safeParseDouble(String fieldName, String input) {
        try {
            return Double.parseDouble(input);
        }
        catch(NumberFormatException ex) {
            logger.error("NumberFormatException parsing field {} value {}", fieldName, input);
        }
        return 0.0;
    }

    public static Long calcPercentage(Double numerator, Double denominator) {
        if (denominator == 0 || numerator == 0) return 0L;
        return ((Double) (100.0* numerator / denominator)).longValue();
    }
    public static Long calcPercentage(BigInteger numerator, BigInteger denominator) {
        if (denominator.longValue() == 0 || numerator.longValue() == 0) return 0L;
        return ((Double)(100.0* numerator.doubleValue() / denominator.doubleValue())).longValue();
    }
    public static Long calcPercentage(Long numerator, Long denominator) {
        if (denominator.longValue() == 0 || numerator.longValue() == 0) return 0L;
        return ((Double)(100.0* numerator.doubleValue() / denominator.doubleValue())).longValue();
    }


    static Integer getIntOrDefault(Map<String,String> server, String fieldName, Integer defaultValue) {
        if (server.containsKey(fieldName)) {
            Object o = server.get(fieldName);
            if (o instanceof Integer)
                return (Integer)o;
            try {
                return Integer.parseInt(o.toString());
            }
            catch(NumberFormatException ex) {
                logger.warn("Could not parse valid Integer for [{}]; defaulting to [{}]",
                        fieldName, defaultValue);
            }
        }
        return defaultValue;
    }

    static Boolean getBooleanOrDefault(Map<String,String> server, String fieldName, Boolean defaultValue) {
        if (server.containsKey(fieldName)) {
            Object o = server.get(fieldName);
            if (o instanceof Boolean)
                return (Boolean) o;
            try {
                return Boolean.parseBoolean(o.toString());
            }
            catch(Exception ex) {
                logger.warn("Could not parse valid Boolean for [{}]; defaulting to [{}]",
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
    static List<Pattern> getRegexPatternListOrNew(Map<String, String> config, String key) {
        if (config.containsKey(key)) {
            Object obj = config.get(key);
            List<Pattern> result = new ArrayList<>();
            if (obj instanceof String) {
                for(String s : splitDelimitedString((String)obj)) {
                    result.add( Pattern.compile(s));
                }
            }
            else if (obj instanceof List) {
                for (String s : (List<String>)obj) {
                    result.add(Pattern.compile(s));
                }
            }
            return result;
        }
        logger.warn("No list found configured for key [{}]", key);
        return new ArrayList<>();
    }

    static List<String> splitDelimitedString(String source) {
        List<String> result = new ArrayList<>();
        for(String s : source.split("," )) {
            result.add(s.trim());
        }
        return result;
    }

    static ExclusionPolicy parseExclusionPolicy(String value) {
        if (value == null) return ExclusionPolicy.BLACKLIST;

        for (ExclusionPolicy each : ExclusionPolicy.class.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(value) == 0) {
                return each;
            }
        }
        logger.error("Failure parsing ExclusionPolicy configuration for value {}, defaulting to BLACKLIST", value);
        return ExclusionPolicy.BLACKLIST;
    }

    static RedundancyModel parseRedundancyModel(String redundancy) {
        if (redundancy == null || redundancy.length()==0)
            return RedundancyModel.STANDALONE;
        if (redundancy.toUpperCase().equals(RedundancyModel.REDUNDANT.name()))
            return RedundancyModel.REDUNDANT;
        return RedundancyModel.STANDALONE;
    }

    static boolean isExcluded(String name, List<Pattern> policyList, ExclusionPolicy policy) {
        if (policy == ExclusionPolicy.BLACKLIST) {
            // Exclude this item because it was found in the blacklist
            for(Pattern p : policyList) {
                if (p.matcher(name).matches())
                    return true;
            }
        }
        else {
            // Exclude this item because it was NOT found in the whitelist
            for(Pattern p : policyList) {
                if (p.matcher(name).matches())
                    return false;
            }
            return true;
        }
        return false;
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
