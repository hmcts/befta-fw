package uk.gov.hmcts.befta;

import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

public interface TestAutomationAdapter {

    String getNewS2SToken();

    String getNewS2SToken(String clientId);

    void authenticate(UserData user, String preferredOauth2ClientId);

    void loadTestDataIfNecessary();

    Object calculateCustomValue(BackEndFunctionalTestScenarioContext scenarioContext, Object key);
}
