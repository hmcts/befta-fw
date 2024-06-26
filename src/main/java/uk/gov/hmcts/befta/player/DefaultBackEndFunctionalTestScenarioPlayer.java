package uk.gov.hmcts.befta.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.aspectj.util.FileUtil;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.befta.AuthenticationRetryConfiguration;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.TestAutomationConfig;
import uk.gov.hmcts.befta.TestAutomationConfig.ResponseHeaderCheckPolicy;
import uk.gov.hmcts.befta.data.FileInBody;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.RequestData;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.exception.InvalidTestDataException;
import uk.gov.hmcts.befta.exception.UnconfirmedApiCallException;
import uk.gov.hmcts.befta.exception.UnconfirmedDataSpecException;
import uk.gov.hmcts.befta.factory.BeftaScenarioContextFactory;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleService;
import uk.gov.hmcts.befta.featuretoggle.ScenarioFeatureToggleInfo;
import uk.gov.hmcts.befta.util.BeftaUtils;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;
import uk.gov.hmcts.befta.util.JsonUtils;
import uk.gov.hmcts.befta.util.MapVerificationResult;
import uk.gov.hmcts.befta.util.MapVerifier;
import uk.gov.hmcts.befta.util.Retryable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultBackEndFunctionalTestScenarioPlayer implements BackEndFunctionalTestAutomationDSL {

    static final String HTTP_S_REGEX = "^(http|https):.*";
    static final String PREREQUISITE_SPEC = "As a prerequisite";

    private Logger logger = LoggerFactory.getLogger(DefaultBackEndFunctionalTestScenarioPlayer.class);

    private final BackEndFunctionalTestScenarioContext scenarioContext;
    private Scenario scenario;
    private ObjectMapper mapper = new ObjectMapper();
    private static final double WAIT_TIME  = 1;

    public DefaultBackEndFunctionalTestScenarioPlayer() {
        RestAssured.useRelaxedHTTPSValidation();
        scenarioContext = BeftaScenarioContextFactory.createBeftaScenarioContext();
    }

    @Before
    public void cucumberPrepare(Scenario scenario) {
        this.scenario = scenario;
        FeatureToggleService<Scenario, ScenarioFeatureToggleInfo> toggleService = BeftaMain.getFeatureToggleService();
        if (toggleService != null) {
            try {
                ScenarioFeatureToggleInfo toggleInfo = toggleService.getToggleStatusFor(scenario);
                if (toggleInfo != null && !toggleInfo.shouldScenarioBeRun()) {
                    BeftaUtils.skipScenario(scenario, toggleInfo);
                }
            } catch (FeatureToggleCheckFailureException e) {
                BeftaUtils.defaultLog(
                        scenario,
                        "Feature toggle check failed, will assume toggle on and run the scenario. Failure message: "
                                + e.getMessage());
            }
        }
    }

    @Override
    @Given("an appropriate test context as detailed in the test data source")
    public void initializeAppropriateTestContextAsDetailedInTheTestDataSource() {
        scenarioContext.initializeTestDataFor(scenario);
        String logPrefix = scenarioContext.getCurrentScenarioTag() + ": Test data ";
        if (scenarioContext.getTestData() != null) {
            logger.info(logPrefix + "was loaded successfully");
        } else {
            logger.info(logPrefix + "was not found");
        }
    }

    @Override
    @Given("a case that has just been created as in [{}]")
    public void createCaseWithTheDataProvidedInATestDataObject(String caseCreationDataId) throws IOException {
        createCase("to create a full case", caseCreationDataId);
    }

    @Override
    @Given("a case [{}] created as in [{}]")
    public void createCaseWithTheDataProvidedInATestDataObject(String specAboutCase, String caseCreationDataId)
            throws IOException {
        createCase(specAboutCase, caseCreationDataId);
    }

    private void createCase(String specAboutCase, String caseCreationDataId) throws IOException {
        String accompanyingTokenCreationDataId = caseCreationDataId + "_Token_Creation";
        HttpTestData tokenCreationData = BackEndFunctionalTestScenarioContext.DATA_SOURCE
                .getDataForTestCall(accompanyingTokenCreationDataId);
        if (tokenCreationData == null) {
            accompanyingTokenCreationDataId = "Standard_Token_Creation_Data_For_Case_Creation";
        }
        performAndVerifyTheExpectedResponseForAnApiCall("to create a token for case creation",
                accompanyingTokenCreationDataId);
        performAndVerifyTheExpectedResponseForAnApiCall(specAboutCase, caseCreationDataId);
    }

    @Override
    @Given("a user [{}]")
    @Given("a user with [{}]")
    public void verifyThatThereIsAUserInTheContextWithAParticularSpecification(String specificationAboutAUser) {
        verifyThatThereIsAUserInTheContextWithAParticularSpecification(this.scenarioContext, specificationAboutAUser);
    }

    private void verifyThatThereIsAUserInTheContextWithAParticularSpecification(
            BackEndFunctionalTestScenarioContext scenarioContext, String specificationAboutAUser) {
        boolean doesTestDataMeetSpec = scenarioContext.getTestData().meetsSpec(specificationAboutAUser);
        if (!doesTestDataMeetSpec) {
            throw new UnconfirmedDataSpecException(specificationAboutAUser);
        }

        Entry<String, UserData> userDataEntry = scenarioContext.getNextUserToAuthenticate();

        if (userDataEntry != null) {
            verifyTheUserBeingSpecifiedInTheContext(scenarioContext, userDataEntry.getKey(), userDataEntry.getValue());
        } else {
            String message = "The user [" + specificationAboutAUser
                    + "] will not be verified with authentication as it is not listed in test data.";
            scenario.log(message);
            logger.warn(message);
        }
    }

    @Override
    @Given("[{}] in the context of the scenario")
    @Given("[{}] in the context")
    public void verifyThatASpecificationAboutScenarioContextIsConfirmed(String specificationAboutScenarioContext) {
        if (specificationAboutScenarioContext.toLowerCase().startsWith("a user ")) {
            verifyThatThereIsAUserInTheContextWithAParticularSpecification(specificationAboutScenarioContext);
        } else {
            boolean doesTestDataMeetSpec = scenarioContext.getTestData().meetsSpec(specificationAboutScenarioContext);
            if (!doesTestDataMeetSpec) {
                throw new UnconfirmedDataSpecException(specificationAboutScenarioContext);
            }
        }
    }

    @Override
    @When("a request is prepared with appropriate values")
    public void prepareARequestWithAppropriateValues() throws IOException {
        runPrerequisitesSpecifiedInTheContext(this.scenarioContext);
        prepareARequestWithAppropriateValues(this.scenarioContext);
    }

    private void prepareARequestWithAppropriateValues(BackEndFunctionalTestScenarioContext scenarioContext)
            throws IOException {
        scenarioContext.injectDataFromContextBeforeApiCall();
        RequestSpecification raRequest = buildRestAssuredRequestWith(scenarioContext.getTestData());

        scenarioContext.setTheRequest(raRequest);
        scenario.log(
                "Request prepared with the following variables: "
                + JsonUtils.getPrettyJsonFromObject(scenarioContext.getTestData().getRequest()));
    }

    private void runPrerequisitesSpecifiedInTheContext(final BackEndFunctionalTestScenarioContext scenarioContext)
            throws IOException {

        List<Object> prerequisites = scenarioContext.getTestData().getPrerequisites();

        if (prerequisites != null && !prerequisites.isEmpty()) {
            scenario.log("Prerequisite processing started: [" + scenarioContext.getContextId() + "]");

            // prerequisites as list of objects
            for (Object prerequisite : prerequisites) {
                // if prerequisite is just a string
                if (prerequisite instanceof String) {
                    String prerequisiteAsString = (String) prerequisite;
                    runSinglePrerequisite(scenarioContext, prerequisiteAsString, prerequisiteAsString);

                    // if prerequisites is a map of pairs: "context_id": "test_data_id"
                } else if (prerequisite instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> prerequisiteAsMap = (Map<String, String>) prerequisite;
                    for (Map.Entry<String, String> prerequisiteAsEntry : prerequisiteAsMap.entrySet()) {
                        runSinglePrerequisite(scenarioContext, prerequisiteAsEntry.getKey(),
                                prerequisiteAsEntry.getValue());
                    }

                } else {
                    throw new FunctionalTestException("Unrecognised prerequisite data type");
                }
            }

            scenario.log("Prerequisite processing complete: [" + scenarioContext.getContextId() + "]");
        }
    }

    private void runSinglePrerequisite(final BackEndFunctionalTestScenarioContext parentContext, String subcontextId,
            String testDataId) throws IOException {

        checkCyclicPrerequisiteDependency(parentContext, subcontextId);

        // avoid undesirable re-execution
        if (shouldExecutePrerequisite(parentContext, subcontextId)) {
            scenario.log("Prerequisite: [" + parentContext.getContextId() + "].[" + subcontextId
                    + "] from ["
                    + testDataId + "]");
            performAndVerifyTheExpectedResponseForAnApiCall(parentContext, PREREQUISITE_SPEC, testDataId, subcontextId,
                    null);
        } else {
            scenario.log("Skipping prerequisite: [" + parentContext.getContextId() + "].[" + subcontextId + "]");
        }
    }

    private boolean shouldExecutePrerequisite(BackEndFunctionalTestScenarioContext parentContext, String subcontextId) {
        boolean prerequisiteAlreadyDefined = parentContext.getChildContexts().containsKey(subcontextId);
        boolean prerequisiteAlreadyExecuted = prerequisiteAlreadyDefined
                && parentContext.getChildContexts().get(subcontextId).getTestData().getActualResponse() != null;
        return !prerequisiteAlreadyExecuted;
    }

    private void checkCyclicPrerequisiteDependency(BackEndFunctionalTestScenarioContext parentContext,
            String subcontextId) {

        if (subcontextId.equals(parentContext.getContextId())) {
            throw new InvalidTestDataException(
                    "Cyclic dependency discovered for prerequisite with contextId: " + subcontextId);
        } else if (parentContext.getParentContext() != null) {
            checkCyclicPrerequisiteDependency(parentContext.getParentContext(), subcontextId);
        }
    }

    private RequestSpecification buildRestAssuredRequestWith(HttpTestData testData) throws IOException {
        RequestSpecification aRequest = RestAssured.given();

        try {
            Method.valueOf(testData.getMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FunctionalTestException("Method '" + testData.getMethod() + "' in test data file not recognised");
        }

        RequestData requestData = testData.getRequest();
        if (requestData.getHeaders() != null) {
            requestData.getHeaders().forEach((header, value) -> aRequest.header(header, value));
        }

        if (requestData.getPathVariables() != null) {
            requestData.getPathVariables().forEach((pathVariable, value) -> aRequest.pathParam(pathVariable, value));
        }

        if (requestData.getQueryParams() != null) {
            requestData.getQueryParams().forEach((queryParam, value) -> aRequest.queryParam(queryParam, value));
        }

        if (requestData.getBody() != null) {
            buildRequestBody(aRequest, requestData);
        }
        return aRequest;
    }

    private void buildRequestBody(RequestSpecification request, RequestData requestData) throws IOException {

        Object requestBodyContent = requestData.getBody();
        if (requestData.getBody().containsKey("arrayInMap"))
            requestBodyContent = requestData.getBody().get("arrayInMap");

        if (requestData.isMultipart()) {
            if (requestBodyContent instanceof List<?>) {
                for (Object object : (List<?>) requestBodyContent) {
                    putMultipartToRequest(request, object);
                }
            } else {
                putMultipartToRequest(request, requestBodyContent);

            }
        } else {
            request.body(mapper.writeValueAsBytes(requestBodyContent));
        }
    }

    private void putMultipartToRequest(RequestSpecification request, Object multipartObject) {
        @SuppressWarnings("unchecked")
        Map<String, String> multipartInfo = (Map<String, String>) multipartObject;
        String controlName = multipartInfo.get("key");
        Object multipartValue = multipartInfo.get("value");

        File fileToUpload = null;
        Boolean deleteAfterUpload = true;
        try {
            if (multipartInfo.containsKey("filePath")) {
                fileToUpload = BeftaUtils.getClassPathResourceIntoTemporaryFile(multipartInfo.get("filePath"));
                multipartValue = fileToUpload;
            } else if (multipartInfo.containsKey("localFilePath")) {
                fileToUpload = new File(multipartInfo.get("localFilePath"));
                multipartValue = fileToUpload;
                deleteAfterUpload = false;
            }
            request.multiPart(controlName, multipartValue);
        } catch (Exception e) {
            throw new FunctionalTestException("Failed to put multi-part into the request: " + controlName, e);
        } finally {
            if (fileToUpload != null && deleteAfterUpload) {
                fileToUpload.deleteOnExit();
            }
        }
    }

    @Override
    @When("the request [{}]")
    public void verifyTheRequestInTheContextWithAParticularSpecification(String requestSpecification) {
        verifyTheRequestInTheContextWithAParticularSpecification(this.scenarioContext, requestSpecification);
    }

    private void verifyTheRequestInTheContextWithAParticularSpecification(
            BackEndFunctionalTestScenarioContext scenarioContext, String requestSpecification) {
        if (!scenarioContext.getTestData().meetsSpec(requestSpecification)) {
            throw new UnconfirmedDataSpecException(requestSpecification);
        }
    }

    @Override
    @When("it is submitted to call the [{}] operation of [{}]")
    public void submitTheRequestToCallAnOperationOfAProduct(String operation, String productName) throws IOException {
        submitTheRequestToCallAnOperationOfAProduct(this.scenarioContext, operation, productName);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void submitTheRequestToCallAnOperationOfAProduct(BackEndFunctionalTestScenarioContext scenarioContext,
            String operationName, String productName) throws IOException {
        boolean isCorrectOperation = scenarioContext.getTestData().meetsOperationOfProduct(productName, operationName);
        if (!isCorrectOperation) {
            throw new UnconfirmedApiCallException(productName, operationName);
        }

        RequestSpecification theRequest = scenarioContext.getTheRequest();
        QueryableRequestSpecification queryableRequest = SpecificationQuerier.query(theRequest);

        HttpTestData testData = scenarioContext.getTestData();
        String uri = testData.getUri();

        // set `baseUri` if testData URI is only a partial URL
        Matcher httpUriMatcher = Pattern.compile(HTTP_S_REGEX).matcher(uri.trim().toLowerCase());
        if (!httpUriMatcher.find()) {
            theRequest.baseUri(TestAutomationConfig.INSTANCE.getTestUrl());
        }

        Retryable retryable = scenarioContext.getRetryConfiguration();
        Retryer<Response> retryer;

        logger.info("Calling: {} {}", testData.getMethod(), uri);
        if (retryable.getNonRetryableHttpMethods().contains("*") || retryable.getNonRetryableHttpMethods()
                                                                                .contains(testData.getMethod())) {
            logger.info("Applying no-retry policy...");
            retryer = RetryerBuilder.<Response>newBuilder().build();
        } else {
            logger.info("Applying active retry policy...");

            retryer = RetryerBuilder.<Response>newBuilder()
                    .withRetryListener(retryable.getRetryListener())
                    .retryIfException(e -> {
                        boolean isRetryableException = retryable.getRetryableExceptions().contains(e.getClass());
                        Throwable cause = e.getCause();
                        boolean isRetryableCause = cause != null && retryable.getRetryableExceptions()
                                .contains(cause.getClass());
                        return isRetryableException || isRetryableCause;
                    })
                    .retryIfResult(res -> retryable.getStatusCodes().contains(res.getStatusCode()))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(retryable.getMaxAttempts()))
                    .withWaitStrategy(WaitStrategies.fixedWait(retryable.getDelay(), TimeUnit.MILLISECONDS))
                    .build();
        }

        Response response = executeHttpRequestWithRetry(theRequest, testData.getMethod(), uri, retryer);

        ResponseData responseData = convertRestAssuredResponseToBeftaResponse(scenarioContext, response);
        scenarioContext.getTestData().setActualResponse(responseData);
        scenarioContext.setTheResponse(responseData);
        scenario.log("Called: " + queryableRequest.getMethod() + " " + queryableRequest.getURI());
        scenario.log("Response:\n" + JsonUtils.getPrettyJsonFromObject(scenarioContext.getTheResponse()));
        scenarioContext.injectDataFromContextAfterApiCall();
    }

    private Response executeHttpRequestWithRetry(RequestSpecification theRequest, String method, String uri,
                                                        Retryer<Response> retryer) {
        try {
            Callable<Response> callable = () -> theRequest.request(method, uri);
            return retryer.call(callable);
        } catch (RetryException retryException) {
            throw new FunctionalTestException(
                    String.format("Retry Exception when calling %s", uri), retryException);
        } catch (ExecutionException executionException) {
            throw new FunctionalTestException(
                    String.format("Execution Exception when authenticating user %s", uri),
                    executionException);
        }
    }

    private ResponseData convertRestAssuredResponseToBeftaResponse(BackEndFunctionalTestScenarioContext scenarioContext,
            Response response) throws IOException {
        Map<String, Object> responseHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        response.getHeaders().forEach(header -> responseHeaders.put(header.getName(), header.getValue()));
        ResponseData responseData = new ResponseData();
        responseData.setResponseCode(response.getStatusCode());
        String reasonPhrase = EnglishReasonPhraseCatalog.INSTANCE.getReason(response.getStatusCode(), null);
        responseData.setResponseMessage(reasonPhrase);
        responseData.setHeaders(responseHeaders);

        String jsonForBody;
        Map<String, Object> wrappedInMap = null;
        if (shouldTreatBodyAsAFile(scenarioContext.getTestData().getExpectedResponse())) {
            jsonForBody = getFileInMapJson(response);
        } else {
            jsonForBody = response.getBody() == null ? null : response.getBody().asString();
            if (jsonForBody != null && !jsonForBody.isEmpty()) {
                wrappedInMap = wrapInMapIfNecessary(jsonForBody, response.getContentType());
            }
        }

        Map<String, Object> mapForBody = getMapForBodyFrom(wrappedInMap, jsonForBody);
        responseData.setBody(mapForBody);

        return responseData;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapForBodyFrom(Map<String, Object> wrappedInMap, String jsonForBody) {
        if (wrappedInMap != null)
            return wrappedInMap;
        if (jsonForBody == null || jsonForBody.isEmpty())
            return null;
        try {
            return JsonUtils.readObjectFromJsonText(jsonForBody, Map.class);
        } catch (Exception e) {
            scenario.log("Can't convert the body to JSON: \n" + jsonForBody);
            throw new FunctionalTestException("Can't convert the body to JSON.", e);
        }
    }

    private boolean shouldTreatBodyAsAFile(ResponseData expectedResponse) {
        return expectedResponse.getBody() != null && expectedResponse.getBody().containsKey("__fileInBody__");
    }

    private String getFileInMapJson(Response response) throws IOException {
        InputStream inputStream = response.getBody().asInputStream();
        if (inputStream == null) {
            return null;
        }
        File tempFile = new File("__download__" + System.currentTimeMillis());
        try {
            tempFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            FileUtil.copyStream(inputStream, outputStream);
            outputStream.close();
            FileInBody fib = new FileInBody("file");
            fib.setSize("" + tempFile.length());
            fib.setContentHash("hash");
            String json = JsonUtils.getJsonFromObject(fib);
            json = "{\"__fileInBody__\":" + json + "}";
            return json;

        } finally {
            tempFile.delete();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> wrapInMapIfNecessary(String apiResponse, String contentType) {
        Map<String, Object> wrapperMap = null;
        if (isResponseJson(apiResponse, contentType)) {
            if (isArrayInJson(apiResponse, contentType)) {
                String wrapperString = "{\"arrayInMap\":" + apiResponse + "}";
                try {
                    wrapperMap = JsonUtils.readObjectFromJsonText(wrapperString, Map.class);
                } catch (Exception e) {
                    scenario.log("Can't convert the body to JSON: \n" + wrapperString);
                    throw new FunctionalTestException("Can't convert the body to JSON.", e);
                }
            }
        } else {
            wrapperMap = new HashedMap<>();
            wrapperMap.put("__plainTextValue__", apiResponse.replaceAll("\n", ""));
        }
        return wrapperMap;
    }

    private boolean isResponseJson(String apiResponse, String contentType) {

        return contentType != null && contentType.toLowerCase().contains("json");
    }

    private boolean isArrayInJson(String apiResponse, String contentType) {

        return isResponseJson(apiResponse, contentType) && apiResponse != null && apiResponse.startsWith("[")
                && apiResponse.endsWith("]");
    }

    @Override
    @Then("a positive response is received")
    public void verifyThatAPositiveResponseWasReceived() {
        int responseCode = scenarioContext.getTheResponse().getResponseCode();
        scenario.log("Response code: " + responseCode);
        boolean responseCodePositive = responseCode / 100 == 2;
        Assert.assertTrue("Response code '" + responseCode + "' is not a success code.", responseCodePositive);
    }

    @Override
    @Then("a negative response is received")
    public void verifyThatANegativeResponseWasReceived() {
        int responseCode = scenarioContext.getTheResponse().getResponseCode();
        scenario.log("Response code: " + responseCode);
        boolean responseCodePositive = responseCode / 100 == 2;
        Assert.assertFalse("Response code '" + responseCode + "' is unexpectedly a success code.",
                responseCodePositive);
    }

    @Override
    @Then("the response has all the details as expected")
    @Then("the response has all other details as expected")
    public void verifyThatTheResponseHasAllTheDetailsAsExpected() throws IOException {
        verifyThatTheResponseHasAllTheDetailsAsExpected(this.scenarioContext, null,null,
                null,null,null);
    }

    private void verifyThatTheResponseHasAllTheDetailsAsExpected(BackEndFunctionalTestScenarioContext scenarioContext,
                                                                 BackEndFunctionalTestScenarioContext parentContext,
                                                                 String testDataSpec, String testDataId,
                                                                 String contextId, String timeOut)
            throws IOException {
        logger.info("verifyThatTheResponseHasAllTheDetailsAsExpected1 {},{} ", scenarioContext, parentContext);
        logger.info("verifyThatTheResponseHasAllTheDetailsAsExpected2 {},{} ", testDataSpec, testDataId);
        logger.info("verifyThatTheResponseHasAllTheDetailsAsExpected3 {},{} ", contextId, timeOut);
        ResponseData expectedResponse = scenarioContext.getTestData().getExpectedResponse();
        ResponseData actualResponse = scenarioContext.getTheResponse();

        List<String> issuesInResponseHeaders = null, issuesInResponseBody = null;
        String issueWithResponseCode = null;
        logger.info("actualResponse.getResponseCode() {} expectedResponse.getResponseCode() {}",
                actualResponse.getResponseCode(), expectedResponse.getResponseCode());
        if (actualResponse.getResponseCode() != expectedResponse.getResponseCode()) {
            issueWithResponseCode = "Response code mismatch, expected: " + expectedResponse.getResponseCode()
                    + ", actual: " + actualResponse.getResponseCode();
        }

        MapVerificationResult headerVerification = MapVerifier.createMapVerifier("actualResponse.headers", 1, false)
                .verifyMap(expectedResponse.getHeaders(), actualResponse.getHeaders());
        logger.info("headerVerification.isVerified() {}", headerVerification.isVerified());
        if (!headerVerification.isVerified()) {
            issuesInResponseHeaders = headerVerification.getAllIssues();
        }

        MapVerificationResult bodyVerification = MapVerifier.createMapVerifier("actualResponse.body", 20)
                .verifyMap(expectedResponse.getBody(), actualResponse.getBody());
        if (!bodyVerification.isVerified()) {
            issuesInResponseBody = bodyVerification.getAllIssues();
        }

        processAnyIssuesInResponse(issueWithResponseCode, issuesInResponseHeaders, issuesInResponseBody,parentContext,
                testDataSpec,testDataId,contextId,timeOut);
    }

    private void processAnyIssuesInResponse(String issueWithResponseCode, List<String> issuesInResponseHeaders,
            List<String> issuesInResponseBody, BackEndFunctionalTestScenarioContext parentContext, String testDataSpec,
                                            String testDataId, String contextId, String timeOut)  {
        logger.info("In processAnyIssuesInResponse1 {}, {} ", issueWithResponseCode, issuesInResponseHeaders);
        logger.info("In processAnyIssuesInResponse2 {}, {} ", issuesInResponseBody, parentContext);
        logger.info("In processAnyIssuesInResponse3 {}, {}, {}, {} ",  testDataSpec, testDataId, contextId, timeOut);
        StringBuffer allVerificationIssues = new StringBuffer(
                "Could not verify the actual response against expected one. Below are the issues.").append('\n');

        if (issueWithResponseCode != null) {
            allVerificationIssues.append(issueWithResponseCode).append('\n');
        }

        ResponseHeaderCheckPolicy headerPolicy = BeftaMain.getConfig().getResponseHeaderCheckPolicy();
        if (issuesInResponseHeaders != null) {
            if (headerPolicy.equals(ResponseHeaderCheckPolicy.JUST_WARN)) {
                logger.warn("Issues found in actual response headers as follows:");
                issuesInResponseHeaders.forEach(issue -> logger.warn(issue));
                allVerificationIssues.append("***").append(issuesInResponseHeaders)
                        .append(" issues in headers are listed just as warnings.").append('\n');
            }
            if (headerPolicy.equals(ResponseHeaderCheckPolicy.FAIL_TEST)) {
                issuesInResponseHeaders.forEach(issue -> allVerificationIssues.append(issue).append('\n'));
            }
        }

        if (issuesInResponseBody != null) {
            issuesInResponseBody.forEach(issue -> allVerificationIssues.append(issue).append('\n'));
        }

        boolean anyVerificationIssue = issueWithResponseCode != null
                || (issuesInResponseHeaders != null && headerPolicy.equals(ResponseHeaderCheckPolicy.FAIL_TEST))
                || issuesInResponseBody != null;
        logger.info("anyVerificationIssue is {}, timeOut {}", anyVerificationIssue, timeOut);
        if (anyVerificationIssue && null != timeOut) {
            DecimalFormat df = new DecimalFormat("#.##");
            Double timeOutMs = (Double.valueOf(df.format(Double.parseDouble(timeOut))) * 1000);
            Double waitTimeMs = (Double.valueOf(df.format(WAIT_TIME)) * 1000);
            Double timeoutExpiredMs = System.currentTimeMillis() + timeOutMs;
            Double lastCheckedTime = (double) System.currentTimeMillis();
            //try {
                while (lastCheckedTime < timeoutExpiredMs) {
                    Double currentTime = (double) System.currentTimeMillis();
                    logger.info("comparing times {} ,  {} ", currentTime - lastCheckedTime, waitTimeMs);
                    // wait for 1 second and retry the request
                     if (currentTime - lastCheckedTime >= waitTimeMs) {
                         logger.info("calling the method again time {}, wait time is  {}",
                                 currentTime - lastCheckedTime, waitTimeMs);
                         logger.info("performAndVerifyTheExpectedResponseForAnApiCall again {}, {}, {}, {}, {}",
                                 parentContext, testDataSpec, testDataId, contextId, timeOutMs);
                         logger.info("repeat the request");
                   /* performAndVerifyTheExpectedResponseForAnApiCall(parentContext, testDataSpec, testDataId,
                            contextId, timeOut);*/
                    lastCheckedTime = currentTime;
                    }
                    Thread.yield();
                }
          /*  } catch (IOException e) {
                throw new RuntimeException(e);
            }*/

        }
        logger.info("Asserting anyVerificationIssue is {}", anyVerificationIssue);
        Assert.assertFalse(allVerificationIssues.toString(), anyVerificationIssue);
    }

    @Override
    @Then("the response [{}]")
    public void verifyTheResponseInTheContextWithAParticularSpecification(String responseSpecification) {
        boolean responseSpecificationConfirmed = scenarioContext.getTestData().meetsSpec(responseSpecification);
        if (!responseSpecificationConfirmed) {
            throw new UnconfirmedDataSpecException(responseSpecification);
        }
    }

    @Override
    @Given("a successful call [{}] as in [{}]")
    @Given("another successful call [{}] as in [{}]")
    @Then("a call [{}] will get the expected response as in [{}]")
    @Then("another call [{}] will get the expected response as in [{}]")
    public void performAndVerifyTheExpectedResponseForAnApiCall(String testDataSpec, String testDataId)
            throws IOException {
        logger.info("Executing existing method {}, {}, {}, {}", this.scenarioContext, testDataSpec, testDataId, null);
        performAndVerifyTheExpectedResponseForAnApiCall(this.scenarioContext, testDataSpec, testDataId, null,
                null);

    }

    @Override
    @Given("a successful call [{}] until the expected response is received [{}] within a timeout of [{}]")
    public void performAndVerifyTheExpectedResponseForAnApiCallWithTimeout(String testDataSpec, String testDataId,
                                                                           String timeOut) throws IOException {
        logger.info("Executing the time out method with timeout1 {}, {} ", this.scenarioContext,  testDataSpec);
        logger.info("Executing the time out method with timeout2 {}, {} ", testDataId, timeOut);
        performAndVerifyTheExpectedResponseForAnApiCall(this.scenarioContext, testDataSpec, testDataId, null,
                timeOut);
    }

    private void performAndVerifyTheExpectedResponseForAnApiCall(BackEndFunctionalTestScenarioContext parentContext,
            String testDataSpec, String testDataId, String contextId, String timeOut) throws IOException {
        logger.info("In performAndVerifyTheExpectedResponseForAnApiCall1 {}, {}" , parentContext ,testDataSpec);
        logger.info("In performAndVerifyTheExpectedResponseForAnApiCall2 {}, {}, {}" , testDataId,contextId ,timeOut);
        BackEndFunctionalTestScenarioContext subcontext = BeftaScenarioContextFactory.createBeftaScenarioContext();
        subcontext.initializeTestDataFor(testDataId);
        subcontext.setRetryableTag(this.scenarioContext.getRetryableTag());
        if (contextId == null) {
            parentContext.addChildContext(subcontext);
        } else {
            parentContext.addChildContext(contextId, subcontext);
        }
        logger.info("calling verifyAllUsersInTheContext");
        verifyAllUsersInTheContext(subcontext);
        runPrerequisitesSpecifiedInTheContext(subcontext);
        prepareARequestWithAppropriateValues(subcontext);
        logger.info("verifyTheRequestInTheContextWithAParticularSpecification");
        verifyTheRequestInTheContextWithAParticularSpecification(subcontext, testDataSpec);
        logger.info("submitTheRequestToCallAnOperationOfAProduct");
        submitTheRequestToCallAnOperationOfAProduct(subcontext, subcontext.getTestData().getOperationName(),
                subcontext.getTestData().getProductName());
        logger.info("verifyThatTheResponseHasAllTheDetailsAsExpected");
        verifyThatTheResponseHasAllTheDetailsAsExpected(subcontext, parentContext, testDataSpec,testDataId, contextId,
                timeOut);
    }

    private void verifyAllUsersInTheContext(BackEndFunctionalTestScenarioContext scenarioContext) {
        scenarioContext.getTestData().getUsers()
                .forEach((key, userData) -> verifyTheUserBeingSpecifiedInTheContext(scenarioContext, key, userData));
    }

    private void verifyTheUserBeingSpecifiedInTheContext(final BackEndFunctionalTestScenarioContext scenarioContext,
            final String userKey, final UserData userBeingSpecified) {
        String prefix = "users[" + userKey + "]";
        resolveUserData(scenarioContext, prefix, userBeingSpecified);
        scenario.log("User Email Id [" + userBeingSpecified.getUsername() + "]...");
        authenticateUser(scenarioContext, prefix, userBeingSpecified);
        scenario.log("User Id [" + userBeingSpecified.getId() + "].");
    }

    private void resolveUserData(final BackEndFunctionalTestScenarioContext scenarioContext, String prefix,
            UserData aUser) {
        String resolvedUsername = EnvironmentVariableUtils.resolvePossibleVariable(aUser.getUsername());

        String resolvedPassword = EnvironmentVariableUtils.resolvePossibleVariable(aUser.getPassword());
        if (resolvedPassword.equals(aUser.getPassword())) {
            logger.warn(scenarioContext.getTestData().get_guid_()
                    + ": Expected environment variable declaration "
                    + "for " + prefix + ".password but found a hard coded value!'");
        }

        aUser.setUsername(resolvedUsername);
        aUser.setPassword(resolvedPassword);
    }

    private void authenticateUser(final BackEndFunctionalTestScenarioContext scenarioContext, String prefix,
            UserData user) {
        String logPrefix = scenarioContext.getTestData().get_guid_() + ": " + prefix + " [" + user.getUsername() + "] ";
        String preferredTokenProviderClientId = scenarioContext.getTestData().getUserTokenClientId();

        AuthenticationRetryConfiguration authenticationRetryConfiguration =
                BeftaMain.getConfig().getAuthenticationRetryConfiguration();

        scenario.log("User service: " + preferredTokenProviderClientId + ".");
        if (authenticationRetryConfiguration.isRetryDisabled()) {
            try {
                BeftaMain.getAdapter().authenticate(user, preferredTokenProviderClientId);
                BeftaUtils.defaultLog(logPrefix + "authenticated.");
            } catch (Exception ex) {
                throw new FunctionalTestException(logPrefix + "could not authenticate.", ex);
            }
        } else {
            BeftaUtils.defaultLog("Authentication retry enabled");
            AtomicInteger counter = new AtomicInteger(0);
            logger.info("Attempting to authenticate {}. Retry no = {}.", user.getUsername(), counter.incrementAndGet());
            authenticateUserWithRetry(authenticationRetryConfiguration, user, preferredTokenProviderClientId);
        }
    }

    private void authenticateUserWithRetry(AuthenticationRetryConfiguration config,
                                           UserData user,
                                           String preferredTokenProviderClientId) {
        Callable<Boolean> callable = () -> {
            BeftaMain.getAdapter().authenticate(user, preferredTokenProviderClientId);
            return true;
        };

        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IOException.class)
                .retryIfExceptionOfType(ConnectException.class)
                .retryIfRuntimeException()
                .withStopStrategy(StopStrategies.stopAfterAttempt(config.getRetryAttempts()))
                .withWaitStrategy(WaitStrategies.fibonacciWait(config.getRetryMultiplierTimeinMilliseconds(),
                        config.getRetryMaxTimeInSeconds(),
                        TimeUnit.SECONDS))
                .build();
        try {
            retryer.call(callable);
        } catch (RetryException retryException) {
            throw new FunctionalTestException(
                    String.format("Retry Exception when authenticating user %s", user.getUsername()), retryException);
        } catch (ExecutionException executionException) {
            throw new FunctionalTestException(
                    String.format("Execution Exception when authenticating user %s", user.getUsername()),
                    executionException);
        }
    }

    @Override
    @When("a wait time of [{}] seconds [{}]")
    @When("a wait time is allowed for [{}] seconds [{}]")
    public void suspendExecutionOnPurposeForAGivenNumberOfSeconds(String waitTime, String specAboutWaitTime)
            throws InterruptedException {
        try {
            DecimalFormat df = new DecimalFormat("#.##");
            TimeUnit.MILLISECONDS.sleep((long) (Double.valueOf(df.format(Double.parseDouble(waitTime))) * 1000));
        } catch (NumberFormatException ex) {
            throw new FunctionalTestException("Wait time provided is not a valid number: " + waitTime, ex);
        }
    }
}
