package uk.gov.hmcts.befta.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import io.cucumber.java.Scenario;
import io.restassured.specification.RequestSpecification;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.factory.DynamicValueInjectorFactory;
import uk.gov.hmcts.befta.factory.HttpTestDataSourceFactory;
import uk.gov.hmcts.befta.util.BeftaUtils;
import uk.gov.hmcts.befta.util.DynamicValueInjector;

public class BackEndFunctionalTestScenarioContext {

    private static final String[] TEST_DATA_RESOURCE_PACKAGES = { "features" };

    static final HttpTestDataSource DATA_SOURCE = HttpTestDataSourceFactory.createHttpTestDataSource(TEST_DATA_RESOURCE_PACKAGES);

    private Scenario scenario;

    protected HttpTestData testData;

    private RequestSpecification theRequest;

    private ResponseData theResponse;

    private Function<Object, Object> customValues = (valueKey -> calculateCustomValue(valueKey));

    private BackEndFunctionalTestScenarioContext parentContext;

    private Map<String, BackEndFunctionalTestScenarioContext> childContexts = new HashMap<>();

    protected String contextId = null;

    private int userCountAuthenticatedSoFar = 0;

    private DynamicValueInjector dynamicValueInjector;

    public synchronized void addChildContext(BackEndFunctionalTestScenarioContext childContext) {
        addChildContext(childContext.getTestDataId(), childContext);
    }

    public synchronized void addChildContext(String contextId, BackEndFunctionalTestScenarioContext childContext) {
        childContext.setParentContext(this);
        childContext.setContextId(contextId);
        this.childContexts.put(contextId, childContext);
    }

    public synchronized void initializeTestDataFor(Scenario scenario) {
        this.scenario = scenario;
        String scenarioTag = getCurrentScenarioTag();
        initializeTestDataFor(scenarioTag);
    }

    public synchronized void  initializeTestDataFor(String testDataId) {
        HttpTestData original = DATA_SOURCE.getDataForTestCall(testDataId);
        if (original == null) {
            throw new FunctionalTestException("No test data found with ID [" + testDataId + "].");
        }
        testData = new HttpTestData(original);
        dynamicValueInjector = DynamicValueInjectorFactory.create(BeftaMain.getAdapter(), testData, this);
    }


    synchronized void injectDataFromContextBeforeApiCall() {
        dynamicValueInjector.injectDataFromContextBeforeApiCall();
    }

    synchronized void injectDataFromContextAfterApiCall() {
        dynamicValueInjector.injectDataFromContextAfterApiCall();
    }

    public synchronized String getCurrentScenarioTag() {
        return BeftaUtils.getScenarioTag(scenario);
    }

    public synchronized String getContextId() {
        return contextId == null ? getTestDataId() : contextId;
    }

    synchronized void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public synchronized UserData getTheInvokingUser() {
        return testData.getInvokingUser();
    }

    public synchronized void setTheInvokingUser(UserData theInvokingUser) {
        testData.setInvokingUser(theInvokingUser);
    }

    protected synchronized Object calculateCustomValue(Object key) {
        return BeftaMain.getAdapter().calculateCustomValue(this, key);
    }

    public synchronized Map<String, BackEndFunctionalTestScenarioContext> getSiblingContexts() {
        if (parentContext == null)
            return null;
        return getParentContext().getChildContexts();
    }

    private String getTestDataId() {
        return testData == null ? "" : testData.get_guid_();
    }

    public synchronized Entry<String, UserData> getNextUserToAuthenticate() {
        return testData.getUserEntryAt(userCountAuthenticatedSoFar++);
    }

    public synchronized BackEndFunctionalTestScenarioContext getParentContext() {
        return parentContext;
    }

    public synchronized void setParentContext(BackEndFunctionalTestScenarioContext parentContext) {
        this.parentContext = parentContext;
    }

    public synchronized Map<String, BackEndFunctionalTestScenarioContext> getChildContexts() {
        return childContexts;
    }

    public synchronized HttpTestData getTestData() {
        return testData;
    }

    public synchronized RequestSpecification getTheRequest() {
        return theRequest;
    }

    public synchronized void setTheRequest(RequestSpecification theRequest) {
        this.theRequest = theRequest;
    }

    public synchronized ResponseData getTheResponse() {
        return theResponse;
    }

    public synchronized void setTheResponse(ResponseData theResponse) {
        this.theResponse = theResponse;
    }

    public synchronized Function<Object, Object> getCustomValues() {
        return customValues;
    }

}

