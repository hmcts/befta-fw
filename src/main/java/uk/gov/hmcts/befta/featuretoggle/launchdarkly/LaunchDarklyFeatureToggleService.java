package uk.gov.hmcts.befta.featuretoggle.launchdarkly;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;

import java.util.List;
import java.util.stream.Collectors;

import io.cucumber.java.Scenario;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleService;

public class LaunchDarklyFeatureToggleService implements FeatureToggleService {

    public static final LaunchDarklyFeatureToggleService INSTANCE = new LaunchDarklyFeatureToggleService();

    private static final String BEFTA = "befta";
    private static final String USER = "user";
    private static final String SERVICENAME = "servicename";

    private static final LDUser user = new LDUser.Builder(LaunchDarklyConfig.getEnvironmentName()).firstName(BEFTA)
            .lastName(USER).custom(SERVICENAME, LaunchDarklyConfig.getLDMicroserviceName()).build();

    private static final String LAUNCH_DARKLY_FLAG = "FeatureToggle";

    private final LDClient ldClient = LaunchDarklyConfig.getLdInstance();

    @Override
    public FeatureToggleInfo getToggleStatusFor(Scenario scenario) {
        if (ldClient == null)
            return null;

        FeatureToggleInfo status = new FeatureToggleInfo();
        List<String> flagNames = getFeatureFlagsOn(scenario);
        if (flagNames.isEmpty())
            return status;

        checkLaunchDarklyConfig(scenario);

        for (String flag : flagNames) {
            boolean isLDFlagEnabled = ldClient.boolVariation(flag, user, false);
            status.add(flag, isLDFlagEnabled);
        }
        return status;
    }

    private void checkLaunchDarklyConfig(Scenario scenario) {
        if (LaunchDarklyConfig.getLDMicroserviceName() == null) {
            throw new FeatureToggleCheckFailureException(
                    "The Scenario is being skipped as MICROSERVICE_NAME variable is not configured");
        }
        if (LaunchDarklyConfig.getEnvironmentName() == null) {
            throw new FeatureToggleCheckFailureException(
                    "The Scenario is being skipped as LAUNCH_DARKLY_ENV is not configured");
        }
    }

    private List<String> getFeatureFlagsOn(Scenario scenario) {
        return scenario.getSourceTagNames().stream().filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")"))).collect(Collectors.toList());
    }
}
