package uk.gov.hmcts.befta;

import static uk.gov.hmcts.befta.util.EnvironmentVariableUtils.getOptionalVariable;

import org.apache.commons.lang3.math.NumberUtils;

import uk.gov.hmcts.befta.auth.UserTokenProviderConfig;
import uk.gov.hmcts.befta.data.CollectionVerificationConfig.Ordering;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;

public class TestAutomationConfig {

    public static final TestAutomationConfig INSTANCE = new TestAutomationConfig();

    private static final double DEFAULT_TEST_DATA_LOAD_SKIP_PERIOD = 15.0;

    private TestAutomationConfig() {
    }

    public String getTestUrl() {
        return EnvironmentVariableUtils.getRequiredVariable("TEST_URL");
    }

    public String getIdamURL() {
        return EnvironmentVariableUtils.getRequiredVariable("IDAM_API_URL_BASE");
    }

    public String getS2SURL() {
        return EnvironmentVariableUtils.getRequiredVariable("S2S_URL_BASE");
    }

    public String getS2SClientId() {
        return EnvironmentVariableUtils.getOptionalVariable("BEFTA_S2S_CLIENT_ID");
    }

    public String getS2SClientSecret() {
        return EnvironmentVariableUtils.getOptionalVariable("BEFTA_S2S_CLIENT_SECRET");
    }

    public String getDefinitionStoreUrl() {
        return EnvironmentVariableUtils.getRequiredVariable("DEFINITION_STORE_URL_BASE");
    }

    public String getImporterAutoTestEmail() {
        return EnvironmentVariableUtils.getRequiredVariable("DEFINITION_IMPORTER_USERNAME");
    }

    public String getImporterAutoTestPassword() {
        return EnvironmentVariableUtils.getRequiredVariable("DEFINITION_IMPORTER_PASSWORD");
    }

    public String getRoleAssignmentEmail() {
        return EnvironmentVariableUtils.getRequiredVariable("ROLE_ASSIGNMENT_USER_EMAIL");
    }

    public String getRoleAssignmentPassword() {
        return EnvironmentVariableUtils.getRequiredVariable("ROLE_ASSIGNMENT_USER_PASSWORD");
    }

    public String getRoleAssignmentHost() {
        return EnvironmentVariableUtils.getRequiredVariable("ROLE_ASSIGNMENT_HOST");
    }

    public String getDataStoreUrl() {
        return EnvironmentVariableUtils.getRequiredVariable("DATA_STORE_URL_BASE");
    }

    public UserTokenProviderConfig getUserTokenProviderConfig() {
        return UserTokenProviderConfig.DEFAULT_INSTANCE;
    }

    public Ordering getDefaultCollectionAssertionMode() {
        Ordering returnValue = Ordering.ORDERED;

        try {
            Ordering defaultCollectionAssertionMode =
                    Ordering.of(EnvironmentVariableUtils.getOptionalVariable("DEFAULT_COLLECTION_ASSERTION_MODE"));
            if (defaultCollectionAssertionMode != null) {
                returnValue = defaultCollectionAssertionMode;
            }
        } catch (IllegalArgumentException iae) {
            returnValue = Ordering.ORDERED;
        }

        return returnValue;
    }

    public static enum ResponseHeaderCheckPolicy {
        IGNORE, JUST_WARN, FAIL_TEST
    }

    public ResponseHeaderCheckPolicy getResponseHeaderCheckPolicy() {
        String setting = getOptionalVariable("BEFTA_RESPONSE_HEADER_CHECK_POLICY");
        return setting == null ? ResponseHeaderCheckPolicy.FAIL_TEST : ResponseHeaderCheckPolicy.valueOf(setting);
    }

    public double getTestDataLoadSkipPeriod() {
        double testDataLoadSkipPeriod = DEFAULT_TEST_DATA_LOAD_SKIP_PERIOD;
        String envVar = getOptionalVariable("TEST_DATA_LOAD_SKIP_PERIOD");
        if (envVar != null) {
            testDataLoadSkipPeriod = Double.parseDouble(envVar);
        }
        return testDataLoadSkipPeriod;
    }

    public AuthenticationRetryConfiguration getAuthenticationRetryConfiguration () {
        return new AuthenticationRetryConfiguration(
                getRetryAttempts(),
                getRetryMaxTimeInSeconds(),
                getRetryMultiplierInMilliseconds()
        );
    }

    public Long getUserTokenCacheTtlInSeconds() {
        Long envVar = NumberUtils.createLong(getOptionalVariable("BEFTA_USER_TOKEN_CACHE_TTL_SECONDS"));
        return envVar != null ? envVar : 0L;
    }

    public Long getS2STokenCacheTtlInSeconds() {
        Long envVar = NumberUtils.createLong(getOptionalVariable("BEFTA_S2S_TOKEN_CACHE_TTL_SECONDS"));
        return envVar != null ? envVar : 0L;
    }

    public boolean isHttpLoggingEnabled() {
        return Boolean.parseBoolean(getOptionalVariable("BEFTA_HTTP_LOGGING_ENABLED"));
    }

    private int getRetryAttempts() {
        int retryAttempts = NumberUtils.toInt(getOptionalVariable("BEFTA_USER_AUTHENTICATION_RETRY_MAX_ATTEMPTS"));
        return retryAttempts !=0 ? retryAttempts : 3;
    }

    private int getRetryMaxTimeInSeconds() {
        int retryMaxTimeInSeconds = NumberUtils.toInt(getOptionalVariable("BEFTA_USER_AUTHENTICATION_RETRY_MAX_TIME_SECONDS"));
        return retryMaxTimeInSeconds !=0 ? retryMaxTimeInSeconds : 10;
    }

    private int getRetryMultiplierInMilliseconds() {
        int retryMultiplierInMilliseconds = NumberUtils.toInt(getOptionalVariable("BEFTA_USER_AUTHENTICATION_RETRY_MULTIPLIER_MILLISECONDS"));
        return retryMultiplierInMilliseconds !=0 ? retryMultiplierInMilliseconds : 60;
    }
}


