package uk.gov.hmcts.befta.featuretoggle;

import com.launchdarkly.sdk.server.LDClient;
import io.cucumber.java.Scenario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
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
    void shouldProcessTheFeatureToggleAnnotationWithCombinationOfAnnotationsScenario1() {
        Scenario scenario = mock(Scenario.class);
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 2021L;

            {
                add(LAUNCH_DARKLY_FLAG + "(flagName)");
                add("S-133");
                add("F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);

        LDClient ldClient = mock(LDClient.class);
        when(ldClient.boolVariation(anyString(), any(), anyBoolean())).thenReturn(true);

        DefaultMultiSourceFeatureToggleService service = new DefaultMultiSourceFeatureToggleService();
        ScenarioFeatureToggleInfo toggleInfo = service.getToggleStatusFor(scenario);
        assertFalse(toggleInfo.isAnyEnabled());
        assertTrue(toggleInfo.isAnyDisabled());
        assertEquals("flagName", toggleInfo.getDisabledFeatureFlags().get(0));
        assertNotNull(toggleInfo);
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.DefaultMultiSourceFeatureToggleService#getToggleStatusFor(Scenario)}.
     */
    @Test
    void shouldProcessTheFeatureToggleAnnotationWithCombinationOfAnnotationsScenario2() {
        Scenario scenario = mock(Scenario.class);
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 2021L;

            {
                add(LAUNCH_DARKLY_FLAG + "(LD:flagName)");
                add("S-133");
                add("F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);

        LDClient ldClient = mock(LDClient.class);
        when(ldClient.boolVariation(anyString(), any(), anyBoolean())).thenReturn(true);

        DefaultMultiSourceFeatureToggleService service = new DefaultMultiSourceFeatureToggleService();
        ScenarioFeatureToggleInfo toggleInfo = service.getToggleStatusFor(scenario);
        assertFalse(toggleInfo.isAnyEnabled());
        assertTrue(toggleInfo.isAnyDisabled());
        assertEquals("flagName", toggleInfo.getDisabledFeatureFlags().get(0));
        assertNotNull(toggleInfo);
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.DefaultMultiSourceFeatureToggleService#getToggleStatusFor(Scenario)}.
     */
    @Test
    void shouldProcessTheFeatureToggleAnnotationWithCombinationOfAnnotationsScenario3() {
        Scenario scenario = mock(Scenario.class);
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 2021L;

            {
                add(LAUNCH_DARKLY_FLAG + "(LD:flagName=on)");
                add("S-133");
                add("F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);

        LDClient ldClient = mock(LDClient.class);
        when(ldClient.boolVariation(anyString(), any(), anyBoolean())).thenReturn(true);

        DefaultMultiSourceFeatureToggleService service = new DefaultMultiSourceFeatureToggleService();
        ScenarioFeatureToggleInfo toggleInfo = service.getToggleStatusFor(scenario);
        assertFalse(toggleInfo.isAnyEnabled());
        assertTrue(toggleInfo.isAnyDisabled());
        assertEquals("flagName", toggleInfo.getDisabledFeatureFlags().get(0));
        assertNotNull(toggleInfo);
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.DefaultMultiSourceFeatureToggleService#getToggleStatusFor(Scenario)}.
     */
    @Test
    void shouldProcessTheFeatureToggleAnnotationWithCombinationOfAnnotationsScenario4() {
        Scenario scenario = mock(Scenario.class);
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 2021L;

            {
                add(LAUNCH_DARKLY_FLAG + "(LD:flagName=off)");
                add("S-133");
                add("F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);

        LDClient ldClient = mock(LDClient.class);
        when(ldClient.boolVariation(anyString(), any(), anyBoolean())).thenReturn(true);

        DefaultMultiSourceFeatureToggleService service = new DefaultMultiSourceFeatureToggleService();
        ScenarioFeatureToggleInfo toggleInfo = service.getToggleStatusFor(scenario);
        assertFalse(toggleInfo.isAnyEnabled());
        assertTrue(toggleInfo.isAnyDisabled());
        assertEquals("flagName", toggleInfo.getDisabledFeatureFlags().get(0));
        assertNotNull(toggleInfo);
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.featuretoggle.DefaultMultiSourceFeatureToggleService#getToggleStatusFor(Scenario)}.
     */
    @Test
    void shouldProcessTheFeatureToggleAnnotationWithCombinationOfAnnotationsScenario5() {
        Scenario scenario = mock(Scenario.class);
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 2021L;

            {
                add(LAUNCH_DARKLY_FLAG + "(LD:flagName=on)");
                add(LAUNCH_DARKLY_FLAG + "(LD:flagName2=off)");
                add("S-133");
                add("F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);

        LDClient ldClient = mock(LDClient.class);
        when(ldClient.boolVariation(anyString(), any(), anyBoolean())).thenReturn(true);

        DefaultMultiSourceFeatureToggleService service = new DefaultMultiSourceFeatureToggleService();
        ScenarioFeatureToggleInfo toggleInfo = service.getToggleStatusFor(scenario);
        assertFalse(toggleInfo.isAnyEnabled());
        assertTrue(toggleInfo.isAnyDisabled());
        assertEquals(2, toggleInfo.getDisabledFeatureFlags().size());
        assertNotNull(toggleInfo);
    }
}
