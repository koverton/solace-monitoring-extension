package com.appdynamics.extensions.solace.semp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for parsing and representing logical attributes of a given SEMP
 * version including Platform (Hardware vs. VMR), Version String and Version Number.
 * Version numbers are represented as Floating point numbers with the major version
 * as the Integer portion, and all further versions concatenated together as the
 * floating point A SEMP version specifies the protocol version-number and the
 * platform type it applies to.
 *
 * For example: VMR versions strings appear as '6_3VMR', while hardware versions
 * appear as '7_2'
 */
public class SempVersion {
    private static final Logger logger = LoggerFactory.getLogger(SempVersion.class);

    public enum Platform {
        APPLIANCE,
        VMR,
        NONE
    }
    public static final String INVALID_VERSION_STR = "INVALID";
    public static final Float  INVALID_VERSION = 0.0f;

    // 4 versions of each going backwards
    public static SempVersion v8_3VMR = new SempVersion(Platform.VMR, "8_3VMR", 8.3f);
    public static SempVersion v8_5VMR = new SempVersion(Platform.VMR, "8_5VMR", 8.5f);
    public static SempVersion v8_6VMR = new SempVersion(Platform.VMR, "8_6VMR", 8.6f);
    public static SempVersion v8_7VMR = new SempVersion(Platform.VMR, "8_7VMR", 8.7f);

    public static SempVersion v7_2_2 = new SempVersion(Platform.APPLIANCE, "7_2_2", 7.22f);
    public static SempVersion v8_0_0 = new SempVersion(Platform.APPLIANCE, "8_0_0", 8.0f);
    public static SempVersion v8_2_0 = new SempVersion(Platform.APPLIANCE, "8_2_0", 8.20f);
    public static SempVersion v8_3_0 = new SempVersion(Platform.APPLIANCE, "8_3_0", 8.30f);

    public static SempVersion INVALID = new SempVersion(Platform.NONE, INVALID_VERSION_STR, INVALID_VERSION);

    public SempVersion(String schemaVersion) {
        // Parse out the version details from the schema version, e.g. "soltr/8_6VMR" or "soltr/8_2_0"
        platform = parsePlatform(schemaVersion);
        versionString = parseVersionString(schemaVersion);
        versionNumber = parseVersionNumber(schemaVersion);
    }
    private SempVersion(Platform p, String versionString, float versionNumber) {
        this.platform = p;
        this.versionString = versionString;
        this.versionNumber = versionNumber;
    }

    public float getVersionNumber() {
        return versionNumber;
    }

    public String getVersionString() {
        return versionString;
    }

    public Platform getPlatform() {
        return platform;
    }

    public boolean isValid() {
        return platform != Platform.NONE
                && versionNumber != INVALID_VERSION
                && (!versionString.equals(INVALID_VERSION_STR));
    }


    private Platform parsePlatform(String schemaVersion) {
        if (!schemaVersion.contains("VMR"))
            return Platform.APPLIANCE;
        // Default to VMR if we don't know
        return Platform.VMR;
    }

    private String parseVersionString(String schemaVersion) {
        int start = schemaVersion.indexOf('/');
        if (start == -1)
            return INVALID_VERSION_STR;
        start++;
        return schemaVersion.substring(start);
    }

    private float parseVersionNumber(String version) {
        // Turn '8_2_0' or '8_6VMR' into '8.20' and '8.6' respectively
        StringBuilder sb = new StringBuilder();
        int decimalCount = 0;
        for(int i = 0; i < version.length(); i++) {
            char c = version.charAt(i);
            // Add 'real' digits to the version string
            if ("0123456789".indexOf(c) != -1)
                sb.append(c);
            else if (c == '_') {
                // Only convert the first '_' into a decimal in the version string
                if (decimalCount == 0) {
                    decimalCount++;
                    sb.append('.');
                }
                // any extra '_' can be skipped
            }
        }
        try {
            return Float.parseFloat(sb.toString());
        }
        catch(NumberFormatException nex) {
            logger.error("INVALID SEMP-VERSION: Exception attempting to parse version version-string: " + sb.toString(), nex);
        }
        return INVALID_VERSION;
    }

    final private float versionNumber;
    final private String versionString;
    final private Platform platform;
}
