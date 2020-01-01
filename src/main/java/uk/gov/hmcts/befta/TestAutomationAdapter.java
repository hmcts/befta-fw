package uk.gov.hmcts.befta;

import uk.gov.hmcts.befta.data.UserData;

public interface TestAutomationAdapter {

    TestAutomationConfig getAutomationConfig();

    String getNewS2SToken();

    UserData authenticate(UserData user);

    void loadTestDataIfNecessary();
}
