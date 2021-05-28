package uk.gov.hmcts.befta.featuretoggle;

import io.cucumber.java.Scenario;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import uk.gov.hmcts.befta.featuretoggle.launchdarkly.LaunchDarklyFeatureToggleService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

public class DefaultMultiSourceFeatureToggleServiceTest {
    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.DefaultMultiSourceFeatureToggleService#getToggleStatusFor(Scenario)}.
     */
    @Test
    
    void shouldIgnoreNonFeatureToggleAnnotations() {
        Scenario scenario = mock(Scenario.class);
        DefaultMultiSourceFeatureToggleService service = new DefaultMultiSourceFeatureToggleService();

        ScenarioFeatureToggleInfo scenarioFeatureToggleInfo = service.getToggleStatusFor(scenario);
        assertEquals(0, scenarioFeatureToggleInfo.getDisabledFeatureFlags().size());
        assertEquals(0, scenarioFeatureToggleInfo.getEnabledFeatureFlags().size());
        assertFalse(scenarioFeatureToggleInfo.isAnyDisabled());
        assertFalse(scenarioFeatureToggleInfo.isAnyEnabled());
    }
}
