package uk.gov.hmcts.befta.featuretoggle;

import io.cucumber.java.Scenario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultMultiSourceFeatureToggleServiceTest {

    private static final String LAUNCH_DARKLY_FLAG = "@FeatureToggle";
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

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.DefaultMultiSourceFeatureToggleService#getToggleStatusFor(Scenario)}.
     */
    @Test
    void shouldThrowExceptionWhenNonLaunchDarklyDomainIsProcessed() {
        Scenario scenario = mock(Scenario.class);
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 2021L;

            {
                add(LAUNCH_DARKLY_FLAG + "(dummyDomain:flagName)");
                add("S-133");
                add("F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);
        DefaultMultiSourceFeatureToggleService service = new DefaultMultiSourceFeatureToggleService();

        FeatureToggleCheckFailureException aeThrown = Assertions.assertThrows(FeatureToggleCheckFailureException.class,
                () -> service.getToggleStatusFor(scenario));

        assertTrue(aeThrown.getMessage()
                .contains("Doesn't know FeatureToggleService for Domain dummyDomain"));
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.DefaultMultiSourceFeatureToggleService#getToggleStatusFor(Scenario)}.
     */
    @Test
    void shouldProcess() {
        Scenario scenario = mock(Scenario.class);
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 2021L;

            {
                add(LAUNCH_DARKLY_FLAG + "(dummyDomain:flagName)");
                add("S-133");
                add("F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);
        DefaultMultiSourceFeatureToggleService service = new DefaultMultiSourceFeatureToggleService();

        FeatureToggleCheckFailureException aeThrown = Assertions.assertThrows(FeatureToggleCheckFailureException.class,
                () -> service.getToggleStatusFor(scenario));

        assertTrue(aeThrown.getMessage()
                .contains("Doesn't know FeatureToggleService for Domain dummyDomain"));
    }
}
