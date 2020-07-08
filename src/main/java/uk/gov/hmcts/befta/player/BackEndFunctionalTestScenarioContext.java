package uk.gov.hmcts.befta.player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.cucumber.java.Scenario;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.util.DynamicValueInjector;

public class BackEndFunctionalTestScenarioContext {

    private static final String[] TEST_DATA_RESOURCE_PACKAGES = { "features" };
    static final HttpTestDataSource DATA_SOURCE = new JsonStoreHttpTestDataSource(TEST_DATA_RESOURCE_PACKAGES);

    @Getter
    private Scenario scenario;

    @Getter
    protected HttpTestData testData;

    @Getter @Setter
    private RequestSpecification theRequest;

    @Getter @Setter
    private ResponseData theResponse;

    @Getter
    private Function<Object, Object> customValues = (valueKey -> calculateCustomValue(valueKey));

    @Getter @Setter
    private BackEndFunctionalTestScenarioContext parentContext;

    @Getter
    private Map<String, BackEndFunctionalTestScenarioContext> childContexts = new HashMap<>();

    @Setter
    protected String reference = null;

    private int userCountSpecifiedSoFar = 0;

    private DynamicValueInjector dynamicValueInjector;

    public void addChildContext(BackEndFunctionalTestScenarioContext childContext) {
        addChildContextByReference(childContext.getGuid(), childContext);
    }

    public void addChildContextByReference(String reference, BackEndFunctionalTestScenarioContext childContext) {
        childContext.setParentContext(this);
        childContext.setReference(reference);
        this.childContexts.put(reference, childContext);
    }

    public void initializeTestDataFor(Scenario scenario) {
        this.scenario = scenario;
        String scenarioTag = getCurrentScenarioTag();
        initializeTestDataFor(scenarioTag);
    }

    public void initializeTestDataFor(String testDataId) {
        HttpTestData original = DATA_SOURCE.getDataForTestCall(testDataId);
        if (original == null) {
            throw new FunctionalTestException("No test data found with ID [" + testDataId + "].");
        }
        testData = original == null ? null : new HttpTestData(original);
        dynamicValueInjector = new DynamicValueInjector(BeftaMain.getAdapter(), testData, this);
    }

    void injectDataFromContextBeforeApiCall() {
        dynamicValueInjector.injectDataFromContextBeforeApiCall();
    }

    void injectDataFromContextAfterApiCall() {
        dynamicValueInjector.injectDataFromContextAfterApiCall();
    }

    public String getCurrentScenarioTag() {
        return scenario.getSourceTagNames().stream()
            .filter(tag -> tag.startsWith("@S-"))
            .map(tag -> tag.substring(1))
            .collect(Collectors.joining(","));
    }

    public String getGuid() {
        return testData == null ? "" : testData.get_guid_();
    }

    public String getReference() {
        return reference == null ? getGuid() : reference;
    }

    public UserData getTheInvokingUser() {
        return testData.getInvokingUser();
    }

    public void setTheInvokingUser(UserData theInvokingUser) {
        testData.setInvokingUser(theInvokingUser);
    }

    public int getUserCountSpecifiedSoFar() {
        return userCountSpecifiedSoFar;
    }

    public int getAndIncrementUserCountSpecifiedSoFar() {
        return userCountSpecifiedSoFar++;
    }

    protected Object calculateCustomValue(Object key) {
        return BeftaMain.getAdapter().calculateCustomValue(this, key);
    }

    public Map<String, BackEndFunctionalTestScenarioContext> getSiblingContexts() {
        if (parentContext == null)
            return null;
        return getParentContext().getChildContexts();
    }

}
