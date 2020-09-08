package uk.gov.hmcts.befta.factory;

import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;

public class BeftaScenarioContextFactory {

    private BeftaScenarioContextFactory() {
    }

    public static BackEndFunctionalTestScenarioContext createBeftaScenarioContext() {
        return new BackEndFunctionalTestScenarioContext();
    }

}
