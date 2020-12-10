/**
 * 
 */
package uk.gov.hmcts.befta.featuretoggle.launchdarkly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import io.cucumber.java.Scenario;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo;

/**
 * @author korneleehenry
 *
 */
class LaunchDarklyFeatureToggleServiceTest {
    public static final String LD_SDK_KEY = "LD_SDK_KEY";
    public static final String LD_SDK_KEY_VALUE = "LD_SDK_KEY_VALUE";
    private static final String LAUNCH_DARKLY_FLAG = "(FeatureToggle)";
    public static final String MICROSERVICE_NAME = "MICROSERVICE_NAME";
    public static final String MICROSERVICE_NAME_VALUE = "MICROSERVICE_NAME_VALUE";
    public static final String LAUNCH_DARKLY_ENV = "LAUNCH_DARKLY_ENV";
    public static final String LAUNCH_DARKLY_ENV_VALUE = "LAUNCH_DARKLY_ENV_VALUE";

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.launchdarkly.LaunchDarklyFeatureToggleService#getToggleStatusFor(io.cucumber.java.Scenario)}.
     */
    @Test
    @SetEnvironmentVariable(key = LD_SDK_KEY, value = LD_SDK_KEY_VALUE)
    void testGetToggleStatusForEmpty() {
        Scenario scenario = mock(Scenario.class);
        LaunchDarklyFeatureToggleService launchDarklyFeatureToggleService = new LaunchDarklyFeatureToggleService();
        FeatureToggleInfo status = new FeatureToggleInfo();
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            {
                add("S-987");
                add("@S-133");
                add("@F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);
        assertEquals(status.getDisabledFeatureFlags(),
                launchDarklyFeatureToggleService.getToggleStatusFor(scenario).getDisabledFeatureFlags());
        assertEquals(status.getEnabledFeatureFlags(),
                launchDarklyFeatureToggleService.getToggleStatusFor(scenario).getEnabledFeatureFlags());
    }

    @Test
    @SetEnvironmentVariable(key = LD_SDK_KEY, value = LD_SDK_KEY_VALUE)
    void testGetToggleStatusForFeature() {
        Scenario scenario = mock(Scenario.class);
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            {
                add(LAUNCH_DARKLY_FLAG);
                add("S-987");
                add("@S-133");
                add("@F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);
        LaunchDarklyFeatureToggleService launchDarklyFeatureToggleService = new LaunchDarklyFeatureToggleService();

        FeatureToggleCheckFailureException aeThrown = Assertions.assertThrows(FeatureToggleCheckFailureException.class,
                () -> {
                    launchDarklyFeatureToggleService.getToggleStatusFor(scenario);
                });

        assertTrue(aeThrown.getMessage()
                .contains("The Scenario is being skipped as MICROSERVICE_NAME variable is not configured"));
    }

    @Test
    @SetEnvironmentVariable(key = LD_SDK_KEY, value = LD_SDK_KEY_VALUE)
    @SetEnvironmentVariable(key = MICROSERVICE_NAME, value = MICROSERVICE_NAME_VALUE)
    void testGetToggleStatusFor() {
        Scenario scenario = mock(Scenario.class);
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            {
                add(LAUNCH_DARKLY_FLAG);
                add("S-987");
                add("@S-133");
                add("@F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);
        LaunchDarklyFeatureToggleService launchDarklyFeatureToggleService = new LaunchDarklyFeatureToggleService();

        FeatureToggleCheckFailureException aeThrown = Assertions.assertThrows(FeatureToggleCheckFailureException.class,
                () -> {
                    launchDarklyFeatureToggleService.getToggleStatusFor(scenario);
                });

        assertTrue(
                aeThrown.getMessage().contains("The Scenario is being skipped as LAUNCH_DARKLY_ENV is not configured"));
    }

    @Test
    @SetEnvironmentVariable(key = LD_SDK_KEY, value = LD_SDK_KEY_VALUE)
    @SetEnvironmentVariable(key = MICROSERVICE_NAME, value = MICROSERVICE_NAME_VALUE)
    @SetEnvironmentVariable(key = LAUNCH_DARKLY_ENV, value = LAUNCH_DARKLY_ENV_VALUE)
    void testGetToggleStatusForAll() {
        Scenario scenario = mock(Scenario.class);
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            {
                add(LAUNCH_DARKLY_FLAG);
                add("S-987");
                add("S-133");
                add("F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);
        LaunchDarklyFeatureToggleService launchDarklyFeatureToggleService = new LaunchDarklyFeatureToggleService();
        FeatureToggleInfo status = new FeatureToggleInfo();
        status.add("FeatureToggle", false);

        launchDarklyFeatureToggleService.getToggleStatusFor(scenario);
        assertEquals(status.getDisabledFeatureFlags(),
                launchDarklyFeatureToggleService.getToggleStatusFor(scenario).getDisabledFeatureFlags());
        assertEquals(status.getEnabledFeatureFlags(),
                launchDarklyFeatureToggleService.getToggleStatusFor(scenario).getEnabledFeatureFlags());

    }

}
