package uk.gov.hmcts.befta;

import io.cucumber.java.Scenario;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.featuretoggle.ScenarioFeatureToggleInfo;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleService;
import uk.gov.hmcts.befta.featuretoggle.launchdarkly.LaunchDarklyFeatureToggleService;

import java.util.Map;

public class DefaultMultiSourceFeatureToggleService implements FeatureToggleService<Scenario, ScenarioFeatureToggleInfo> {

    @Override
    public ScenarioFeatureToggleInfo getToggleStatusFor(Scenario toggleable) throws FeatureToggleCheckFailureException {
        Map<String, Boolean> expectedStatuses = null;
        Map<String, Boolean> actualStatuses = null;
        //prepare Expected Map
        toggleable.getSourceTagNames().forEach(
                // Get LD key
        );
        expectedStatus.forEach((key, value) -> {
            //key = LD:id
            String domain
            String id;
            String expectedStatus;
            Boolean actualStatus;
            //Identify the domain. It should be backwrad compatible
            FeatureToggleService service = getToggleServiceFor(domain);
            actualStatus = service.getToggleStatusFor(id);
            actualStatuses.put(key, actualStatus);
        });
        return new ScenarioFeatureToggleInfo();
    }

    protected FeatureToggleService getToggleServiceFor(String toggleDomain) {
        if (toggleDomain.equalsIgnoreCase("LD") || toggleDomain.equalsIgnoreCase("LaunchDarkly")) {
            return new LaunchDarklyFeatureToggleService();
        } else throw new IllegalArgumentException("Doesn't know FeatureToggleService for Domain " + toggleDomain);
    }
}
