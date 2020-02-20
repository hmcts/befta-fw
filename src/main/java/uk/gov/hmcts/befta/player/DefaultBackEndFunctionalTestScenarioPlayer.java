package uk.gov.hmcts.befta.player;

import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.aspectj.util.FileUtil;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.TestAutomationConfig;
import uk.gov.hmcts.befta.TestAutomationConfig.ResponseHeaderCheckPolicy;
import uk.gov.hmcts.befta.data.FileInBody;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.RequestData;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.exception.UnconfirmedApiCallException;
import uk.gov.hmcts.befta.exception.UnconfirmedDataSpecException;
import uk.gov.hmcts.befta.util.BeftaUtils;
import uk.gov.hmcts.befta.util.DynamicValueInjector;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;
import uk.gov.hmcts.befta.util.JsonUtils;
import uk.gov.hmcts.befta.util.MapVerificationResult;
import uk.gov.hmcts.befta.util.MapVerifier;

public class DefaultBackEndFunctionalTestScenarioPlayer implements BackEndFunctionalTestAutomationDSL {

    private Logger logger = LoggerFactory.getLogger(DefaultBackEndFunctionalTestScenarioPlayer.class);

    private final BackEndFunctionalTestScenarioContext scenarioContext;
    private Scenario scenario;

    public DefaultBackEndFunctionalTestScenarioPlayer() {
        RestAssured.baseURI = TestAutomationConfig.INSTANCE.getTestUrl();
        RestAssured.useRelaxedHTTPSValidation();
        scenarioContext = new BackEndFunctionalTestScenarioContext();
    }

    @Before()
    public void prepare(Scenario scenario) {
        this.scenario = scenario;
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

        performAndVerifyTheExpectedResponseForAnApiCall("to create a token for case creation",
                "Standard_Token_Creation_Data_For_Case_Creation");
        performAndVerifyTheExpectedResponseForAnApiCall("to create a full case", caseCreationDataId);
    }

    @Override
    @Given("a user with [{}]")
    public void verifyThatThereIsAUserInTheContextWithAParticularSpecification(String specificationAboutAUser) {
        final int userIndex = scenarioContext.getAndIncrementUserCountSpecifiedSoFar();
        boolean doesTestDataMeetSpec = scenarioContext.getTestData().meetsSpec(specificationAboutAUser);

        if (!doesTestDataMeetSpec) {
            throw new UnconfirmedDataSpecException(specificationAboutAUser);
        }

        if (userIndex < scenarioContext.getTestData().getUsers().size()) {
            UserData userData = (UserData) scenarioContext.getTestData().getUsers().values().toArray()[userIndex];
            verifyTheUserBeingSpecifiedInTheContext(scenarioContext, userData, userIndex);
        } else {
            logger.info("The user [{}] will not be verified with authentication as it is not listed in test data.",
                    specificationAboutAUser);
        }
    }

    @Override
    @When("a request is prepared with appropriate values")
    public void prepareARequestWithAppropriateValues() throws IOException {
        prepareARequestWithAppropriateValues(this.scenarioContext);
    }

    private void prepareARequestWithAppropriateValues(BackEndFunctionalTestScenarioContext scenarioContext)
            throws IOException {
        HttpTestData testData = scenarioContext.getTestData();

        new DynamicValueInjector(BeftaMain.getAdapter(), testData, scenarioContext).injectDataFromContext();

        RequestSpecification raRequest = buildRestAssuredRequestWith(testData.getRequest());

        scenarioContext.setTheRequest(raRequest);
        scenario.write("Request prepared with the following variables: "
                + JsonUtils.getPrettyJsonFromObject(scenarioContext.getTestData().getRequest()));
    }

