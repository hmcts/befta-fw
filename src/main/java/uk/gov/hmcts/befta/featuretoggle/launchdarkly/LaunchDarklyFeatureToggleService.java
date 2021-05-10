package uk.gov.hmcts.befta.featuretoggle.launchdarkly;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import uk.gov.hmcts.befta.TestAutomationConfig;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleService;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LaunchDarklyFeatureToggleService implements FeatureToggleService {

    public static final LaunchDarklyFeatureToggleService INSTANCE = new LaunchDarklyFeatureToggleService();

    private static final String BEFTA = "befta";
    private static final String USER = "user";
    private static final String SERVICENAME = "servicename";

    private static final LDUser user = new LDUser.Builder(LaunchDarklyConfig.getEnvironmentName()).firstName(BEFTA)
            .lastName(USER).custom(SERVICENAME, LaunchDarklyConfig.getLDMicroserviceName()).build();

    private static final String LAUNCH_DARKLY_FLAG = "FeatureToggle";
    private static final String LAUNCH_DARKLY_FLAG_WITH_EXPECTED_VALUE = "FeatureFlagWithExpectedValue";
    private static final String DATABASE_FLAG_WITH_EXPECTED_VALUE = "DatabaseFlagWithExpectedValue";

    private LDClient ldClient = LaunchDarklyConfig.getLdInstance();

    @Override
    public FeatureToggleInfo getToggleStatusFor(Scenario scenario) {
        FeatureToggleInfo status = new FeatureToggleInfo();
        try {
            if (ldClient == null)
                return null;

            List<String> flagNames = getFeatureFlagsOn(scenario);
            Map<String, Boolean> mapFeatureWithExpectedValues = getFeatureFlagsWithExpectedValue(scenario);
            Map<String, Boolean> dbFlagMap = getDatabaseFlagsWithDefaultValue(scenario);

            if (flagNames.isEmpty() && mapFeatureWithExpectedValues.isEmpty() && dbFlagMap.isEmpty()) {
                return status;
            }

            checkLaunchDarklyConfig();

            for (String flag : flagNames) {
                boolean isLDFlagEnabled = ldClient.boolVariation(flag, user, false);
                status.add(flag, isLDFlagEnabled);
            }

            mapFeatureWithExpectedValues.forEach((flagName, expectedValue) -> {
                boolean isLDFlagEnabled = ldClient.boolVariation(flagName, user, false);
                status.add(flagName, isLDFlagEnabled == expectedValue);
            });

            dbFlagMap.forEach((dbFlagName, expectedValue) -> {
                boolean dbFlagValue = getDbFlagValue(dbFlagName);
                scenario.log(String.format("isDbFlagEnabled: %s : %s", dbFlagName, dbFlagValue));
                status.add(dbFlagName, dbFlagValue == expectedValue);
            });

            scenario.log("Enabled Flags  :" + status.getEnabledFeatureFlags());
            scenario.log("Disabled Flags  :" + status.getDisabledFeatureFlags());
        }
        catch (Exception e) {
            scenario.log("Exception is");
            e.printStackTrace();
        }
        return status;
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

    private List<String> getFeatureFlagsOn(Scenario scenario) {
        return scenario.getSourceTagNames().stream().filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")"))).collect(Collectors.toList());
    }

    private Map<String, Boolean> getFeatureFlagsWithExpectedValue(Scenario scenario) {

        return scenario.getSourceTagNames()
                .stream()
                .filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG_WITH_EXPECTED_VALUE))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")")))
                .map(str -> str.split(","))
                .collect(Collectors.toMap(str -> str[0], str -> Boolean.parseBoolean(str[1])));
    }

    private Map<String, Boolean> getDatabaseFlagsWithDefaultValue(Scenario scenario) {
        Map<String, Boolean> dbFlagMap = new HashMap<>();
        scenario.getSourceTagNames().forEach(tagname -> {
            if (tagname.contains(DATABASE_FLAG_WITH_EXPECTED_VALUE)) {
                String[] array = tagname.substring(tagname.indexOf("(") + 1, tagname.indexOf(")")).split(",");
                System.out.println(array);
                scenario.log(array.toString());
                dbFlagMap.put(array[0], Boolean.valueOf(array[1]));
            }
        });

        return dbFlagMap;
    }

    private boolean getDbFlagValue(String dbFlag) {
        RestAssured.useRelaxedHTTPSValidation();

        RestAssured.baseURI = TestAutomationConfig.INSTANCE.getTestUrl();

        String path = "/" + EnvironmentVariableUtils.getRequiredVariable("DB_FLAG_QUERY_PATH") + dbFlag;
        Response response = RestAssured.get(path);

        if (response.getStatusCode() == HttpStatus.SC_OK) {
            return response.getBody().as(Boolean.class);
        }
        return false;
    }
}
