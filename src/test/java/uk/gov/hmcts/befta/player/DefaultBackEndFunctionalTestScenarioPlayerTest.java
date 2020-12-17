package uk.gov.hmcts.befta.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;
import uk.gov.hmcts.befta.data.RequestData;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.exception.InvalidTestDataException;
import uk.gov.hmcts.befta.exception.UnconfirmedApiCallException;
import uk.gov.hmcts.befta.exception.UnconfirmedDataSpecException;
import uk.gov.hmcts.befta.factory.BeftaScenarioContextFactory;
import uk.gov.hmcts.befta.util.DynamicValueInjector;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;
import uk.gov.hmcts.befta.util.JsonUtils;
import uk.gov.hmcts.befta.util.MapVerificationResult;
import uk.gov.hmcts.befta.util.MapVerifier;
import uk.gov.hmcts.common.TestUtils;

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

    private MockedStatic<RestAssured> restAssuredMock = null;
    private MockedStatic<EnvironmentVariableUtils> environmentVariableUtilsMock = null;
    private MockedStatic<SpecificationQuerier> specificationQuerierMock = null;
    private MockedStatic<JsonUtils> jsonUtilsMock = null;
    private MockedStatic<BackEndFunctionalTestScenarioContext> backEndFunctionalTestScenarioContextMock = null;
    private MockedStatic<BeftaScenarioContextFactory> beftaScenarioContextFactoryMock = null;
    private MockedStatic<MapVerifier> mapVerifierMock = null;

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String OPERATION = "OPERATION";
    private static final String PRODUCT_NAME = "PRODUCT NAME";
    @Mock
    private HttpTestData s103TestData;
    @Mock
    private JsonStoreHttpTestDataSource dataSource;


    @Captor
    private ArgumentCaptor<?> captor;

    @BeforeEach
    public void prepareMockedObjectUnderTest() {
        try {
        	restAssuredMock = mockStatic(RestAssured.class);
        	environmentVariableUtilsMock = mockStatic(EnvironmentVariableUtils.class);
        	specificationQuerierMock = mockStatic(SpecificationQuerier.class);
        	jsonUtilsMock = mockStatic(JsonUtils.class);
            backEndFunctionalTestScenarioContextMock = mockStatic(BackEndFunctionalTestScenarioContext.class);
            beftaScenarioContextFactoryMock = mockStatic(BeftaScenarioContextFactory.class);

        	mapVerifierMock = mockStatic(MapVerifier.class);
        	setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void closeMockedObjectUnderTest() {
        try {
        	scenarioPlayer = null;
        	restAssuredMock.close();
        	environmentVariableUtilsMock.close();
        	specificationQuerierMock.close();
        	jsonUtilsMock.close();
            backEndFunctionalTestScenarioContextMock.close();
            beftaScenarioContextFactoryMock.close();
        	mapVerifierMock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestUtils.setFieldWithReflection(context,
        		context.getClass().getDeclaredField("DATA_SOURCE"),
        		dataSource);

        scenarioPlayer = new DefaultBackEndFunctionalTestScenarioPlayer();
        TestUtils.setFieldWithReflection(scenarioPlayer,
                scenarioPlayer.getClass().getDeclaredField("scenarioContext"),
                context);
        scenarioPlayer.cucumberPrepare(scenario);
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

        verify(scenario).log("Response code: 204");
    }

    @Test
    public void shouldErrorWhenVerifyingThatAPositiveResponseWasReceivedForCode50x() {
        createResponseDataWithResponseCode(500);
        AssertionError aeThrown =Assertions.assertThrows(AssertionError.class, () ->
            scenarioPlayer.verifyThatAPositiveResponseWasReceived(),"AssertionError is not thrown"
        );
        assertTrue(aeThrown.getMessage().contains("Response code '500' is not a success code."));
    }

    @Test
    public void shouldVerifyThatANegativeResponseWasReceivedForCode40x() {
        createResponseDataWithResponseCode(400);

        scenarioPlayer.verifyThatANegativeResponseWasReceived();

        verify(scenario).log("Response code: 400");
    }

    @Test
    public void shouldErrorWhenVerifyingThatANegativeResponseWasReceivedForCode20x() {
        createResponseDataWithResponseCode(201);
        AssertionError aeThrown =Assertions.assertThrows(AssertionError.class, () ->
        scenarioPlayer.verifyThatANegativeResponseWasReceived(),
        "AssertionError is not thrown"
        );
        assertTrue(aeThrown.getMessage().contains("Response code '201' is unexpectedly a success code."));
    }

    @Test
    public void shouldPrepareARequestWithAppropriateValuesUsingMaxData() throws IOException {
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
        HttpTestData testData = new HttpTestData();
        testData.setRequest(requestData);
        testData.setMethod("GET");

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
        testData.setMethod("GET");

        scenarioPlayer.prepareARequestWithAppropriateValues();

        verifyNoMoreInteractions(requestSpecification);
    }

    @Test
    public void shouldPrepareARequestWithAppropriateValuesAndRunPrerequisiteSpecifiedByString() throws Exception {
        // ARRANGE
        HttpTestData testData = new HttpTestData();
        RequestData requestData = new RequestData();
        testData.setRequest(requestData);

        when(RestAssured.given()).thenReturn(requestSpecification);
        when(context.getTestData()).thenReturn(testData);
        testData.setMethod("GET");
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.headers", 1, false)).thenReturn(mapVerifier);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.body", 20)).thenReturn(mapVerifier);

        String prerequisiteTestDataId = "PR1";
        BackEndFunctionalTestScenarioContext prerequisiteContext = createAndPrepareMockPrerequisiteContext(prerequisiteTestDataId, context);
        Mockito.when(BeftaScenarioContextFactory.createBeftaScenarioContext()).thenReturn(prerequisiteContext);
        testData.setPrerequisites(Collections.singletonList(prerequisiteTestDataId));

        // ACT
        scenarioPlayer.prepareARequestWithAppropriateValues();

        // ASSERT
        verify(this.context, times(1)).addChildContext(eq(prerequisiteTestDataId), eq(prerequisiteContext));
        verify(prerequisiteContext, times(1)).setTheResponse(any());
    }

    @Test
    public void shouldPrepareARequestWithAppropriateValuesAndRunPrerequisiteSpecifiedByMap() throws Exception {
        // ARRANGE
        HttpTestData testData = new HttpTestData();
        RequestData requestData = new RequestData();
        testData.setRequest(requestData);

        when(RestAssured.given()).thenReturn(requestSpecification);
        when(context.getTestData()).thenReturn(testData);
        testData.setMethod("GET");

        // i.e. call same prerequisite 3 time but using unique context_ids
        String prerequisiteTestDataId = "PR1";
        String prerequisiteContextId1 = "PR1_Call1";
        String prerequisiteContextId2 = "PR1_Call2";
        String prerequisiteContextId3 = "PR1_Call3";
        BackEndFunctionalTestScenarioContext prerequisiteContext1 = createAndPrepareMockPrerequisiteContext(prerequisiteTestDataId, context);
        BackEndFunctionalTestScenarioContext prerequisiteContext2 = createAndPrepareMockPrerequisiteContext(prerequisiteTestDataId, context);
        BackEndFunctionalTestScenarioContext prerequisiteContext3 = createAndPrepareMockPrerequisiteContext(prerequisiteTestDataId, context);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.headers", 1, false)).thenReturn(mapVerifier);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.body", 20)).thenReturn(mapVerifier);
        Mockito.when(BeftaScenarioContextFactory.createBeftaScenarioContext()).thenReturn(
                prerequisiteContext1,
                prerequisiteContext2, prerequisiteContext3);

        testData.setPrerequisites(Arrays.asList(new LinkedHashMap<String, String>() {
            private static final long serialVersionUID = 1L;

            {
                put(prerequisiteContextId1, prerequisiteTestDataId);
                put(prerequisiteContextId2, prerequisiteTestDataId);
            }
        }, new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put(prerequisiteContextId3, prerequisiteTestDataId);
            }
        }));

        // ACT
        scenarioPlayer.prepareARequestWithAppropriateValues();

        // ASSERT
        verify(this.context, times(1)).addChildContext(eq(prerequisiteContextId1), eq(prerequisiteContext1));
        verify(this.context, times(1)).addChildContext(eq(prerequisiteContextId2), eq(prerequisiteContext2));
        verify(this.context, times(1)).addChildContext(eq(prerequisiteContextId3), eq(prerequisiteContext3));
        verify(prerequisiteContext1, times(1)).setTheResponse(any());
        verify(prerequisiteContext2, times(1)).setTheResponse(any());
        verify(prerequisiteContext3, times(1)).setTheResponse(any());
    }

    @Test
    public void shouldPrepareARequestWithAppropriateValuesAndSkipAlreadyExecutedPrerequisites() throws Exception {
        // ARRANGE
        String testContextId = "TEST_CONTEXT_ID";
        when(context.getContextId()).thenReturn(testContextId);

        HttpTestData testData = new HttpTestData();
        RequestData requestData = new RequestData();
        testData.setRequest(requestData);

        when(RestAssured.given()).thenReturn(requestSpecification);
        when(context.getTestData()).thenReturn(testData);
        testData.setMethod("GET");
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.headers", 1, false)).thenReturn(mapVerifier);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.body", 20)).thenReturn(mapVerifier);

        String testDataId1 = "TEST1";
        String testDataId2 = "TEST2";
        BackEndFunctionalTestScenarioContext prerequisiteContext1 = createAndPrepareMockPrerequisiteContext(testDataId1, context);
        BackEndFunctionalTestScenarioContext prerequisiteContext2 = createAndPrepareMockPrerequisiteContext(testDataId2, context);
        Mockito.when(BeftaScenarioContextFactory.createBeftaScenarioContext()).thenReturn(
                prerequisiteContext1,
                prerequisiteContext2);

        // for simplicity add prerequisites just using strings of test data ids
        testData.setPrerequisites(Arrays.asList(testDataId1, testDataId2));

        // add child contexts (to mimic other steps) using matching IDs
        BackEndFunctionalTestScenarioContext childContext1 = createAndPrepareTestScenarioContext("SPEC1", testDataId1);
        BackEndFunctionalTestScenarioContext childContext2 = createAndPrepareTestScenarioContext("SPEC2", testDataId2);

        // configure child context 2 as though it is already executed
        childContext2.getTestData().setActualResponse(new ResponseData());

        when(context.getChildContexts()).thenReturn(
                new LinkedHashMap<String, BackEndFunctionalTestScenarioContext>() {
                    private static final long serialVersionUID = 1L;
                    {
                        // NB: steps use IDs as contextId
                        put(testDataId1, childContext1);
                        put(testDataId2, childContext2);
                    }
                }
        );

        // ACT
        scenarioPlayer.prepareARequestWithAppropriateValues();

        // ASSERT
        verify(this.context, times(1)).addChildContext(any(), eq(prerequisiteContext1));
        verify(this.context, never()).addChildContext(any(), eq(prerequisiteContext2));
        verify(prerequisiteContext1, times(1)).setTheResponse(any());
        verify(prerequisiteContext2, never()).setTheResponse(any());
        verify(this.scenario).log(eq("Skipping prerequisite: [TEST_CONTEXT_ID].[TEST2]")); // i.e. TEST2 already
                                                                                           // executed
    }

    @Test
    public void shouldFailToPrepareARequestWithAppropriateValuesIfPrerequisiteSpecifiedInWrongFormat() throws Exception {
        // ARRANGE
        HttpTestData testData = new HttpTestData();
        RequestData requestData = new RequestData();
        testData.setRequest(requestData);

        when(RestAssured.given()).thenReturn(requestSpecification);
        when(context.getTestData()).thenReturn(testData);
        testData.setMethod("GET");

        testData.setPrerequisites(Collections.singletonList(new Object()));

        FunctionalTestException feThrown =Assertions.assertThrows(FunctionalTestException.class, () ->
        scenarioPlayer.prepareARequestWithAppropriateValues(),
        "FunctionalTestException is not thrown"
        );
        assertTrue(feThrown.getMessage().contains("Unrecognised prerequisite data type"));

        // ACT

        // ASSERT
        // see exceptionRule above
    }

    @Test
    public void shouldFailToPrepareARequestWithAppropriateValuesIfPrerequisiteCallFailsToVerify() throws Exception {
        // ARRANGE
        HttpTestData testData = new HttpTestData();
        RequestData requestData = new RequestData();
        testData.setRequest(requestData);

        when(RestAssured.given()).thenReturn(requestSpecification);
        when(context.getTestData()).thenReturn(testData);
        testData.setMethod("GET");
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.headers", 1, false)).thenReturn(mapVerifier);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.body", 20)).thenReturn(mapVerifier);

        String prerequisiteTestDataId = "PR1";
        BackEndFunctionalTestScenarioContext prerequisiteContext = createAndPrepareMockPrerequisiteContext(prerequisiteTestDataId, context);
        testData.setPrerequisites(Collections.singletonList(prerequisiteTestDataId));
        Mockito.when(BeftaScenarioContextFactory.createBeftaScenarioContext()).thenReturn(prerequisiteContext);

        when(verificationResult.isVerified()).thenReturn(false);

        when(verificationResult.getAllIssues()).thenReturn(new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
            {
                add("Test issue 1");
                add("Test issue 2");
            }
        });

        AssertionError aeThrown =Assertions.assertThrows(AssertionError.class, () ->
        scenarioPlayer.prepareARequestWithAppropriateValues(),
        "AssertionError is not thrown"
        );
        assertTrue(aeThrown.getMessage().contains("Test issue 1"));
        assertTrue(aeThrown.getMessage().contains("Test issue 2"));

        // ACT

        // ASSERT
        // see exceptionRule above
    }

    @Test
    public void shouldFailToPrepareARequestWithAppropriateValuesIfCyclicPrerequisiteDependencyDetected() throws Exception {
        // ARRANGE
        HttpTestData testData = new HttpTestData();
        RequestData requestData = new RequestData();
        testData.setRequest(requestData);

        when(RestAssured.given()).thenReturn(requestSpecification);
        when(context.getTestData()).thenReturn(testData);
        testData.setMethod("GET");

        // test -> PR1 -> PR2 -> PR1
        String prerequisiteTestDataId1 = "PR1";
        String prerequisiteTestDataId2 = "PR2";
        BackEndFunctionalTestScenarioContext prerequisiteContext1 = createAndPrepareMockPrerequisiteContext(prerequisiteTestDataId1, context);
        BackEndFunctionalTestScenarioContext prerequisiteContext2 = createAndPrepareMockPrerequisiteContext(prerequisiteTestDataId2, prerequisiteContext1);
        // test -> PR1 -> PR2 -> PR1
        testData.setPrerequisites(Collections.singletonList(prerequisiteTestDataId1));
        prerequisiteContext1.getTestData().setPrerequisites(Collections.singletonList(prerequisiteTestDataId2));
        prerequisiteContext2.getTestData().setPrerequisites(Collections.singletonList(prerequisiteTestDataId1));
        Mockito.when(BeftaScenarioContextFactory
                .createBeftaScenarioContext())
                .thenReturn(prerequisiteContext1);
        Mockito.when(BeftaScenarioContextFactory
                .createBeftaScenarioContext())
                .thenReturn(prerequisiteContext2);

        InvalidTestDataException itdeThrown =Assertions.assertThrows(InvalidTestDataException.class, () ->
        scenarioPlayer.prepareARequestWithAppropriateValues(),
        "AssertionError is not thrown"
        );
        assertTrue(itdeThrown.getMessage().contains("Cyclic dependency discovered for prerequisite with contextId: " + prerequisiteTestDataId1));
        // ACT

        // ASSERT
        // see exceptionRule above
    }

    @Test
    public void shouldPerformAndVerifyTheExpectedResponseForAnApiCallAndAddAsChildContext() throws Exception {
        // ARRANGE
        String testDataId = "TD1";
        String testDataSpec = "Spec1";
        BackEndFunctionalTestScenarioContext testDataContext = createAndPrepareTestScenarioContext(testDataSpec, testDataId);
        Mockito.when(BeftaScenarioContextFactory.createBeftaScenarioContext()).thenReturn(testDataContext);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.headers", 1, false)).thenReturn(mapVerifier);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.body", 20)).thenReturn(mapVerifier);

        // ACT
        scenarioPlayer.performAndVerifyTheExpectedResponseForAnApiCall(testDataSpec, testDataId);

        // ASSERT
        verify(this.context, times(1)).addChildContext(eq(testDataContext));
        verify(testDataContext, times(1)).initializeTestDataFor(eq(testDataId));
        verify(testDataContext, times(1)).setTheResponse(any());
    }

    @Test
    public void shouldVerifyThatTheResponseHasAllTheDetailsAsExpectedSuccessfully() throws IOException {
        ResponseData response = createResponseDataWithResponseCode(200);
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.getExpectedResponse()).thenReturn(response);
        when(context.getTestData()).thenReturn(testData);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.headers", 1, false)).thenReturn(mapVerifier);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.body", 20)).thenReturn(mapVerifier);
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
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.headers", 1, false)).thenReturn(mapVerifier);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.body", 20)).thenReturn(mapVerifier);
        when(mapVerifier.verifyMap(any(), any())).thenReturn(verificationResult);
        when(verificationResult.isVerified()).thenReturn(true);

        AssertionError aeThrown =Assertions.assertThrows(AssertionError.class, () ->
        scenarioPlayer.verifyThatTheResponseHasAllTheDetailsAsExpected(),
        "AssertionError is not thrown"
        );
        assertTrue(aeThrown.getMessage().contains("Response code mismatch, expected: 200, actual: 500"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFailToVerifyResponsesWithMapVerifierIssues() throws IOException {
        ResponseData response = new ResponseData();
        response.setHeaders(new HashMap<>());
        response.setBody(new HashMap<>());
        HttpTestData testData = new HttpTestData();
        testData.setExpectedResponse(response);

        Mockito.when(context.getTestData()).thenReturn(testData);
        Mockito.when(context.getTheResponse()).thenReturn(response);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.headers", 1, false)).thenReturn(mapVerifier);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.body", 20)).thenReturn(mapVerifier);
        Mockito.when(mapVerifier.verifyMap(any(), any())).thenReturn(verificationResult);
        Mockito.when(verificationResult.isVerified()).thenReturn(false);

        Mockito.when(verificationResult.getAllIssues()).thenReturn(new ArrayList<String>() {
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

        AssertionError aeThrown =Assertions.assertThrows(AssertionError.class, () ->
        scenarioPlayer.verifyThatTheResponseHasAllTheDetailsAsExpected(),
        "AssertionError is not thrown"
        );
        assertTrue(aeThrown.getMessage().contains("Header issue 1"));
        assertTrue(aeThrown.getMessage().contains("Header issue 2"));
        assertTrue(aeThrown.getMessage().contains("Body issue 1"));
        assertTrue(aeThrown.getMessage().contains("Body issue 2"));
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
        when(testData.getUserEntryAt(0)).thenReturn(new AbstractMap.SimpleEntry<>("invokingUser", userData));
        when(context.getTestData()).thenReturn(testData);
        when(EnvironmentVariableUtils.resolvePossibleVariable(USERNAME)).thenReturn(USERNAME);
        when(EnvironmentVariableUtils.resolvePossibleVariable(PASSWORD)).thenReturn(PASSWORD);
        BeftaMain.setTaAdapter(adapter);
        scenarioPlayer.verifyThatThereIsAUserInTheContextWithAParticularSpecification(specificationAboutUser);

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

        UnconfirmedDataSpecException udseThrown =Assertions.assertThrows(UnconfirmedDataSpecException.class, () ->
        scenarioPlayer.verifyThatThereIsAUserInTheContextWithAParticularSpecification(specificationAboutUser),
        "UnconfirmedDataSpecException is not thrown"
        );
        assertTrue(udseThrown.getMessage().contains("Test data does not confirm it meets the specification: 'USER SPEC'"));

    }

    @Test
    public void shouldVerifyThatASpecificationAboutScenarioContextIsConfirmed() {
        final String specificationAboutScenarioContext = "CONTEXT SPEC";
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.meetsSpec(specificationAboutScenarioContext)).thenReturn(true);
        when(context.getTestData()).thenReturn(testData);

        scenarioPlayer.verifyThatASpecificationAboutScenarioContextIsConfirmed(specificationAboutScenarioContext);

        verify(testData).meetsSpec(specificationAboutScenarioContext);
    }

    @Test
    public void shouldFailToVerifyThatASpecificationAboutScenarioContextIsConfirmed() {
        final String specificationAboutScenarioContext = "CONTEXT SPEC";
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.meetsSpec(specificationAboutScenarioContext)).thenReturn(false);
        when(context.getTestData()).thenReturn(testData);

        UnconfirmedDataSpecException udseThrown =Assertions.assertThrows(UnconfirmedDataSpecException.class, () ->
        scenarioPlayer.verifyThatASpecificationAboutScenarioContextIsConfirmed(specificationAboutScenarioContext),
        "UnconfirmedDataSpecException is not thrown"
        );
        assertTrue(udseThrown.getMessage().contains("Test data does not confirm it meets the specification: 'CONTEXT SPEC'"));

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

        UnconfirmedDataSpecException udseThrown =Assertions.assertThrows(UnconfirmedDataSpecException.class, () ->
        scenarioPlayer.verifyTheRequestInTheContextWithAParticularSpecification(requestSpecification),
        "UnconfirmedDataSpecException is not thrown"
        );
        assertTrue(udseThrown.getMessage().contains("Test data does not confirm it meets the specification: 'REQUEST SPEC'"));

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

        UnconfirmedDataSpecException udseThrown =Assertions.assertThrows(UnconfirmedDataSpecException.class, () ->
        scenarioPlayer.verifyTheResponseInTheContextWithAParticularSpecification(responseSpecification),
        "UnconfirmedDataSpecException is not thrown"
        );
        assertTrue(udseThrown.getMessage().contains("Test data does not confirm it meets the specification: 'RESPONSE SPEC'"));

    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void shouldSubmitTheRequestToCallAnOperationOfAProductWithCorrectOperation() throws IOException {
        final String methodType = "POST";
        final String uri = "URI";
        final String bodyString = "{}";
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.meetsOperationOfProduct(eq(PRODUCT_NAME), eq(OPERATION))).thenReturn(true);
        when(testData.getMethod()).thenReturn(methodType);
        when(testData.getUri()).thenReturn(uri);
        when(testData.getExpectedResponse()).thenReturn(new ResponseData());
        when(context.getTestData()).thenReturn(testData);
        testData.setMethod("POST");
        when(context.getTheRequest()).thenReturn(requestSpecification);

        Response response = mock(Response.class);
        when(response.getHeaders())
                .thenReturn(new Headers(Collections.singletonList(new Header("Content-Type", "application/json;charset=UTF-8"))));
        when(response.getStatusCode()).thenReturn(200);
        when(response.contentType()).thenReturn("application/json;charset=UTF-8");
        when(requestSpecification.request(eq("POST"), eq(uri))).thenReturn(response);
        QueryableRequestSpecification queryableRequest = mock(QueryableRequestSpecification.class);
        when(SpecificationQuerier.query(eq(requestSpecification))).thenReturn(queryableRequest);

        ResponseBody responseBody = mock(ResponseBody.class);
        when(response.getBody()).thenReturn(responseBody);
        when(responseBody.asString()).thenReturn(bodyString);

        Map<String, Object> body = new HashMap<>();
        body.put("__plainTextValue__", "{}");

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
        when(testData.getUri()).thenReturn("http://localhost");
        when(testData.getMethod()).thenReturn("GETT");

    	FunctionalTestException feThrown =Assertions.assertThrows(FunctionalTestException.class, () ->
        scenarioPlayer.prepareARequestWithAppropriateValues(),
        "FunctionalTestException is not thrown"
        );
        assertTrue(feThrown.getMessage().contains("Method 'GETT' in test data file not recognised"));

    }

    @Test
    public void shouldFailToSubmitTheRequestToCallAnOperationOfAProductWithIncorrectOperation() throws IOException {
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.meetsOperationOfProduct(eq(PRODUCT_NAME), eq(OPERATION))).thenReturn(false);
        when(context.getTestData()).thenReturn(testData);

        UnconfirmedApiCallException uceThrown =Assertions.assertThrows(UnconfirmedApiCallException.class, () ->
        scenarioPlayer.submitTheRequestToCallAnOperationOfAProduct(OPERATION, PRODUCT_NAME),
        "UnconfirmedApiCallException is not thrown"
        );
        assertTrue(uceThrown.getMessage().contains("Test data does not confirm it is calling the following operation of a product: OPERATION -> PRODUCT NAME"));

    }

    @Test
    public void shouldPerformWaitTimeOfFiveSecondsToAllowOperationToComplete() throws InterruptedException {
        long before = System.currentTimeMillis();
        scenarioPlayer.suspendExecutionOnPurposeForAGivenNumberOfSeconds("5", "to allow Logstash to catch up");

        assertEquals(5, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - before));
    }

    @Test
    public void shouldPerformWaitTimeOfTwoSecondsToAllowOperationToComplete() throws InterruptedException {
        long before = System.currentTimeMillis();
        scenarioPlayer.suspendExecutionOnPurposeForAGivenNumberOfSeconds("2.0", "to allow Logstash to catch up");

        assertEquals(2, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - before));
    }

    @Test
    public void shouldFailPerformWaitTimeToAllowOperationToCompleteWhenInvalidEntry() throws InterruptedException {
    	FunctionalTestException feThrown =Assertions.assertThrows(FunctionalTestException.class, () ->
        scenarioPlayer.suspendExecutionOnPurposeForAGivenNumberOfSeconds("five", "to allow Logstash to catch up"),
        "FunctionalTestException is not thrown"
        );
        assertTrue(feThrown.getMessage().contains("Wait time provided is not a valid number: five"));
    }

    @Test
    public void shouldFailPerformWaitTimeToAllowOperationToCompleteWhenNothingIsPassed() throws InterruptedException {
    	FunctionalTestException feThrown =Assertions.assertThrows(FunctionalTestException.class, () ->
        scenarioPlayer.suspendExecutionOnPurposeForAGivenNumberOfSeconds("", "to allow Logstash to catch up"),
        "FunctionalTestException is not thrown"
        );
        assertTrue(feThrown.getMessage().contains("Wait time provided is not a valid number: "));
    }


	/**
	 * Test method for {@link uk.gov.hmcts.befta.player.DefaultBackEndFunctionalTestScenarioPlayer#createCaseWithTheDataProvidedInATestDataObject(java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	void testCreateCaseWithTheDataProvidedInATestDataObjectString() throws IOException {
        // ARRANGE
        String testDataId = "Standard_Token_Creation_Data_For_Case_Creation";
        String[] testDataSpec = {"to create a token for case creation", "to create a full case" };
        
        
        BackEndFunctionalTestScenarioContext testDataContext = createAndPrepareTestScenarioContext(Arrays.asList(testDataSpec), testDataId);
        Mockito.when(BeftaScenarioContextFactory.createBeftaScenarioContext()).thenReturn(testDataContext);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.headers", 1, false)).thenReturn(mapVerifier);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.body", 20)).thenReturn(mapVerifier);
        HttpTestData testData = mock(HttpTestData.class);
        when(testData.meetsSpec(any())).thenReturn(true);
        when(testData.meetsSpec(any())).thenReturn(true);
        when(context.getTestData()).thenReturn(testData);

        // ACT
        scenarioPlayer.createCaseWithTheDataProvidedInATestDataObject(testDataId);

        // ASSERT
        verify(this.context, times(2)).addChildContext(eq(testDataContext));
        verify(testDataContext, times(2)).initializeTestDataFor(eq(testDataId));
        verify(testDataContext, times(2)).setTheResponse(any());
    }

	/**
	 * Test method for {@link uk.gov.hmcts.befta.player.DefaultBackEndFunctionalTestScenarioPlayer#createCaseWithTheDataProvidedInATestDataObject(java.lang.String, java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	void testCreateCaseWithTheDataProvidedInATestDataObjectStringString() throws IOException {
        // ARRANGE
        String testDataId = "TD1";
        String testDataSpec = "Spec1";
        BackEndFunctionalTestScenarioContext testDataContext = createAndPrepareTestScenarioContext(testDataSpec, testDataId);
        Mockito.when(BeftaScenarioContextFactory.createBeftaScenarioContext()).thenReturn(testDataContext);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.headers", 1, false)).thenReturn(mapVerifier);
        mapVerifierMock.when(() -> MapVerifier.createMapVerifier("actualResponse.body", 20)).thenReturn(mapVerifier);

        // ACT
        Assertions.assertThrows(UnconfirmedDataSpecException.class, () -> {
            scenarioPlayer.createCaseWithTheDataProvidedInATestDataObject(testDataId);
          });

			
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.player.DefaultBackEndFunctionalTestScenarioPlayer#verifyThatThereIsAUserInTheContextWithAParticularSpecification(java.lang.String)}.
	 */
	@Test
	void testVerifyThatThereIsAUserInTheContextWithAParticularSpecification() {
		final String specificationAboutUser = "a user";
        HttpTestData testData = mock(HttpTestData.class);
        UserData userData = createUserData(USERNAME, PASSWORD);
        LinkedHashMap<String, UserData> users = new LinkedHashMap<String, UserData>() {
            private static final long serialVersionUID = 1L;
            {
                put("invokingUser", userData);
            }
        };
        Entry<String, UserData> entry = users.entrySet().iterator().next();
        when(testData.meetsSpec(any())).thenReturn(true);
        when(testData.getInvokingUser()).thenReturn(userData);
        when(testData.getUsers()).thenReturn(users);
        when(testData.getUserEntryAt(0)).thenReturn(new AbstractMap.SimpleEntry<>("invokingUser", userData));
        when(context.getTestData()).thenReturn(testData);
        when(EnvironmentVariableUtils.resolvePossibleVariable(USERNAME)).thenReturn(USERNAME);
        when(EnvironmentVariableUtils.resolvePossibleVariable(PASSWORD)).thenReturn(PASSWORD);
        BeftaMain.setTaAdapter(adapter);
        when(context.getNextUserToAuthenticate()).thenReturn(entry);
        
        scenarioPlayer.verifyThatThereIsAUserInTheContextWithAParticularSpecification(specificationAboutUser);

        verify(testData).meetsSpec(specificationAboutUser);
		
	}


	/**
	 * Test method for {@link uk.gov.hmcts.befta.player.DefaultBackEndFunctionalTestScenarioPlayer#submitTheRequestToCallAnOperationOfAProduct(java.lang.String, java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	void testSubmitTheRequestToCallAnOperationOfAProduct() throws IOException {
        final String methodType = "POST";
        final String uri = "URI";
        final String bodyString = "{}";
        HttpTestData testData = mock(HttpTestData.class);

        when(testData.meetsOperationOfProduct(eq(PRODUCT_NAME), eq(OPERATION))).thenReturn(true);
        when(testData.getMethod()).thenReturn(methodType);
        when(testData.getUri()).thenReturn(uri);
        ResponseData responseData1 = new ResponseData();
        Map<String, Object> body = new HashMap<>();
        body.put("__fileInBody__", "{}");
        responseData1.setBody(body);
        when(testData.getExpectedResponse()).thenReturn(responseData1);
        when(context.getTestData()).thenReturn(testData);
        testData.setMethod("POST");
        when(context.getTheRequest()).thenReturn(requestSpecification);

        Response response = mock(Response.class);
        when(response.getHeaders())
                .thenReturn(new Headers(Collections.singletonList(new Header("Content-Type", "application/json;charset=UTF-8"))));
        when(response.getStatusCode()).thenReturn(200);
        when(response.contentType()).thenReturn("application/json;charset=UTF-8");
        when(requestSpecification.request(eq("POST"), eq(uri))).thenReturn(response);
        QueryableRequestSpecification queryableRequest = mock(QueryableRequestSpecification.class);
        when(SpecificationQuerier.query(eq(requestSpecification))).thenReturn(queryableRequest);

        ResponseBody responseBody = mock(ResponseBody.class);
        when(response.getBody()).thenReturn(responseBody);
        when(responseBody.asString()).thenReturn(bodyString);

        scenarioPlayer.submitTheRequestToCallAnOperationOfAProduct(OPERATION, PRODUCT_NAME);

        verify(testData).setActualResponse((ResponseData) captor.capture());
        ResponseData responseData = (ResponseData) captor.getValue();
        assertEquals(200, responseData.getResponseCode());
        assertEquals(1, responseData.getHeaders().size());
        assertEquals("OK", responseData.getResponseMessage());
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

    private BackEndFunctionalTestScenarioContext createAndPrepareMockPrerequisiteContext(String testDataId,
                                                                                         BackEndFunctionalTestScenarioContext parentContext) {
        BackEndFunctionalTestScenarioContext subcontext
                = createAndPrepareTestScenarioContext(DefaultBackEndFunctionalTestScenarioPlayer.PREREQUISITE_SPEC, testDataId);
        when(subcontext.getParentContext()).thenReturn(parentContext);
        return subcontext;
    }
    private BackEndFunctionalTestScenarioContext createAndPrepareTestScenarioContext(String testDataSpec,  String testDataId) {

    	List<String> testDataSpecs = Collections.singletonList(testDataSpec);
    	return createAndPrepareTestScenarioContext(testDataSpecs,testDataId);
    }
    private BackEndFunctionalTestScenarioContext createAndPrepareTestScenarioContext(List<String> testDataSpecs, String testDataId) {

        RequestData requestData = new RequestData();
        ResponseData expectedResponseData = new ResponseData();
        expectedResponseData.setHeaders(new HashMap<>());

        // test data
        HttpTestData testData = new HttpTestData();
        testData.setRequest(requestData);
        testData.setExpectedResponse(expectedResponseData);
        testData.set_guid_(testDataId);
        testData.setSpecs(testDataSpecs);
        testData.setOperationName("GET " + testDataId);
        testData.setProductName("Test Product Name");

        String uri = "http://localhost/" + testDataId;
        testData.setUri(uri);
        testData.setMethod("GET");

        // response
        Response response = Mockito.mock(Response.class);
        when(response.getHeaders())
                .thenReturn(new Headers(Collections.singletonList(new Header("Content-Type", "application/json;charset=UTF-8"))));
        when(response.getStatusCode()).thenReturn(200);
        when(response.contentType()).thenReturn("application/json;charset=UTF-8");

        ResponseBody<?> responseBody = mock(ResponseBody.class);
        when(responseBody.asString()).thenReturn("{}");
        when(response.getBody()).thenReturn(responseBody);

        // request
        RequestSpecification requestSpecification = Mockito.mock(RequestSpecification.class);
        QueryableRequestSpecification queryableRequest = mock(QueryableRequestSpecification.class);

        when(requestSpecification.request(eq("GET"), eq(uri))).thenReturn(response);
        when(SpecificationQuerier.query(eq(requestSpecification))).thenReturn(queryableRequest);

        when(mapVerifier.verifyMap(any(), any())).thenReturn(verificationResult);
        when(verificationResult.isVerified()).thenReturn(true);

        // context
        BackEndFunctionalTestScenarioContext context1 = Mockito.mock(BackEndFunctionalTestScenarioContext.class);

        when(context1.getTestData()).thenReturn(testData);
        when(context1.getTheRequest()).thenReturn(requestSpecification);
        when(context1.getTheResponse()).thenReturn(expectedResponseData);
        when(context1.getContextId()).thenReturn(testDataId);

        return context1;
    }

}
