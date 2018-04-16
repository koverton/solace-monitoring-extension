package com.appdynamics.extensions.solace;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class HelperTest {
    private final String[] diseaseArray = new String[] {"Botulism", "Salmonella", "Anthrax"};
    private final List<String> policyList = Arrays.asList(diseaseArray);

    @Test
    public void blacklistTest() {
        for(String disease : diseaseArray) {
            assertTrue( Helper.isExcluded(disease, policyList, MonitorConfigs.ExclusionPolicy.BLACKLIST));
        }
    }

    @Test
    public void blacklistNegativeTest() {
        assertFalse( Helper.isExcluded("Bubonic Plague", policyList, MonitorConfigs.ExclusionPolicy.BLACKLIST) );
    }

    @Test
    public void whitelistTest() {
        for(String disease : diseaseArray) {
            assertFalse( Helper.isExcluded(disease, policyList, MonitorConfigs.ExclusionPolicy.WHITELIST));
        }
    }

    @Test
    public void whitelistNegativeTest() {
        assertTrue( Helper.isExcluded("Bubonic Plague", policyList, MonitorConfigs.ExclusionPolicy.WHITELIST) );
    }

    @Test
    public void emptyLstTest() {
        assertFalse( Helper.isExcluded("Ebola", new ArrayList<>(), MonitorConfigs.ExclusionPolicy.BLACKLIST) );
        assertTrue( Helper.isExcluded("Ebola", new ArrayList<>(), MonitorConfigs.ExclusionPolicy.WHITELIST) );
    }
}
