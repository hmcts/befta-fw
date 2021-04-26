package uk.gov.hmcts.befta;

import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

import java.util.concurrent.ExecutionException;

public interface TestAutomationAdapter {

    public static final String EXECUTION_INFO_FILE = "./befta_recent_executions_info.json";

    String getNewS2SToken();

    String getNewS2SToken(String clientId);

    void authenticate(UserData user, String preferredTokenClientId) throws ExecutionException;

    Object calculateCustomValue(BackEndFunctionalTestScenarioContext scenarioContext, Object key);

    BeftaTestDataLoader getDataLoader();
}
