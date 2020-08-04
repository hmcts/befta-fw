package uk.gov.hmcts.befta.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import io.cucumber.java.Scenario;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;
import uk.gov.hmcts.common.TestUtils;

public class BackEndFunctionalTestScenarioContextTest {

    private final String VALID_TAG_ID = "S-133";

    private BackEndFunctionalTestScenarioContext contextUnderTest = new BackEndFunctionalTestScenarioContext();

    @Mock
    private HttpTestData s103TestData;

    @Mock
    private Scenario scenario;

    @Mock
    private JsonStoreHttpTestDataSource dataSource;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(dataSource.getDataForTestCall(VALID_TAG_ID)).thenReturn(s103TestData);
        when(dataSource.getDataForTestCall("S-133,S-456")).thenReturn(s103TestData);

        TestUtils.setFinalStatic(BackEndFunctionalTestScenarioContext.class.getDeclaredField("DATA_SOURCE"),
                dataSource);

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
        contextUnderTest.initializeTestDataFor(scenario);

        String result = contextUnderTest.getCurrentScenarioTag();

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
        contextUnderTest.initializeTestDataFor(scenario);

        String result = contextUnderTest.getCurrentScenarioTag();

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
        contextUnderTest.addChildContext(testChildContext);

        // ASSERT
        assertTrue(contextUnderTest.getChildContexts().containsKey(testDataId));
        assertEquals(testChildContext, contextUnderTest.getChildContexts().get(testDataId));
        assertEquals(testDataId, testChildContext.getContextId());
    }

    @Test
    public void shouldAddChildContextWithContextId() {
        // ARRANGE
        final String testContextId = "TEST_CONTEXT_ID";

        // ACT
        BackEndFunctionalTestScenarioContext testChildContext = new BackEndFunctionalTestScenarioContext();
        contextUnderTest.addChildContext(testContextId, testChildContext);

        // ASSERT
        assertTrue(contextUnderTest.getChildContexts().containsKey(testContextId));
        assertEquals(testChildContext, contextUnderTest.getChildContexts().get(testContextId));
        assertEquals(testContextId, testChildContext.getContextId());
    }

    @Test
    public void shouldGetBlankContextIdIfContextIdAndTestDataNotSet() {
        // ARRANGE
        contextUnderTest = new BackEndFunctionalTestScenarioContext();

        // ACT
        String result = contextUnderTest.getContextId();

        // ASSERT
        assertEquals("", result);
    }

    @Test
    public void shouldGetContextIdFromTestDataIdIfContextIdNotSet() throws Exception {
        // ARRANGE
        final String testDataId = "TD_GUID";
        when(s103TestData.get_guid_()).thenReturn(testDataId);
        whenNew(HttpTestData.class).withArguments(ArgumentMatchers.any(HttpTestData.class)).thenReturn(s103TestData);
        contextUnderTest.initializeTestDataFor(VALID_TAG_ID);

        // ACT
        String result = contextUnderTest.getContextId();

        // ASSERT
        assertEquals(testDataId, result);
    }

    @Test
    public void shouldGetContextIdIfContextIdIsSet() {
        // ARRANGE
        final String testContextId = "TEST_CONTEXT_ID";
        contextUnderTest.setContextId(testContextId);

        // ACT
        String result = contextUnderTest.getContextId();

        // ASSERT
        assertEquals(testContextId, result);
    }
}
