package uk.gov.hmcts.befta;

import uk.gov.hmcts.befta.data.UserData;

public interface TestAutomationAdapter {

    String getNewS2SToken();

    String getNewS2SToken(String clientId);

    void authenticate(UserData user);

    void loadTestDataIfNecessary();
}
