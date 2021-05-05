package uk.gov.hmcts.befta.featuretoggle.launchdarkly;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import uk.gov.hmcts.befta.exception.FeatureToggleCheckFailureException;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleInfo;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleService;

import java.util.ArrayList;
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

        scenario.log(getFeatureFlagsWithDefaultValue(scenario).toString());

        Map<String, Boolean> mapFeatureWithExpectedValues = getFeatureFlagsWithDefaultValue(scenario);
        mapFeatureWithExpectedValues.forEach((flagName, expectedValue) -> {
            boolean isLDFlagEnabled = ldClient.boolVariation(flagName, user, false);
            status.add(flagName, isLDFlagEnabled == expectedValue);
        });

        scenario.log(getDatabaseFlagsWithDefaultValue(scenario).toString());
        List<Triple<String, String, Boolean>> tripleList = getDatabaseFlagsWithDefaultValue(scenario);
        tripleList.forEach(triplet -> {
            boolean isDbFlagEnabled = getDbFlagValue(triplet);
            status.add(triplet.getMiddle(), isDbFlagEnabled == triplet.getRight());
        });

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

    private Map<String, Boolean> getFeatureFlagsWithDefaultValue(Scenario scenario) {
        scenario.log("Getting getFeatureFlagsWithDefaultValue ");

        System.out.println(scenario.getSourceTagNames()
                .stream()
                .filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG_WITH_EXPECTED_VALUE))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")"))));

        System.out.println(scenario.getSourceTagNames()
                .stream()
                .filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG_WITH_EXPECTED_VALUE))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")")))
                .map(flag -> flag.split(",")));

        return scenario.getSourceTagNames()
                .stream()
                .filter(tag -> tag.contains(LAUNCH_DARKLY_FLAG_WITH_EXPECTED_VALUE))
                .map(tag -> tag.substring(tag.indexOf("(") + 1, tag.indexOf(")")))
                .map(flag -> flag.split(","))
                .collect(Collectors.toMap(tag -> tag[0].trim(), tag -> Boolean.getBoolean(tag[1].trim())));
    }

    private List<Triple<String, String, Boolean>> getDatabaseFlagsWithDefaultValue(Scenario scenario) {

        List<Triple<String, String, Boolean>> tripleList = new ArrayList<>();
        scenario.getSourceTagNames().forEach(tagname -> {
           if(tagname.contains(DATABASE_FLAG_WITH_EXPECTED_VALUE)) {
               String[] array = tagname.substring(tagname.indexOf("(") + 1, tagname.indexOf(")")).split(",");

               tripleList.add(new ImmutableTriple(array[0].trim(), array[1].trim(),
                       Boolean.valueOf(array[2].trim())));
           }
        });
        return tripleList;
    }

    private boolean getDbFlagValue(Triple dbTriple) {
        ///fetchFlagStatus
        RestAssured.useRelaxedHTTPSValidation();
        Response response = RestAssured.get(dbTriple.getLeft().toString());
        boolean bool = response.getBody().as(Boolean.class);
        return bool;
    }
}
