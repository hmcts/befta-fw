/**
 * 
 */
package uk.gov.hmcts.befta.featuretoggle.launchdarkly;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

/**
 * @author korneleehenry
 *
 */
class LaunchDarklyConfigTest {
    public static final String LD_SDK_KEY = "LD_SDK_KEY";
    public static final String LAUNCH_DARKLY_ENV = "LAUNCH_DARKLY_ENV";
    public static final String MICROSERVICE_NAME = "MICROSERVICE_NAME";
    public static final String LD_SDK_KEY_VALUE = "LD_SDK_KEY_VALUE";
    public static final String LAUNCH_DARKLY_ENV_VALUE = "LAUNCH_DARKLY_ENV_VALUE";
    public static final String MICROSERVICE_NAME_VALUE = "MICROSERVICE_NAME_VALUE";

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.launchdarkly.LaunchDarklyConfig#getLdInstance()}.
     */
    @Test
    @SetEnvironmentVariable(key = LD_SDK_KEY, value = LD_SDK_KEY_VALUE)
    void testGetLdInstance() {
        assertNotNull(LaunchDarklyConfig.getLdInstance());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.launchdarkly.LaunchDarklyConfig#getEnvironmentName()}.
     */
    @Test
    @SetEnvironmentVariable(key = LAUNCH_DARKLY_ENV, value = LAUNCH_DARKLY_ENV_VALUE)
    void testGetEnvironmentName() {
        assertEquals(LAUNCH_DARKLY_ENV_VALUE, LaunchDarklyConfig.getEnvironmentName());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.launchdarkly.LaunchDarklyConfig#getLDMicroserviceName()}.
     */
    @Test
    @SetEnvironmentVariable(key = MICROSERVICE_NAME, value = MICROSERVICE_NAME_VALUE)
    void testGetLDMicroserviceName() {
        assertEquals(MICROSERVICE_NAME_VALUE, LaunchDarklyConfig.getLDMicroserviceName());
    }

}
