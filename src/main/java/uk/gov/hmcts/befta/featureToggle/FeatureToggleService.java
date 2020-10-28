package uk.gov.hmcts.befta.featureToggle;

import io.cucumber.java.Scenario;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.launchdarkly.LaunchDarklyFeatureToggleService;

public interface FeatureToggleService {

    FeatureToggleService DEFAULT_INSTANCE = LaunchDarklyFeatureToggleService.INSTANCE;

    FeatureToggleInfo getToggleStatusFor(Scenario scenario) throws FeatureToggleCheckFailureException;

}
