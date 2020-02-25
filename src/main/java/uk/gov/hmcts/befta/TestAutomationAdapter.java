package uk.gov.hmcts.befta;

import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

public interface TestAutomationAdapter {

    String getNewS2SToken();

    void authenticate(UserData user);

    void loadTestDataIfNecessary();

    Object calculateCustomValue(BackEndFunctionalTestScenarioContext scenarioContext, Object key);
}
