/**
 * 
 */
package uk.gov.hmcts.befta.featuretoggle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author korneleehenry
 *
 */
class FeatureToggleInfoTest {

    private FeatureToggleInfo featureToggleInfo = null;

    @BeforeEach
    public void setUp() {
        featureToggleInfo = new FeatureToggleInfo();

    }

    @AfterEach
    public void cleanUp() {
        featureToggleInfo = null;
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo#FeatureToggleInfo()}.
     */
    @Test
    void testFeatureToggleInfo() {
        assertNotNull(featureToggleInfo);
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo#add(java.lang.String, java.lang.Boolean)}.
     */
    @Test
    void testAdd() {
        featureToggleInfo.add("Flag", true);
        assertTrue(featureToggleInfo.isAnyEnabled());
        assertTrue(featureToggleInfo.isAllEnabled());
        assertFalse(featureToggleInfo.isAnyDisabled());
        assertFalse(featureToggleInfo.isAllDisabled());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo#isAnyEnabled()}.
     */
    @Test
    void testIsAnyEnabled() {
        featureToggleInfo.add("Flag", false);
        assertFalse(featureToggleInfo.isAnyEnabled());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo#isAllEnabled()}.
     */
    @Test
    void testIsAllEnabled() {
        featureToggleInfo.add("Flag1", true);
        featureToggleInfo.add("Flag2", true);
        assertTrue(featureToggleInfo.isAllEnabled());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo#isAnyDisabled()}.
     */
    @Test
    void testIsAnyDisabled() {
        featureToggleInfo.add("Flag", false);
        assertTrue(featureToggleInfo.isAnyDisabled());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo#isAllDisabled()}.
     */
    @Test
    void testIsAllDisabled() {
        featureToggleInfo.add("Flag1", false);
        featureToggleInfo.add("Flag2", false);
        assertTrue(featureToggleInfo.isAllDisabled());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo#getDisabledFeatureFlags()}.
     */
    @Test
    void testGetDisabledFeatureFlags() {
        List<String> list = Arrays.asList("Flag1", "Flag2");
        featureToggleInfo.add("Flag1", false);
        featureToggleInfo.add("Flag2", false);
        featureToggleInfo.add("Flag3", true);
        featureToggleInfo.add("Flag4", true);
        assertFalse(featureToggleInfo.isAllEnabled());
        assertTrue(featureToggleInfo.getDisabledFeatureFlags().containsAll(list));
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo#getEnabledFeatureFlags()}.
     */
    @Test
    void testGetEnabledFeatureFlags() {
        List<String> list = Arrays.asList("Flag3", "Flag4");
        featureToggleInfo.add("Flag2", false);
        featureToggleInfo.add("Flag3", true);
        featureToggleInfo.add("Flag4", true);
        assertTrue(featureToggleInfo.getEnabledFeatureFlags().containsAll(list));
    }

}
