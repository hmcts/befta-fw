package uk.gov.hmcts.befta.util;

import org.apache.logging.log4j.util.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

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

        Assert.assertEquals("http://http://localhost:5000token value at index 2http://localhost:5000/documents/binary", testData.getRequest().getPathVariables().get("email"));

        Assert.assertEquals("http://http://localhost:5000/documents/binary", testData.getRequest().getPathVariables().get("token"));
        Assert.assertEquals("http://localhost:5000", testData.getRequest().getPathVariables().get("token_2"));
        Assert.assertEquals("http://http://localhost:5000/documents/binary", testData.getRequest().getBody().get("event_token"));

        Assert.assertEquals(null, testData.getRequest().getBody().get("nullValueField"));
        Assert.assertEquals(Strings.EMPTY, testData.getRequest().getBody().get("emptyString"));
        Assert.assertEquals(4.6, testData.getRequest().getBody().get("onlyStaticNumber"));
        Assert.assertEquals("string without any dynamic part", testData.getRequest().getBody().get("onlyStaticString"));
        Assert.assertEquals("token value at index 2", testData.getRequest().getBody().get("onlyFormulaOnly"));
        Assert.assertEquals("http://localhost:5000", testData.getRequest().getBody().get("oneEnvironmentVariableOnly"));
        Assert.assertEquals("http://localhost:5000Pa55word11http://localhost:4451", testData.getRequest().getBody().get("threeEnvironmentVariablesOnly"));
        Assert.assertEquals("token value at index 2token value at index 2", testData.getRequest().getBody().get("twoFormulasOnly"));
        Assert.assertEquals("token value at index 2http://localhost:5000abc123http://localhost:4451", testData.getRequest().getBody().get("complicatedNestedValue_1"));
        Assert.assertEquals("abctoken value at index 2.=.http://localhost:5000token value at index 2abc123http://localhost:4451", testData.getRequest().getBody().get("complicatedNestedValue_2"));

    }

    class BackEndFunctionalTestScenarioContextForTest extends BackEndFunctionalTestScenarioContext {

        @Override
        public void initializeTestDataFor(String testDataId) {
            testData = TEST_DATA_RESOURCE.getDataForTestCall(testDataId);
        }
    }
}

