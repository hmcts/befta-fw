package uk.gov.hmcts.befta.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;

import io.cucumber.java.Scenario;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;

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
    private HttpTestData s103TestData;

    @Mock
    private Scenario scenario;

    private static final String VALID_TAG_ID = "S-133";

    @Before
    public void setUp() throws Exception {
        whenNew(JsonStoreHttpTestDataSource.class).withAnyArguments().thenReturn(dataSource);
        context = new BackEndFunctionalTestScenarioContext();
        when(dataSource.getDataForTestCall(VALID_TAG_ID)).thenReturn(s103TestData);
        when(dataSource.getDataForTestCall("S-133,S-456")).thenReturn(s103TestData);
    }

    @Test
    public void shouldGetCurrentScenarioTagForCorrectPrefixOnly() {
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            {
                add("@A-133");
                add("S-987");
                add("@S-133");
                add("@F-103");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);
        context.initializeTestDataFor(scenario);

        String result = context.getCurrentScenarioTag();

        assertEquals(VALID_TAG_ID, result);
    }

    @Test
    public void shouldGetCurrentScenarioTagWithMultipleScenarios() {
        final Collection<String> tags = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            {
                add("@S-133");
                add("@S-456");
            }
        };
        when(scenario.getSourceTagNames()).thenReturn(tags);
        context.initializeTestDataFor(scenario);

        String result = context.getCurrentScenarioTag();

        assertEquals("S-133,S-456", result);
    }

    @Test
    public void shouldAddChildContextUsingTestDataIdAsContextId() throws Exception {
        // ARRANGE
        final String testDataId = "TD_GUID";
        when(s103TestData.get_guid_()).thenReturn(testDataId);
        whenNew(HttpTestData.class).withArguments(ArgumentMatchers.any(HttpTestData.class)).thenReturn(s103TestData);

        // ACT
        BackEndFunctionalTestScenarioContext testChildContext = new BackEndFunctionalTestScenarioContext();
        testChildContext.initializeTestDataFor(VALID_TAG_ID);
        context.addChildContext(testChildContext);

        // ASSERT
        assertTrue(context.getChildContexts().containsKey(testDataId));
        assertEquals(testChildContext, context.getChildContexts().get(testDataId));
        assertEquals(testDataId, testChildContext.getContextId());
    }

    @Test
    public void shouldAddChildContextWithContextId() {
        // ARRANGE
        final String testContextId = "TEST_CONTEXT_ID";

        // ACT
        BackEndFunctionalTestScenarioContext testChildContext = new BackEndFunctionalTestScenarioContext();
        context.addChildContext(testContextId, testChildContext);

        // ASSERT
        assertTrue(context.getChildContexts().containsKey(testContextId));
        assertEquals(testChildContext, context.getChildContexts().get(testContextId));
        assertEquals(testContextId, testChildContext.getContextId());
    }

    @Test
    public void shouldGetBlankContextIdIfContextIdAndTestDataNotSet() {
        // ARRANGE
        context = new BackEndFunctionalTestScenarioContext();

        // ACT
        String result = context.getContextId();

        // ASSERT
        assertEquals("", result);
    }

    @Test
    public void shouldGetContextIdFromTestDataIdIfContextIdNotSet() throws Exception {
        // ARRANGE
        final String testDataId = "TD_GUID";
        when(s103TestData.get_guid_()).thenReturn(testDataId);
        whenNew(HttpTestData.class).withArguments(ArgumentMatchers.any(HttpTestData.class)).thenReturn(s103TestData);
        context.initializeTestDataFor(VALID_TAG_ID);

        // ACT
        String result = context.getContextId();

        // ASSERT
        assertEquals(testDataId, result);
    }

    @Test
    public void shouldGetContextIdIfContextIdIsSet() {
        // ARRANGE
        final String testContextId = "TEST_CONTEXT_ID";
        context.setContextId(testContextId);

        // ACT
        String result = context.getContextId();

        // ASSERT
        assertEquals(testContextId, result);
    }
}
