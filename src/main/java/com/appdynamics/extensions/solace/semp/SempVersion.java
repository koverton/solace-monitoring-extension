package com.appdynamics.extensions.solace.semp;

public class SempVersion {
    public enum Platform {
        APPLIANCE,
        VMR
    }

    // 4 versions of each going backwards
    public static SempVersion v8_3VMR = new SempVersion(Platform.VMR, "8_3VMR", 8.3f);
    public static SempVersion v8_5VMR = new SempVersion(Platform.VMR, "8_5VMR", 8.5f);
    public static SempVersion v8_6VMR = new SempVersion(Platform.VMR, "8_6VMR", 8.6f);
    public static SempVersion v8_7VMR = new SempVersion(Platform.VMR, "8_7VMR", 8.7f);

    public static SempVersion v7_2_1 = new SempVersion(Platform.APPLIANCE, "7_2_1", 7.21f);
    public static SempVersion v8_0_0 = new SempVersion(Platform.APPLIANCE, "8_0_0", 8.0f);
    public static SempVersion v8_2_0 = new SempVersion(Platform.APPLIANCE, "8_2_0", 8.20f);
    public static SempVersion v8_3_0 = new SempVersion(Platform.APPLIANCE, "8_3_0", 8.30f);

    public SempVersion(String schemaVersion) throws IllegalArgumentException {
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

    private Platform parsePlatform(String schemaVersion) {
        if (schemaVersion.indexOf("VMR") == -1)
            return Platform.APPLIANCE;
        // Default to VMR if we don't know
        return Platform.VMR;
    }

    private String parseVersionString(String schemaVersion) throws IllegalArgumentException {
        int start = schemaVersion.indexOf('/');
        if (start == -1)
            throw new IllegalArgumentException("Invalid schemaVersion string");
        start++;
        return schemaVersion.substring(start);
    }

    private float parseVersionNumber(String version) throws IllegalArgumentException {
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
            throw new IllegalArgumentException("Invalid version number string");
        }
    }

    final private float versionNumber;
    final private String versionString;
    final private Platform platform;
}
