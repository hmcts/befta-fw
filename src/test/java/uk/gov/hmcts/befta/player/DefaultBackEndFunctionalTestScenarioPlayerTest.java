package uk.gov.hmcts.befta.player;

import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.RequestData;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.util.DynamicValueInjector;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;
import uk.gov.hmcts.befta.util.MapVerificationResult;
import uk.gov.hmcts.befta.util.MapVerifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        DefaultBackEndFunctionalTestScenarioPlayer.class,
        RestAssured.class,
        Scenario.class,
        BeftaMain.class,
        DynamicValueInjector.class,
        EnvironmentVariableUtils.class
})
public class DefaultBackEndFunctionalTestScenarioPlayerTest {

    private DefaultBackEndFunctionalTestScenarioPlayer scenarioPlayer;

    @Mock
    private BackEndFunctionalTestScenarioContext context;

    @Mock
    private DynamicValueInjector dynamicInjector;

    @Mock
    private DefaultTestAutomationAdapter adapter;

    @Mock
    private MapVerifier mapVerifier;

    @Mock
    private RequestSpecification requestSpecification;

    @Mock
    private Scenario scenario;

    @Mock
    private MapVerificationResult verificationResult;

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        mockStatic(RestAssured.class);
        mockStatic(EnvironmentVariableUtils.class);
        whenNew(DynamicValueInjector.class).withAnyArguments().thenReturn(dynamicInjector);
        whenNew(DefaultTestAutomationAdapter.class).withNoArguments().thenReturn(adapter);
        whenNew(BackEndFunctionalTestScenarioContext.class).withNoArguments().thenReturn(context);
        whenNew(MapVerifier.class).withAnyArguments().thenReturn(mapVerifier);
        scenarioPlayer = new DefaultBackEndFunctionalTestScenarioPlayer();
        scenarioPlayer.prepare(scenario);
    }

    @Test
    public void shouldInitializeAppropriateTestContextAsDetailedInTheTestDataSource() {
        scenarioPlayer.initializeAppropriateTestContextAsDetailedInTheTestDataSource();

        verify(context).initializeTestDataFor(eq(scenario));
        verify(context).getCurrentScenarioTag();
    }

    @Test
    public void shouldVerifyThatAPositiveResponseWasReceivedForCode20x() {
        createResponseDataWithResponseCode(204);

        scenarioPlayer.verifyThatAPositiveResponseWasReceived();

        verify(scenario).write("Response code: 204");
    }

    @Test
    public void shouldErrorWhenVerifyingThatAPositiveResponseWasReceivedForCode50x() {
        createResponseDataWithResponseCode(500);
        exceptionRule.expect(FunctionalTestException.class);

        scenarioPlayer.verifyThatAPositiveResponseWasReceived();
    }

    @Test
    public void shouldVerifyThatANegativeResponseWasReceivedForCode40x() {
        createResponseDataWithResponseCode(400);

        scenarioPlayer.verifyThatANegativeResponseWasReceived();

        verify(scenario).write("Response code: 400");
    }

    @Test
    public void shouldErrorWhenVerifyingThatANegativeResponseWasReceivedForCode20x() {
        createResponseDataWithResponseCode(201);
        exceptionRule.expect(FunctionalTestException.class);

        scenarioPlayer.verifyThatANegativeResponseWasReceived();
    }

    @Test
    public void shouldPrepareARequestWithAppropriateValuesUsingMaxData() throws IOException {
        HttpTestData testData = new HttpTestData();
        RequestData requestData = new RequestData();
        requestData.setHeaders(new HashMap<String, Object>() {{
            put("header1", "header value 1");
            put("header2", "header value 2");
        }});
        requestData.setPathVariables(new HashMap<String, Object>() {{
            put("pathvar1", "path var value 1");
            put("pathvar2", "path var value 2");
        }});
        requestData.setQueryParams(new HashMap<String, Object>() {{
            put("param1", "param value 1");
            put("param2", "param value 2");
        }});
        requestData.setBody(new HashMap<String, Object>() {{
            put("key1", "value 1");
            put("key2", "value 2");
        }});
        testData.setRequest(requestData);

        when(RestAssured.given()).thenReturn(requestSpecification);
        when(context.getTheInvokingUser()).thenReturn(new UserData());
        when(context.getTestData()).thenReturn(testData);

        scenarioPlayer.prepareARequestWithAppropriateValues();

        verify(context, times(0)).setTheInvokingUser(any());
        verify(requestSpecification).header("header1", "header value 1");
        verify(requestSpecification).header("header2", "header value 2");
        verify(requestSpecification).pathParam("pathvar1", "path var value 1");
        verify(requestSpecification).pathParam("pathvar2", "path var value 2");
        verify(requestSpecification).queryParam("param1", "param value 1");
        verify(requestSpecification).queryParam("param2", "param value 2");
        verify(requestSpecification).body(any(byte[].class));
        verifyNoMoreInteractions(requestSpecification);
    }

    @Test
    public void shouldPrepareARequestWithAppropriateValuesUsingMinData() throws IOException {
        HttpTestData testData = new HttpTestData();
        RequestData requestData = new RequestData();
        UserData userData = new UserData();
        userData.setUsername("USERNAME");
        userData.setPassword("PASSWORD");
        testData.setRequest(requestData);
        testData.setInvokingUser(userData);

        when(RestAssured.given()).thenReturn(requestSpecification);
        when(EnvironmentVariableUtils.resolvePossibleVariable(USERNAME)).thenReturn(USERNAME);
        when(EnvironmentVariableUtils.resolvePossibleVariable(PASSWORD)).thenReturn(PASSWORD);
        when(context.getTestData()).thenReturn(testData);

        scenarioPlayer.prepareARequestWithAppropriateValues();

        verify(context).setTheInvokingUser(eq(userData));
        verifyNoMoreInteractions(requestSpecification);
    }

    @Test
    public void shouldVerifyThatTheResponseHasAllTheDetailsAsExpectedSuccessfully() throws IOException {
        ResponseData response = createResponseDataWithResponseCode(200);
        HttpTestData testData = new HttpTestData();
        testData.setExpectedResponse(response);

        when(context.getTestData()).thenReturn(testData);
        when(mapVerifier.verifyMap(any(), any())).thenReturn(verificationResult);
        when(verificationResult.isVerified()).thenReturn(true);

        scenarioPlayer.verifyThatTheResponseHasAllTheDetailsAsExpected();
    }

    @Test
    public void shouldFailToVerifyResponsesWithDifferentResponseCodes() throws IOException {
        createResponseDataWithResponseCode(500);
        ResponseData expectedResponse = new ResponseData();
        expectedResponse.setResponseCode(200);
        HttpTestData testData = new HttpTestData();
        testData.setExpectedResponse(expectedResponse);

        when(context.getTestData()).thenReturn(testData);
        when(mapVerifier.verifyMap(any(), any())).thenReturn(verificationResult);
        when(verificationResult.isVerified()).thenReturn(true);

        exceptionRule.expect(FunctionalTestException.class);
        exceptionRule.expectMessage(containsString("Response code mismatch, expected: 200, actual: 500"));

        scenarioPlayer.verifyThatTheResponseHasAllTheDetailsAsExpected();
    }

    @Test
    public void shouldFailToVerifyResponsesWithMapVerifierIssues() throws IOException {
        ResponseData response = new ResponseData();
        response.setHeaders(new HashMap<>());
        response.setBody(new HashMap<>());
        HttpTestData testData = new HttpTestData();
        testData.setExpectedResponse(response);

        when(context.getTestData()).thenReturn(testData);
        when(context.getTheResponse()).thenReturn(response);
        when(mapVerifier.verifyMap(any(), any())).thenReturn(verificationResult);
        when(verificationResult.isVerified()).thenReturn(false);

        when(verificationResult.getAllIssues()).thenReturn(new ArrayList<String>() {{
            add("Header issue 1");
            add("Header issue 2");
        }}, new ArrayList<String>() {{
            add("Body issue 1");
            add("Body issue 2");
        }});

        exceptionRule.expect(FunctionalTestException.class);
        exceptionRule.expectMessage(containsString("Header issue 1"));
        exceptionRule.expectMessage(containsString("Header issue 2"));
        exceptionRule.expectMessage(containsString("Body issue 1"));
        exceptionRule.expectMessage(containsString("Body issue 2"));

        scenarioPlayer.verifyThatTheResponseHasAllTheDetailsAsExpected();
    }

    private ResponseData createResponseDataWithResponseCode(int responseCode) {
        ResponseData responseData = new ResponseData();
        responseData.setResponseCode(responseCode);
        when(context.getTheResponse()).thenReturn(responseData);
        return responseData;
    }
}
