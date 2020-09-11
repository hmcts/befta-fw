package uk.gov.hmcts.befta.launchdarkly;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.befta.featureToggle.FeatureToggle;
import uk.gov.hmcts.befta.player.DefaultBackEndFunctionalTestScenarioPlayer;
import uk.gov.hmcts.befta.util.BeftaUtils;

import java.util.Optional;

@Slf4j
public class LaunchDarklyFeatureToggleService implements FeatureToggle {

    private final Logger logger = LoggerFactory.getLogger(LaunchDarklyFeatureToggleService.class);

    public static final LaunchDarklyFeatureToggleService INSTANCE =
            new LaunchDarklyFeatureToggleService();

    public static final String BEFTA = "befta";
    public static final String USER = "user";
    public static final String SERVICENAME = "servicename";

    private static final String LAUNCH_DARKLY_FLAG = "FeatureToggle";
    private final LDClient ldClient = LaunchDarklyConfig.getLdInstance();

    @Override
    public void evaluateFlag(Scenario scenario) {

        logger.info("Inside evaluateFlag");
        scenario.log("Inside evaluateFlag");
        Optional<String> flagName = scenario.getSourceTagNames().stream()
                .filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")")))
                .findFirst();

        logger.info("LDClient is: " + ldClient);
        scenario.log("LDClient is: " + ldClient);

        logger.info("flagname is: " + flagName.orElse("Flag name is empty"));
        scenario.log("flagname is: " + flagName.orElse("Flag name is empty"));
        if (ldClient != null && flagName.isPresent()) {
            logger.info("getLDMicroserviceName is: " + LaunchDarklyConfig.getLDMicroserviceName());
            scenario.log("getLDMicroserviceName is: " + LaunchDarklyConfig.getLDMicroserviceName());

            if (LaunchDarklyConfig.getLDMicroserviceName() == null) {
                BeftaUtils.skipScenario(scenario, ("The Scenario is being skipped as MICROSERVICE_NAME variable is not configured"));

            }
            logger.info("getEnvironmentName is: " + LaunchDarklyConfig.getEnvironmentName());
            scenario.log("getEnvironmentName is: " + LaunchDarklyConfig.getEnvironmentName());

            if (LaunchDarklyConfig.getEnvironmentName() == null) {
                BeftaUtils.skipScenario(scenario, ("The Scenario is being skipped as LAUNCH_DARKLY_ENV is not configured"));
            }

            logger.info("Calling LD : " + LaunchDarklyConfig.getLDMicroserviceName() + "  " + LaunchDarklyConfig.getEnvironmentName());
            scenario.log("Calling LD : " + LaunchDarklyConfig.getLDMicroserviceName() + "  " + LaunchDarklyConfig.getEnvironmentName());
            LDUser user = new LDUser.Builder(LaunchDarklyConfig.getEnvironmentName())
                    .firstName(BEFTA)
                    .lastName(USER)
                    .custom(SERVICENAME, LaunchDarklyConfig.getLDMicroserviceName())
                    .build();

            boolean isLDFlagEnabled = ldClient.boolVariation(flagName.get(), user, false);
            logger.info("Inside isLDFlagEnabled " + isLDFlagEnabled);
            scenario.log("Inside isLDFlagEnabled" + isLDFlagEnabled);

            if (!isLDFlagEnabled) {
                Optional<String> scenarioName = scenario.getSourceTagNames().stream()
                        .filter(tag -> tag.contains("@S-"))
                        .map(tag -> tag.substring(1))
                        .findFirst();

                BeftaUtils.skipScenario(scenario, String.format("The Scenario %s is being skipped as LD flag is disabled",
                        scenarioName.orElse(StringUtils.EMPTY)));
            }
        }
    }
}

