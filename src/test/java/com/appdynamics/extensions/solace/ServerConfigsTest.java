package com.appdynamics.extensions.solace;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ServerConfigsTest {
    private final static String[] diseaseArray = new String[] {"Botulism", "Salmonella", "Anthrax"};
    private final static String[] patternArray = new String[] {"^Botu.*", "S[^o]*onella$", "^A.*$"};
    private final static List<Pattern> diseaseList = new ArrayList<>();
    private final static List<Pattern> patternList = new ArrayList<>();

    @BeforeClass
    public static void setup() {
        for(String d : diseaseArray) {
            diseaseList.add(Pattern.compile(d));
        }
        for(String p : patternArray) {
            patternList.add(Pattern.compile(p));
        }
    }

    @Test
    public void parseWhitelistTest() {
        assertEquals(MonitorConfigs.ExclusionPolicy.WHITELIST, Helper.parseExclusionPolicy("whitelist"));
        assertEquals(MonitorConfigs.ExclusionPolicy.WHITELIST, Helper.parseExclusionPolicy("WhItElIsT"));
    }

    @Test
    public void parseBlacklistTest() {
        assertEquals(MonitorConfigs.ExclusionPolicy.BLACKLIST, Helper.parseExclusionPolicy("blacklist"));
        assertEquals(MonitorConfigs.ExclusionPolicy.BLACKLIST, Helper.parseExclusionPolicy("bLaCkLiSt"));
    }

    @Test
    public void unrecognizedExclusionPolicyDefaultTest() {
        assertEquals(MonitorConfigs.ExclusionPolicy.BLACKLIST, Helper.parseExclusionPolicy("Sarrusophone"));
    }

    @Test
    public void blacklistTest() {
        for(String disease : diseaseArray) {
            assertTrue( Helper.isExcluded(disease, diseaseList, MonitorConfigs.ExclusionPolicy.BLACKLIST));
            assertTrue( Helper.isExcluded(disease, patternList, MonitorConfigs.ExclusionPolicy.BLACKLIST));
        }
    }

    @Test
    public void blacklistNegativeTest() {
        assertFalse( Helper.isExcluded("Bubonic Plague", diseaseList, MonitorConfigs.ExclusionPolicy.BLACKLIST) );
        assertFalse( Helper.isExcluded("Bubonic Plague", patternList, MonitorConfigs.ExclusionPolicy.BLACKLIST) );
    }

    @Test
    public void whitelistTest() {
        for(String disease : diseaseArray) {
            assertFalse( Helper.isExcluded(disease, diseaseList, MonitorConfigs.ExclusionPolicy.WHITELIST));
            assertFalse( Helper.isExcluded(disease, patternList, MonitorConfigs.ExclusionPolicy.WHITELIST));
        }
    }

    @Test
    public void whitelistNegativeTest() {
        assertTrue( Helper.isExcluded("Bubonic Plague", diseaseList, MonitorConfigs.ExclusionPolicy.WHITELIST) );
        assertTrue( Helper.isExcluded("Bubonic Plague", patternList, MonitorConfigs.ExclusionPolicy.WHITELIST) );
    }

    @Test
    public void emptyLstTest() {
        assertFalse( Helper.isExcluded("Ebola", new ArrayList<>(), MonitorConfigs.ExclusionPolicy.BLACKLIST) );
        assertTrue( Helper.isExcluded("Ebola", new ArrayList<>(), MonitorConfigs.ExclusionPolicy.WHITELIST) );
    }

    @Test
    public void parseRedundancyTest() {
        assertEquals(MonitorConfigs.RedundancyModel.REDUNDANT, Helper.parseRedundancyModel("redundant"));
        assertEquals(MonitorConfigs.RedundancyModel.REDUNDANT, Helper.parseRedundancyModel("rEdUnDaNt"));
        assertEquals(MonitorConfigs.RedundancyModel.STANDALONE, Helper.parseRedundancyModel("Standalone"));
        assertEquals(MonitorConfigs.RedundancyModel.STANDALONE, Helper.parseRedundancyModel(""));
        assertEquals(MonitorConfigs.RedundancyModel.STANDALONE, Helper.parseRedundancyModel(null));
    }
}
