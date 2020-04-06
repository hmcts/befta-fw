package uk.gov.hmcts.befta.player;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.RequestData;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.exception.UnconfirmedApiCallException;
import uk.gov.hmcts.befta.exception.UnconfirmedDataSpecException;
import uk.gov.hmcts.befta.util.DynamicValueInjector;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;
import uk.gov.hmcts.befta.util.JsonUtils;
import uk.gov.hmcts.befta.util.MapVerificationResult;
import uk.gov.hmcts.befta.util.MapVerifier;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DefaultBackEndFunctionalTestScenarioPlayer.class, RestAssured.class, Scenario.class, BeftaMain.class,
        DynamicValueInjector.class, EnvironmentVariableUtils.class, JsonUtils.class, Method.class,
        SpecificationQuerier.class })
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
    private static final String OPERATION = "OPERATION";
    private static final String PRODUCT_NAME = "PRODUCT NAME";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Captor
    private ArgumentCaptor<?> captor;

    @Before
    public void setUp() throws Exception {
        mockStatic(RestAssured.class);
        mockStatic(EnvironmentVariableUtils.class);
        mockStatic(EnvironmentVariableUtils.class);
        mockStatic(Method.class);
        mockStatic(SpecificationQuerier.class);
        mockStatic(JsonUtils.class);
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
        exceptionRule.expect(AssertionError.class);
        exceptionRule.expectMessage("Response code '500' is not a success code.");

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
        exceptionRule.expect(AssertionError.class);
        exceptionRule.expectMessage("Response code '201' is unexpectedly a success code.");

        scenarioPlayer.verifyThatANegativeResponseWasReceived();
    }

    @Test
    public void shouldPrepareARequestWithAppropriateValuesUsingMaxData() throws IOException {
        HttpTestData testData = new HttpTestData();
        RequestData requestData = new RequestData();
        requestData.setHeaders(new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("header1", "header value 1");
                put("header2", "header value 2");
            }
        });
        requestData.setPathVariables(new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("pathvar1", "path var value 1");
                put("pathvar2", "path var value 2");
            }
        });
        requestData.setQueryParams(new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("param1", "param value 1");
                put("param2", "param value 2");
            }
        });
        requestData.setBody(new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", "value 1");
                put("key2", "value 2");
            }
        });
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
        testData.setRequest(requestData);

        when(RestAssured.given()).thenReturn(requestSpecification);
        when(context.getTestData()).thenReturn(testData);

        scenarioPlayer.prepareARequestWithAppropriateValues();

        verifyNoMoreInteractions(requestSpecification);
    }

    @Test
    public void shouldVerifyThatTheResponseHasAllTheDetailsAsExpectedSuccessfully() throws IOException {
        ResponseData response = createResponseDataWithResponseCode(200);
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.getExpectedResponse()).thenReturn(response);
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

        exceptionRule.expect(AssertionError.class);
        exceptionRule.expectMessage(containsString("Response code mismatch, expected: 200, actual: 500"));

        scenarioPlayer.verifyThatTheResponseHasAllTheDetailsAsExpected();
    }

    @SuppressWarnings("unchecked")
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

        when(verificationResult.getAllIssues()).thenReturn(new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            {
                add("Header issue 1");
                add("Header issue 2");
            }
        }, new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            {
                add("Body issue 1");
                add("Body issue 2");
            }
        });

        exceptionRule.expect(AssertionError.class);
        exceptionRule.expectMessage(containsString("Header issue 1"));
        exceptionRule.expectMessage(containsString("Header issue 2"));
        exceptionRule.expectMessage(containsString("Body issue 1"));
        exceptionRule.expectMessage(containsString("Body issue 2"));

        scenarioPlayer.verifyThatTheResponseHasAllTheDetailsAsExpected();
    }

    @Test
    public void shouldVerifyThatThereIsAUserInTheContextWithAParticularSpecificationSuccessfully() {
        final String specificationAboutUser = "USER SPEC";
        HttpTestData testData = mock(HttpTestData.class);
        UserData userData = createUserData(USERNAME, PASSWORD);
        LinkedHashMap<String, UserData> users = new LinkedHashMap<String, UserData>() {
            private static final long serialVersionUID = 1L;
            {
                put("invokingUser", userData);
            }
        };

        when(testData.meetsSpec(any())).thenReturn(true);
        when(testData.getInvokingUser()).thenReturn(userData);
        when(testData.getUsers()).thenReturn(users);
        when(context.getTestData()).thenReturn(testData);
        when(EnvironmentVariableUtils.resolvePossibleVariable(USERNAME)).thenReturn(USERNAME);
        when(EnvironmentVariableUtils.resolvePossibleVariable(PASSWORD)).thenReturn(PASSWORD);
        BeftaMain.setTaAdapter(adapter);
        scenarioPlayer.verifyThatThereIsAUserInTheContextWithAParticularSpecification(specificationAboutUser);

        verify(context).setTheInvokingUser(userData);
        verify(testData).meetsSpec(specificationAboutUser);
    }

    @Test
    public void shouldFailToVerifyThatThereIsAUserInTheContextWithAParticularSpecification() {
        final String specificationAboutUser = "USER SPEC";
        HttpTestData testData = mock(HttpTestData.class);
        UserData userData = createUserData(USERNAME, PASSWORD);

        when(testData.meetsSpec(any())).thenReturn(false);
        when(testData.getInvokingUser()).thenReturn(userData);
        when(context.getTestData()).thenReturn(testData);
        when(EnvironmentVariableUtils.resolvePossibleVariable(USERNAME)).thenReturn(USERNAME);
        when(EnvironmentVariableUtils.resolvePossibleVariable(PASSWORD)).thenReturn(PASSWORD);

        exceptionRule.expect(UnconfirmedDataSpecException.class);
        exceptionRule.expectMessage("Test data does not confirm it meets the specification: 'USER SPEC'");

        scenarioPlayer.verifyThatThereIsAUserInTheContextWithAParticularSpecification(specificationAboutUser);
    }

    @Test
    public void shouldVerifyTheRequestInTheContextWithAParticularSpecificationSuccessfully() {
        final String requestSpecification = "REQUEST SPEC";
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.meetsSpec(any())).thenReturn(true);
        when(context.getTestData()).thenReturn(testData);

        scenarioPlayer.verifyTheRequestInTheContextWithAParticularSpecification(requestSpecification);

        verify(testData).meetsSpec(requestSpecification);
    }

    @Test
    public void shouldFailToVerifyTheRequestInTheContextWithAParticularSpecification() {
        final String requestSpecification = "REQUEST SPEC";
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.meetsSpec(any())).thenReturn(false);
        when(context.getTestData()).thenReturn(testData);

        exceptionRule.expect(UnconfirmedDataSpecException.class);
        exceptionRule.expectMessage("Test data does not confirm it meets the specification: 'REQUEST SPEC'");

        scenarioPlayer.verifyTheRequestInTheContextWithAParticularSpecification(requestSpecification);
    }

    @Test
    public void shouldVerifyTheResponseInTheContextWithAParticularSpecificationSuccessfully() {
        final String responseSpecification = "RESPONSE SPEC";
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.meetsSpec(any())).thenReturn(true);
        when(context.getTestData()).thenReturn(testData);

        scenarioPlayer.verifyTheResponseInTheContextWithAParticularSpecification(responseSpecification);

        verify(testData).meetsSpec(responseSpecification);
    }

    @Test
    public void shouldFailToVerifyTheResponseInTheContextWithAParticularSpecification() {
        final String responseSpecification = "RESPONSE SPEC";
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.meetsSpec(any())).thenReturn(false);
        when(context.getTestData()).thenReturn(testData);

        exceptionRule.expect(UnconfirmedDataSpecException.class);
        exceptionRule.expectMessage("Test data does not confirm it meets the specification: 'RESPONSE SPEC'");

        scenarioPlayer.verifyTheResponseInTheContextWithAParticularSpecification(responseSpecification);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldSubmitTheRequestToCallAnOperationOfAProductWithCorrectOperation() throws IOException {
        final String methodType = "POST";
        final String uri = "URI";
        final String bodyString = "{}";
        Map<String, Object> body = mock(Map.class);
        HttpTestData testData = mock(HttpTestData.class);
        Response response = mock(Response.class);
        ResponseBody responseBody = mock(ResponseBody.class);
        QueryableRequestSpecification queryableRequest = mock(QueryableRequestSpecification.class);

        when(testData.meetsOperationOfProduct(eq(PRODUCT_NAME), eq(OPERATION))).thenReturn(true);
        when(testData.getMethod()).thenReturn(methodType);
        when(testData.getUri()).thenReturn(uri);
        when(testData.getExpectedResponse()).thenReturn(new ResponseData());
        when(context.getTestData()).thenReturn(testData);
        when(context.getTheRequest()).thenReturn(requestSpecification);
        when(response.getHeaders())
                .thenReturn(new Headers(Arrays.asList(new Header("Content-Type", "application/json;charset=UTF-8"))));
        when(response.getStatusCode()).thenReturn(200);
        when(response.getBody()).thenReturn(responseBody);
        when(response.contentType()).thenReturn("application/json;charset=UTF-8");
        when(responseBody.asString()).thenReturn(bodyString);
        when(requestSpecification.request(eq(Method.POST), eq(uri))).thenReturn(response);
        when(Method.valueOf(eq(methodType))).thenReturn(Method.POST);
        when(SpecificationQuerier.query(eq(requestSpecification))).thenReturn(queryableRequest);
        when(JsonUtils.readObjectFromJsonText(any(), any())).thenReturn(body);

        scenarioPlayer.submitTheRequestToCallAnOperationOfAProduct(OPERATION, PRODUCT_NAME);

        verify(testData).setActualResponse((ResponseData) captor.capture());
        ResponseData responseData = (ResponseData) captor.getValue();
        assertEquals(200, responseData.getResponseCode());
        assertEquals(1, responseData.getHeaders().size());
        assertEquals(body, responseData.getBody());
        assertEquals("OK", responseData.getResponseMessage());
    }

    @Test
    public void shouldFailToSubmitTheRequestToCallAnOperationOfAProductWithInvalidMethodType() throws IOException {
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.meetsOperationOfProduct(eq(PRODUCT_NAME), eq(OPERATION))).thenReturn(true);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getMethod()).thenReturn("X");
        when(testData.getUri()).thenReturn("http://localhost");
        when(Method.valueOf(any())).thenThrow(IllegalArgumentException.class);

        exceptionRule.expect(FunctionalTestException.class);
        exceptionRule.expectMessage("Method 'X' in test data file not recognised");

        scenarioPlayer.submitTheRequestToCallAnOperationOfAProduct(OPERATION, PRODUCT_NAME);
    }

    @Test
    public void shouldFailToSubmitTheRequestToCallAnOperationOfAProductWithIncorrectOperation() throws IOException {
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.meetsOperationOfProduct(eq(PRODUCT_NAME), eq(OPERATION))).thenReturn(false);
        when(context.getTestData()).thenReturn(testData);

        exceptionRule.expect(UnconfirmedApiCallException.class);
        exceptionRule.expectMessage(
                "Test data does not confirm it is calling the following operation of a product: OPERATION -> PRODUCT NAME");

        scenarioPlayer.submitTheRequestToCallAnOperationOfAProduct(OPERATION, PRODUCT_NAME);
    }

    private ResponseData createResponseDataWithResponseCode(int responseCode) {
        ResponseData responseData = new ResponseData();
        responseData.setResponseCode(responseCode);
        when(context.getTheResponse()).thenReturn(responseData);
        return responseData;
    }

    private UserData createUserData(String username, String password) {
        UserData userData = mock(UserData.class);
        when(userData.getUsername()).thenReturn(username);
        when(userData.getPassword()).thenReturn(password);
        return userData;
    }
}