    private RequestSpecification buildRestAssuredRequestWith(RequestData requestData) throws IOException {
        RequestSpecification aRequest = RestAssured.given();

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
        if (requestData.getBody().containsKey("__fileInBody__")) {
            @SuppressWarnings("unchecked")
            Map<String, String> fileInfo = (Map<String, String>) requestData.getBody().get("__fileInBody__");
            String fullPath = fileInfo.get("fullPath");
            File fileToUpload = BeftaUtils.getClassPathResourceIntoTemporaryFile(fullPath);
            try {
                request.multiPart(fileToUpload);
            } catch (Exception e) {
                throw new FunctionalTestException("Unable to upload the file: " + fullPath, e);
            } finally {
                fileToUpload.delete();
            }

        } else {
            Object bodyToSend = requestData.getBody();
            if (requestData.getBody().containsKey("arrayInMap"))
                bodyToSend = requestData.getBody().get("arrayInMap");
            request.body(new ObjectMapper().writeValueAsBytes(bodyToSend));
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

    private void submitTheRequestToCallAnOperationOfAProduct(BackEndFunctionalTestScenarioContext scenarioContext,
            String operationName, String productName) throws IOException {
        boolean isCorrectOperation = scenarioContext.getTestData().meetsOperationOfProduct(productName, operationName);
        if (!isCorrectOperation) {
            throw new UnconfirmedApiCallException(productName, operationName);
        }

        RequestSpecification theRequest = scenarioContext.getTheRequest();
        String uri = scenarioContext.getTestData().getUri();
        String methodAsString = scenarioContext.getTestData().getMethod();
        Method method;
        try {
            method = Method.valueOf(methodAsString.toUpperCase());
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Method '" + methodAsString + "' in test data file not recognised";
            throw new FunctionalTestException(errorMessage);
        }

        QueryableRequestSpecification queryableRequest = SpecificationQuerier.query(theRequest);
        scenario.write("Calling " + queryableRequest.getMethod() + " " + queryableRequest.getURI());
        Response response = theRequest.request(method, uri);

        ResponseData responseData = convertRestAssuredResponseToBeftaResponse(response);
        scenarioContext.getTestData().setActualResponse(responseData);
        scenarioContext.setTheResponse(responseData);
    }

    @SuppressWarnings("unchecked")
    private ResponseData convertRestAssuredResponseToBeftaResponse(Response response) throws IOException {
        Map<String, Object> responseHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        response.getHeaders().forEach(header -> responseHeaders.put(header.getName(), header.getValue()));
        ResponseData responseData = new ResponseData();
        responseData.setResponseCode(response.getStatusCode());
        String reasonPhrase = EnglishReasonPhraseCatalog.INSTANCE.getReason(response.getStatusCode(), null);
        responseData.setResponseMessage(reasonPhrase);
        responseData.setHeaders(responseHeaders);
        
        String jsonTextForBody = null;
        if (response.contentType() != null && response.contentType().toLowerCase().contains("application/json")) {
            if (!response.getBody().asString().isEmpty()) {
                jsonTextForBody = response.getBody().asString();
                jsonTextForBody = wrapInMapIfNecessary(jsonTextForBody);
            }            
        }
        else {
            jsonTextForBody = wrapInMapIfNecessary(response.getBody().asInputStream());
        }
        if (jsonTextForBody != null) {
            responseData.setBody(JsonUtils.readObjectFromJsonText(jsonTextForBody, Map.class));
        }

        return responseData;
    }

    private String wrapInMapIfNecessary(InputStream inputStream) throws IOException {
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

    private String wrapInMapIfNecessary(String apiResponse) {
        if (apiResponse.startsWith("[") && apiResponse.endsWith("]")) {
            apiResponse = "{\"arrayInMap\":" + apiResponse + "}";
        }
        return apiResponse;
    }

    @Override
    @Then("a positive response is received")
    public void verifyThatAPositiveResponseWasReceived() {
        int responseCode = scenarioContext.getTheResponse().getResponseCode();
        scenario.write("Response code: " + responseCode);
        boolean responseCodePositive = responseCode / 100 == 2;
        Assert.assertTrue("Response code '" + responseCode + "' is not a success code.", responseCodePositive);
    }

    @Override
    @Then("a negative response is received")
    public void verifyThatANegativeResponseWasReceived() {
        int responseCode = scenarioContext.getTheResponse().getResponseCode();
        scenario.write("Response code: " + responseCode);
        boolean responseCodePositive = responseCode / 100 == 2;
        Assert.assertFalse("Response code '" + responseCode + "' is unexpectedly a success code.",
                responseCodePositive);
    }

    @Override
    @Then("the response has all the details as expected")
    @Then("the response has all other details as expected")
    public void verifyThatTheResponseHasAllTheDetailsAsExpected() throws IOException {
        verifyThatTheResponseHasAllTheDetailsAsExpected(this.scenarioContext);
    }

    private void verifyThatTheResponseHasAllTheDetailsAsExpected(BackEndFunctionalTestScenarioContext scenarioContext)
            throws IOException {
        ResponseData expectedResponse = scenarioContext.getTestData().getExpectedResponse();
        ResponseData actualResponse = scenarioContext.getTheResponse();

        List<String> issuesInResponseHeaders = null, issuesInResponseBody = null;
        String issueWithResponseCode = null;

        if (actualResponse.getResponseCode() != expectedResponse.getResponseCode()) {
            issueWithResponseCode = "Response code mismatch, expected: " + expectedResponse.getResponseCode()
                    + ", actual: " + actualResponse.getResponseCode();
        }

        MapVerificationResult headerVerification = new MapVerifier("actualResponse.headers", 1, false)
                .verifyMap(expectedResponse.getHeaders(), actualResponse.getHeaders());
        if (!headerVerification.isVerified()) {
            issuesInResponseHeaders = headerVerification.getAllIssues();
        }

        MapVerificationResult bodyVerification = new MapVerifier("actualResponse.body", 20)
                .verifyMap(expectedResponse.getBody(), actualResponse.getBody());
        if (!bodyVerification.isVerified()) {
            issuesInResponseBody = bodyVerification.getAllIssues();
        }

        scenario.write("Response:\n" + JsonUtils.getPrettyJsonFromObject(scenarioContext.getTheResponse()));

        processAnyIssuesInResponse(issueWithResponseCode, issuesInResponseHeaders, issuesInResponseBody);
    }

    private void processAnyIssuesInResponse(String issueWithResponseCode, List<String> issuesInResponseHeaders,
            List<String> issuesInResponseBody) {
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
        BackEndFunctionalTestScenarioContext subcontext = new BackEndFunctionalTestScenarioContext();
        subcontext.initializeTestDataFor(testDataId);
        this.scenarioContext.addChildContext(subcontext);
        verifyAllUsersInTheConext(subcontext);
        prepareARequestWithAppropriateValues(subcontext);
        verifyTheRequestInTheContextWithAParticularSpecification(subcontext, testDataSpec);
        submitTheRequestToCallAnOperationOfAProduct(subcontext, subcontext.getTestData().getOperationName(),
                subcontext.getTestData().getProductName());
        verifyThatTheResponseHasAllTheDetailsAsExpected(subcontext);
    }

    private void verifyAllUsersInTheConext(BackEndFunctionalTestScenarioContext scenarioContext) {
        scenarioContext.getTestData().getUsers()
                .forEach((key, userData) -> verifyTheUserBeingSpecifiedInTheContext(scenarioContext, userData,
                        scenarioContext.getAndIncrementUserCountSpecifiedSoFar()));
    }

    private void verifyTheUserBeingSpecifiedInTheContext(final BackEndFunctionalTestScenarioContext scenarioContext,
            final UserData userBeingSpecified, int userIndex) {
        String prefix = userIndex == 0 ? "users.invokingUser" : "users[" + userIndex + "]";
        resolveUserData(prefix, userBeingSpecified);
        scenario.write("prefix: " + userBeingSpecified.getUsername());
        authenticateUser(prefix, userBeingSpecified);
        if (userIndex == 0) {
            scenarioContext.setTheInvokingUser(userBeingSpecified);
        }
    }

    private void resolveUserData(String prefix, UserData aUser) {
        String resolvedUsername = EnvironmentVariableUtils.resolvePossibleVariable(aUser.getUsername());
        if (resolvedUsername.equals(aUser.getUsername())) {
            logger.info(scenarioContext.getCurrentScenarioTag() + ": Expected environment variable declaration "
                    + "for " + prefix + ".username but found '" + resolvedUsername + "', which may cause issues "
                    + "in higher environments");
        }

        String resolvedPassword = EnvironmentVariableUtils.resolvePossibleVariable(aUser.getPassword());
        if (resolvedPassword.equals(aUser.getPassword())) {
            logger.info(scenarioContext.getCurrentScenarioTag() + ": Expected environment variable declaration "
                    + "for " + prefix + ".password but found '" + resolvedPassword + "', which may cause issues "
                    + "in higher environments");
        }

        aUser.setUsername(resolvedUsername);
        aUser.setPassword(resolvedPassword);
    }

    private void authenticateUser(String prefix, UserData user) {
        String logPrefix = scenarioContext.getCurrentScenarioTag() + ": " + prefix + " [" + user.getUsername() + "]["
                + user.getPassword() + "] ";
        try {
            BeftaMain.getAdapter().authenticate(user);
            logger.info(logPrefix + "authenticated.");
        } catch (Exception ex) {
            logger.info(logPrefix + "could not authenticate.");
        }
    }
}
