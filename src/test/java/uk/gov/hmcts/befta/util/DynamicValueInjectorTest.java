package uk.gov.hmcts.befta.util;

import static org.mockito.Mockito.mockStatic;

import org.apache.logging.log4j.util.Strings;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;
import uk.gov.hmcts.befta.factory.DynamicValueInjectorFactory;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

public class DynamicValueInjectorTest {

    private static final String[] TEST_DATA_RESOURCE_PACKAGES = { "framework-test-data" };
    private static final HttpTestDataSource TEST_DATA_RESOURCE = new JsonStoreHttpTestDataSource(
            TEST_DATA_RESOURCE_PACKAGES);

    private BackEndFunctionalTestScenarioContext scenarioContext;

    @Mock
    private DefaultTestAutomationAdapter taAdapter;

    private MockedStatic<EnvironmentVariableUtils> environmentVariableUtilsMock = null;

    @BeforeEach
    public void prepareMockedObjectUnderTest() {
        try {
            environmentVariableUtilsMock = mockStatic(EnvironmentVariableUtils.class);
            prepareScenarioConext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void closeMockedObjectUnderTest() {
        try {
            scenarioContext = null;
            environmentVariableUtilsMock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({ "deprecation" })
    private void prepareScenarioConext() {
        MockitoAnnotations.initMocks(this);
        scenarioContext = new BackEndFunctionalTestScenarioContextForTest();

        scenarioContext.initializeTestDataFor("Simple-Test-Data-With-All-Possible-Dynamic-Values");

        BackEndFunctionalTestScenarioContext subcontext = new BackEndFunctionalTestScenarioContextForTest();
        subcontext.initializeTestDataFor("Token_Creation_Call");
        subcontext.getTestData().setActualResponse(subcontext.getTestData().getExpectedResponse());

        scenarioContext.setTheInvokingUser(scenarioContext.getTestData().getInvokingUser());
        scenarioContext.addChildContext(subcontext);
    }

    @Test
    public void shouldInjectAllFormulaValues() {

        HttpTestData testData = scenarioContext.getTestData();

        DynamicValueInjector underTest = DynamicValueInjectorFactory.create(taAdapter, testData, scenarioContext);

        Assert.assertEquals("[[DEFAULT_AUTO_VALUE]]", testData.getRequest().getPathVariables().get("uid"));

        underTest.injectDataFromContextBeforeApiCall();
        // Mocking
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("S2S_URL")).thenReturn("http://s2s.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("IDAM_URL")).thenReturn("http://idam.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("IDAM_USER_URL"))
                .thenReturn("http://idamuser.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("DEFINITION_STORE_HOST"))
                .thenReturn("http://defstore.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("CCD_CASEWORKER_AUTOTEST_PASSWORD"))
                .thenReturn("PassQ@rT");

        // Mocked behavior
        Assert.assertEquals("mutlu.sancaktutar@hmcts.net", testData.getRequest().getPathVariables().get("email"));

        Assert.assertEquals("token value", testData.getRequest().getPathVariables().get("token"));
        Assert.assertEquals("token value at index 2", testData.getRequest().getPathVariables().get("token_2"));
        Assert.assertEquals("token value", testData.getRequest().getBody().get("event_token"));
    }

    @Test
    public void shouldInjectAllEnvironmentAndFormulaVariables() {

        scenarioContext = new BackEndFunctionalTestScenarioContextForTest();

        scenarioContext.initializeTestDataFor("Complex-Test-Data-With-All-Possible-Dynamic-Values");

        BackEndFunctionalTestScenarioContext subcontext = new BackEndFunctionalTestScenarioContextForTest();
        subcontext.initializeTestDataFor("Token_Creation_Call_For_Complex_Data");
        subcontext.getTestData().setActualResponse(subcontext.getTestData().getExpectedResponse());

        scenarioContext.setTheInvokingUser(scenarioContext.getTestData().getInvokingUser());
        scenarioContext.addChildContext(subcontext);

        HttpTestData testData = scenarioContext.getTestData();

        DynamicValueInjector underTest = DynamicValueInjectorFactory.create(taAdapter, testData, scenarioContext);


        // Mocking
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("S2S_URL")).thenReturn("http://s2s.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("IDAM_URL")).thenReturn("http://idam.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("IDAM_USER_URL"))
                .thenReturn("http://idamuser.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("DEFINITION_STORE_HOST"))
                .thenReturn("http://defstore.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("CCD_CASEWORKER_AUTOTEST_PASSWORD"))
                .thenReturn("PassQ@rT");

        // Mocked behavior
        Assert.assertEquals("[[DEFAULT_AUTO_VALUE]]", testData.getRequest().getPathVariables().get("uid"));

        underTest.injectDataFromContextBeforeApiCall();
        Assert.assertEquals(
                "a.user@http://idam.hmcts.bla.bla/token value at index 2#http://idamuser.hmcts.bla.bla/documents/binary",
                testData.getRequest().getPathVariables().get("dummyComplexPathVariable"));

        Assert.assertEquals("http://idam.hmcts.bla.bla/documents/binary",
                testData.getRequest().getPathVariables().get("binanyUrlOverIdam"));
        Assert.assertEquals("http://idam.hmcts.bla.bla", testData.getRequest().getPathVariables().get("justIdamUrl"));
        Assert.assertEquals("http://idamuser.hmcts.bla.bla/documents/binary",
                testData.getRequest().getBody().get("event_token"));

        Assert.assertNull(testData.getRequest().getBody().get("nullValueField"));
        Assert.assertEquals(Strings.EMPTY, testData.getExpectedResponse().getBody().get("emptyString"));
        Assert.assertEquals(4.6, testData.getExpectedResponse().getBody().get("onlyStaticNumber"));
        Assert.assertEquals("string without any dynamic part",
                testData.getExpectedResponse().getBody().get("onlyStaticString"));
        Assert.assertEquals("token value at index 2", testData.getExpectedResponse().getBody().get("onlyFormulaOnly"));
        Assert.assertEquals("{{DEFINITION_STORE_HOST}}",
                testData.getExpectedResponse().getBody().get("oneEnvironmentVariableOnly"));
        Assert.assertEquals("{{DEFINITION_STORE_HOST}}Pa55word11{{DEFINITION_STORE_HOST}}",
                testData.getExpectedResponse().getBody().get("threeEnvironmentVariablesOnly"));
        Assert.assertEquals("token value at index 2token value at index 2",
                testData.getExpectedResponse().getBody().get("twoFormulasOnly"));
        Assert.assertEquals("token value at index 2{{DEFINITION_STORE_HOST}}abc123{{DEFINITION_STORE_HOST}}",
                testData.getExpectedResponse().getBody().get("complicatedNestedValue_1"));
        Assert.assertEquals(
                "abctoken value at index 2.=.{{DEFINITION_STORE_HOST}}token value at index 2abc123{{DEFINITION_STORE_HOST}}",
                testData.getExpectedResponse().getBody().get("complicatedNestedValue_2"));
        underTest.injectDataFromContextAfterApiCall();
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("IDAM_URL")).thenReturn("http://idam.hmcts.bla.bla");

        Assert.assertEquals("http://defstore.hmcts.bla.blaPa55word11http://defstore.hmcts.bla.bla",
                testData.getExpectedResponse().getBody().get("threeEnvironmentVariablesOnly"));
        Assert.assertEquals("token value at index 2http://defstore.hmcts.bla.blaabc123http://defstore.hmcts.bla.bla",
                testData.getExpectedResponse().getBody().get("complicatedNestedValue_1"));
        Assert.assertEquals(
                "abctoken value at index 2.=.http://defstore.hmcts.bla.blatoken value at index 2abc123http://defstore.hmcts.bla.bla",
                testData.getExpectedResponse().getBody().get("complicatedNestedValue_2"));

    }

    @Test
    public void shouldInjectCustomValuesWithReturnNull() {
        testAndVerifyInjectionOfCustomValues(null);
    }

    @Test
    public void shouldInjectCustomValuesWithReturnTypeString() {
        String expectedResponse = "EXPECTED_RESPONSE";
        testAndVerifyInjectionOfCustomValues(expectedResponse);
    }

    @Test
    public void shouldInjectCustomValuesWithReturnTypeInteger() {
        int expectedResponse = 1234;
        testAndVerifyInjectionOfCustomValues(expectedResponse);
    }

    @Test
    public void shouldInjectCustomValuesWithReturnTypeArray() {
        Object[] expectedResponse = new Object[]{"one", "two", "three"};
        testAndVerifyInjectionOfCustomValues(expectedResponse);
    }

    @Test
    public void shouldInjectCustomValuesWithReturnTypeList() {
        List<Object> expectedResponse = Arrays.asList("one", "two");
        testAndVerifyInjectionOfCustomValues(expectedResponse);
    }

    @Test
    public void shouldInjectCustomValuesWithReturnTypeMap() {
        Map<String, Object> expectedResponse = Collections.singletonMap("key", "value");
        testAndVerifyInjectionOfCustomValues(expectedResponse);
    }

    private void testAndVerifyInjectionOfCustomValues(Object expectedResponse) {
        // ARRANGE
        scenarioContext = new BackEndFunctionalTestScenarioContextForTest();
        scenarioContext.initializeTestDataFor("Custom-Value-Test-Data");
        HttpTestData testData = scenarioContext.getTestData();

        Mockito.when(taAdapter.calculateCustomValue(scenarioContext, "test-custom-value-key")).thenReturn(expectedResponse);
        Mockito.when(taAdapter.calculateCustomValue(scenarioContext, "test-custom-value-string")).thenReturn("INLINE");

        DynamicValueInjector underTest = DynamicValueInjectorFactory.create(taAdapter, testData, scenarioContext);

        // verify custom-value TD file looks OK prior to execution of test
        assertCustomValuesTestData(testData, "${[scenarioContext][customValues][test-custom-value-key]}");

        // ACT
        underTest.injectDataFromContextBeforeApiCall();

        // ASSERT
        assertCustomValuesTestData(testData, expectedResponse);
        Assert.assertEquals("BEFORE-INLINE-AFTER", testData.getRequest().getBody().get("inline-value"));
    }

    private void assertCustomValuesTestData(HttpTestData testData, Object expectedResponse) {
        // check string test data
        Assert.assertNull(testData.getRequest().getBody().get("null-check"));

        // check string test data
        Assert.assertEquals(expectedResponse,
                testData.getRequest().getBody().get("test-custom-value"));

        // check map test data
        // : map > null
        Assert.assertNull(((Map<?,?>)testData.getRequest().getBody().get("map")).get("null-check"));
        // : map > string
        Assert.assertEquals(expectedResponse,
                ((Map<?,?>)testData.getRequest().getBody().get("map")).get("test-custom-value"));
        // : map > map
        Assert.assertEquals(expectedResponse,
                ((Map<?,?>)((Map<?,?>)testData.getRequest().getBody().get("map")).get("map")).get("test-custom-value"));
        // : map > array
        Assert.assertEquals(expectedResponse,
                ((ArrayList<?>)((Map<?,?>)testData.getRequest().getBody().get("map")).get("array")).get(0));

        // check array test data
        // : array > null
        Assert.assertNull(((ArrayList<?>)testData.getRequest().getBody().get("array")).get(0));
        // : array > string
        Assert.assertEquals(expectedResponse,
                ((ArrayList<?>)testData.getRequest().getBody().get("array")).get(1));
        // : array > map
        Assert.assertEquals(expectedResponse,
                ((Map<?,?>)((ArrayList<?>)testData.getRequest().getBody().get("array")).get(2)).get("test-custom-value"));
        // : array > array
        Assert.assertEquals(expectedResponse,
                ((ArrayList<?>)((ArrayList<?>)testData.getRequest().getBody().get("array")).get(3)).get(0));
    }

    class BackEndFunctionalTestScenarioContextForTest extends BackEndFunctionalTestScenarioContext {

        @Override
        public void initializeTestDataFor(String testDataId) {
            testData = new HttpTestData(TEST_DATA_RESOURCE.getDataForTestCall(testDataId));
        }

        @Override
        protected Object calculateCustomValue(Object key) {
            return taAdapter.calculateCustomValue(this, key);
        }
    }
}

