package uk.gov.hmcts.befta.util;

import org.apache.logging.log4j.util.Strings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EnvironmentVariableUtils.class)
public class DynamicValueInjectorTest {

    private static final String[] TEST_DATA_RESOURCE_PACKAGES = { "framework-test-data" };
    private static final HttpTestDataSource TEST_DATA_RESOURCE = new JsonStoreHttpTestDataSource(
            TEST_DATA_RESOURCE_PACKAGES);

    private BackEndFunctionalTestScenarioContext scenarioContext;

    @Mock
    private DefaultTestAutomationAdapter taAdapter;

    @Before
    public void prepareScenarioConext() {
        scenarioContext = new BackEndFunctionalTestScenarioContextForTest();

        scenarioContext.initializeTestDataFor("Simple-Test-Data-With-All-Possible-Dynamic-Values");
        
        BackEndFunctionalTestScenarioContext subcontext = new BackEndFunctionalTestScenarioContextForTest();
        subcontext.initializeTestDataFor("Token_Creation_Call");
        subcontext.getTestData().setActualResponse(subcontext.getTestData().getExpectedResponse());

        scenarioContext.setTheInvokingUser(scenarioContext.getTestData().getInvokingUser());
        scenarioContext.addChildContext(subcontext);

        PowerMockito.mockStatic(EnvironmentVariableUtils.class);

        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("S2S_URL")).thenReturn("http://s2s.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("IDAM_URL")).thenReturn("http://idam.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("IDAM_USER_URL"))
                .thenReturn("http://idamuser.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("DEFINITION_STORE_HOST"))
                .thenReturn("http://defstore.hmcts.bla.bla");
        Mockito.when(EnvironmentVariableUtils.getRequiredVariable("CCD_CASEWORKER_AUTOTEST_PASSWORD"))
                .thenReturn("PassQ@rT");

    }
    
    @Test
    public void shouldInjectAllFormulaValues() {

        HttpTestData testData = scenarioContext.getTestData();

        DynamicValueInjector underTest = new DynamicValueInjector(taAdapter, testData, scenarioContext);

        Assert.assertEquals("[[DYNAMIC]]", testData.getRequest().getPathVariables().get("uid"));

        underTest.injectDataFromContext();

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

        DynamicValueInjector underTest = new DynamicValueInjector(taAdapter, testData, scenarioContext);

        Assert.assertEquals("[[DYNAMIC]]", testData.getRequest().getPathVariables().get("uid"));

        underTest.injectDataFromContext();

        Assert.assertEquals(
                "a.user@http://idam.hmcts.bla.bla/token value at index 2#http://idamuser.hmcts.bla.bla/documents/binary",
                testData.getRequest().getPathVariables().get("dummyComplexPathVariable"));

        Assert.assertEquals("http://idam.hmcts.bla.bla/documents/binary",
                testData.getRequest().getPathVariables().get("binanyUrlOverIdam"));
        Assert.assertEquals("http://idam.hmcts.bla.bla", testData.getRequest().getPathVariables().get("justIdamUrl"));
        Assert.assertEquals("http://idamuser.hmcts.bla.bla/documents/binary",
                testData.getRequest().getBody().get("event_token"));

        Assert.assertEquals(null, testData.getRequest().getBody().get("nullValueField"));
        Assert.assertEquals(Strings.EMPTY, testData.getExpectedResponse().getBody().get("emptyString"));
        Assert.assertEquals(4.6, testData.getExpectedResponse().getBody().get("onlyStaticNumber"));
        Assert.assertEquals("string without any dynamic part",
                testData.getExpectedResponse().getBody().get("onlyStaticString"));
        Assert.assertEquals("token value at index 2", testData.getExpectedResponse().getBody().get("onlyFormulaOnly"));
        Assert.assertEquals("http://defstore.hmcts.bla.bla",
                testData.getExpectedResponse().getBody().get("oneEnvironmentVariableOnly"));
        Assert.assertEquals("http://defstore.hmcts.bla.blaPa55word11http://defstore.hmcts.bla.bla",
                testData.getExpectedResponse().getBody().get("threeEnvironmentVariablesOnly"));
        Assert.assertEquals("token value at index 2token value at index 2",
                testData.getExpectedResponse().getBody().get("twoFormulasOnly"));
        Assert.assertEquals("token value at index 2http://defstore.hmcts.bla.blaabc123http://defstore.hmcts.bla.bla",
                testData.getExpectedResponse().getBody().get("complicatedNestedValue_1"));
        Assert.assertEquals(
                "abctoken value at index 2.=.http://defstore.hmcts.bla.blatoken value at index 2abc123http://defstore.hmcts.bla.bla",
                testData.getExpectedResponse().getBody().get("complicatedNestedValue_2"));

    }

    class BackEndFunctionalTestScenarioContextForTest extends BackEndFunctionalTestScenarioContext {

        @Override
        public void initializeTestDataFor(String testDataId) {
            testData = TEST_DATA_RESOURCE.getDataForTestCall(testDataId);
        }
    }
}

