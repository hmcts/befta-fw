package uk.gov.hmcts.befta.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import io.cucumber.java.Scenario;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;
import uk.gov.hmcts.common.TestUtils;

public class BackEndFunctionalTestScenarioContextTest {

    public static final String DEFINITION_STORE_HOST_KEY = "DEFINITION_STORE_HOST";
	public static final String DEFINITION_STORE_HOST_VALUE = "http://127.0.0.1:8089/";
	public static final String IDAM_URL_KEY = "IDAM_URL";
	public static final String IDAM_URL_VALUE = "IDAM_URL_VALUE";
	public static final String S2S_URL_KEY = "S2S_URL";
	public static final String S2S_URL_VALUE = "S2S_URL_VALUE";
	public static final String BEFTA_S2S_CLIENT_ID_KEY = "BEFTA_S2S_CLIENT_ID";
	public static final String BEFTA_S2S_CLIENT_ID_VALUE = "BEFTA_S2S_CLIENT_ID_VALUE";
	public static final String BEFTA_S2S_CLIENT_SECRET_KEY = "BEFTA_S2S_CLIENT_SECRET";
	public static final String BEFTA_S2S_CLIENT_SECRET_VALUE = "BEFTA_S2S_CLIENT_SECRET_VALUE";


    private static final String VALID_TAG_ID = "S-133";

    private BackEndFunctionalTestScenarioContext contextUnderTest = new BackEndFunctionalTestScenarioContext();
    private MockedStatic <BeftaMain> beftaMain = null;
    @Mock
    private HttpTestData s103TestData;

    @Mock
    private Scenario scenario;

    @Mock
    private JsonStoreHttpTestDataSource dataSource;
    public void prepareStaticMockedObjectUnderTest() {
        try {
        	beftaMain = mockStatic(BeftaMain.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void closeStaticMockedObjectUnderTest() {
        try {
        	beftaMain.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(dataSource.getDataForTestCall(VALID_TAG_ID)).thenReturn(s103TestData);
        when(dataSource.getDataForTestCall("S-133,S-456")).thenReturn(s103TestData);

        TestUtils.setFieldWithReflection(BackEndFunctionalTestScenarioContext.class.getDeclaredField("DATA_SOURCE"),
                dataSource);
        closeStaticMockedObjectUnderTest();
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
    public void shouldAddChildContextUsingTestDataIdAsContextId() {
        // ARRANGE
        final String testDataId = "TD_GUID";
        when(s103TestData.get_guid_()).thenReturn(testDataId);

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
    public void shouldGetContextIdFromTestDataIdIfContextIdNotSet() {
        // ARRANGE
        final String testDataId = "TD_GUID";
        when(s103TestData.get_guid_()).thenReturn(testDataId);
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
//    @Test
//    public void testcalculateCustomValue() {
//        // ARRANGE
//        final String testContextId = "TEST_CONTEXT_ID";
//        contextUnderTest.setContextId(testContextId);
//        String today = "today";
//        TestAutomationAdapter tAdapter = mock(TestAutomationAdapter.class);
//        BackEndFunctionalTestScenarioContext context = new BackEndFunctionalTestScenarioContext();
//        when(BeftaMain.getAdapter()).thenReturn(tAdapter);
//        when(tAdapter.calculateCustomValue(context, today)).thenReturn(today);
//        // ACT
//        String result = contextUnderTest.getContextId();
//
//        // ASSERT
//        assertEquals(testContextId, result);
//        ;
//        assertNotNull(context.calculateCustomValue(today));
//    }
}
