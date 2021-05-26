package uk.gov.hmcts.befta.featuretoggle.launchdarkly;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import io.cucumber.java.Scenario;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LaunchDarklyFeatureToggleService implements FeatureToggleService<String, Boolean> {

    public static final LaunchDarklyFeatureToggleService INSTANCE = new LaunchDarklyFeatureToggleService();

    private static final String BEFTA = "befta";
    private static final String USER = "user";
    private static final String SERVICENAME = "servicename";

    private static final LDUser ldUser = new LDUser.Builder(LaunchDarklyConfig.getEnvironmentName()).firstName(BEFTA)
            .lastName(USER).custom(SERVICENAME, LaunchDarklyConfig.getLDMicroserviceName()).build();

    private static final String LAUNCH_DARKLY_FLAG_WITH_EXPECTED_VALUE = "FeatureFlagWithExpectedValue";
    private static final String EXTERNAL_FLAG_WITH_EXPECTED_VALUE = "ExternalFlagWithExpectedValue";

    LDClient ldClient = LaunchDarklyConfig.getLdInstance();

    @Override
    public Boolean getToggleStatusFor(String flagId) {
        if (ldClient == null) {
            return Boolean.FALSE;
        }
        checkLaunchDarklyConfig();
        return ldClient.boolVariation(flagId, ldUser, false);

    }

    private void checkLaunchDarklyConfig() {
        if (LaunchDarklyConfig.getLDMicroserviceName() == null) {
            throw new FeatureToggleCheckFailureException(
                    "The Scenario is being skipped as MICROSERVICE_NAME variable is not configured");
        }
        if (LaunchDarklyConfig.getEnvironmentName() == null) {
            throw new FeatureToggleCheckFailureException(
                    "The Scenario is being skipped as LAUNCH_DARKLY_ENV is not configured");
        }
    }

 /*   private List<String> getFeatureFlagsOn(Scenario scenario) {
        return scenario.getSourceTagNames().stream().filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")"))).collect(Collectors.toList());
    }*/

    private Map<String, Boolean> getFeatureFlagsWithExpectedValue(Scenario scenario) {

        return scenario.getSourceTagNames()
                .stream()
                .filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG_WITH_EXPECTED_VALUE))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")")))
                .map(str -> str.split(","))
                .collect(Collectors.toMap(str -> str[0], str -> Boolean.parseBoolean(str[1])));
    }

    private Map<String, Boolean> getExternalFlagsWithDefaultValue(Scenario scenario) {
        Map<String, Boolean> externalFlagMap = new HashMap<>();
        scenario.getSourceTagNames().forEach(tagname -> {
            if (tagname.contains(EXTERNAL_FLAG_WITH_EXPECTED_VALUE)) {
                String[] array = tagname.substring(tagname.indexOf("(") + 1, tagname.indexOf(")")).split(",");
                externalFlagMap.put(array[0], Boolean.valueOf(array[1]));
            }
        });

        return externalFlagMap;
    }

}
