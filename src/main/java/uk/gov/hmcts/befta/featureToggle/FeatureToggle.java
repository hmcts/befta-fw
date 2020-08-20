package uk.gov.hmcts.befta.featureToggle;

import io.cucumber.java.Scenario;

public interface FeatureToggle {
    void evaluateFlag(Scenario scenario);
}
