package uk.gov.hmcts.befta.launchdarkly;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.AssumptionViolatedException;
import uk.gov.hmcts.befta.featureToggle.FeatureToggle;

import java.util.Optional;

@Slf4j
public class LaunchDarklyFeatureToggleService implements FeatureToggle {
    public static final LaunchDarklyFeatureToggleService INSTANCE =
            new LaunchDarklyFeatureToggleService();

    private final LDClient ldClient = LaunchDarklyConfig.getLdInstance();
    private static final String LAUNCH_DARKLY_FLAG = "FeatureToggle";

    @Override
    public void evaluateFlag(Scenario scenario) {

        Optional<String> flagName = scenario.getSourceTagNames().stream()
                .filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")")))
                .findFirst();

        if (ldClient != null && flagName.isPresent() && StringUtils.isNotEmpty(flagName.get())) {
            LDUser user = new LDUser.Builder(LaunchDarklyConfig.getEnvironmentName())
                    .firstName("befta")
                    .lastName("user")
                    .custom("servicename", LaunchDarklyConfig.getLDMicroserviceName())
                    .build();

            boolean isFlagTrue = ldClient.boolVariation(flagName.get(), user, false);

            if (!isFlagTrue) {
                Optional<String> scenarioName = scenario.getSourceTagNames().stream()
                        .filter(tag -> tag.contains("@S-"))
                        .map(tag -> tag.substring(1))
                        .findFirst();

                throw new AssumptionViolatedException(String.format("The scenario %s is not enabled.",
                        scenarioName.orElse(StringUtils.EMPTY)));
            }
        }
    }
}

