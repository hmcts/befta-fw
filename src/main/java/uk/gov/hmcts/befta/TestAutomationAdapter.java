package uk.gov.hmcts.befta;

import uk.gov.hmcts.befta.data.UserData;

public interface TestAutomationAdapter {

    String getNewS2SToken();

    void authenticate(UserData user);

    void loadTestDataIfNecessary();
}
