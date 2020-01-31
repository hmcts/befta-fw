package uk.gov.hmcts.befta.player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.cucumber.java.Scenario;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.HttpTestDataSource;
import uk.gov.hmcts.befta.data.JsonStoreHttpTestDataSource;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;

public class BackEndFunctionalTestScenarioContext {

    private static final String[] TEST_DATA_RESOURCE_PACKAGES = { "features" };
    private static final HttpTestDataSource DATA_SOURCE = new JsonStoreHttpTestDataSource(TEST_DATA_RESOURCE_PACKAGES);

    private Scenario scenario;

    @Getter
    protected HttpTestData testData;

    @Getter @Setter
    private UserData theInvokingUser;

    @Getter @Setter
    private RequestSpecification theRequest;

    @Getter @Setter
    private ResponseData theResponse;

    @Getter @Setter
    private BackEndFunctionalTestScenarioContext parentContext;

    @Getter
    private Map<String, BackEndFunctionalTestScenarioContext> childContexts = new HashMap<>();


    public void addChildContext(BackEndFunctionalTestScenarioContext childContext) {
        childContext.setParentContext(this);
        childContexts.put(childContext.getTestData().get_guid_(), childContext);
    }

    public void initializeTestDataFor(Scenario scenario) {
        this.scenario = scenario;
        String scenarioTag = getCurrentScenarioTag();
        initializeTestDataFor(scenarioTag);
    }

    public void initializeTestDataFor(String testDataId) {
        testData = DATA_SOURCE.getDataForTestCall(testDataId);
    }

    public String getCurrentScenarioTag() {
        return scenario.getSourceTagNames().stream()
            .filter(tag -> tag.startsWith("@S-"))
            .map(tag -> tag.substring(1))
            .collect(Collectors.joining(","));
    }
}
