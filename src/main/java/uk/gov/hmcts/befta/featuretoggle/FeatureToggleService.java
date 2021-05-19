package uk.gov.hmcts.befta.featuretoggle;

import io.cucumber.java.Scenario;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.featuretoggle.launchdarkly.LaunchDarklyFeatureToggleService;

public interface FeatureToggleService<T,R> {

    FeatureToggleService DEFAULT_INSTANCE = LaunchDarklyFeatureToggleService.INSTANCE;

    R getToggleStatusFor(T toggleable) throws FeatureToggleCheckFailureException;

}
