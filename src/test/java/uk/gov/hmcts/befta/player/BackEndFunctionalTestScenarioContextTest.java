package uk.gov.hmcts.befta.player;

import io.cucumber.java.Scenario;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        BackEndFunctionalTestScenarioContext.class,
        JsonStoreHttpTestDataSource.class,
        Scenario.class
})
public class BackEndFunctionalTestScenarioContextTest {

    private BackEndFunctionalTestScenarioContext context;

    @Mock
    private JsonStoreHttpTestDataSource dataSource;

    @Mock
    private Scenario scenario;

    private static final Collection<String> VALID_TAGS = Collections.singletonList("@S-133");

    @Before
    public void setUp() throws Exception {
        whenNew(JsonStoreHttpTestDataSource.class).withAnyArguments().thenReturn(dataSource);
        context = new BackEndFunctionalTestScenarioContext();
    }

    @Test
    public void shouldInitializeTestDataForScenario() {
        when(scenario.getSourceTagNames()).thenReturn(VALID_TAGS);

        context.initializeTestDataFor(scenario);

        verify(dataSource).getDataForTestCall(eq("S-133"));
    }

    @Test
    public void shouldGetCurrentScenarioTagForCorrectPrefixOnly() {
        final Collection<String> tags = new ArrayList<String>() {{
            add("@A-133");
            add("S-987");
            add("@S-133");
            add("@F-103");
        }};
        when(scenario.getSourceTagNames()).thenReturn(tags);
        context.initializeTestDataFor(scenario);

        String result = context.getCurrentScenarioTag();

        assertEquals("S-133", result);
    }

    @Test
    public void shouldGetCurrentScenarioTagWithMultipleScenarios() {
        final Collection<String> tags = new ArrayList<String>() {{
            add("@S-133");
            add("@S-456");
        }};
        when(scenario.getSourceTagNames()).thenReturn(tags);
        context.initializeTestDataFor(scenario);

        String result = context.getCurrentScenarioTag();

        assertEquals("S-133,S-456", result);
    }
}
