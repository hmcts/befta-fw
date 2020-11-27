package uk.gov.hmcts.befta;

import uk.gov.hmcts.befta.auth.UserTokenProviderConfig;
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
        return EnvironmentVariableUtils.getRequiredVariable("IDAM_URL");
    }

    public String getS2SURL() {
        return EnvironmentVariableUtils.getRequiredVariable("S2S_URL");
    }

    public String getS2SClientId() {
        return EnvironmentVariableUtils.getRequiredVariable("BEFTA_S2S_CLIENT_ID");
    }

    public String getS2SClientSecret() {
        return EnvironmentVariableUtils.getRequiredVariable("BEFTA_S2S_CLIENT_SECRET");
    }

    public String getDefinitionStoreUrl() {
        return EnvironmentVariableUtils.getRequiredVariable("DEFINITION_STORE_HOST");
    }

    public String getImporterAutoTestEmail() {
        return EnvironmentVariableUtils.getRequiredVariable("CCD_IMPORT_AUTOTEST_EMAIL");
    }

    public String getImporterAutoTestPassword() {
        return EnvironmentVariableUtils.getRequiredVariable("CCD_IMPORT_AUTOTEST_PASSWORD");
    }

    public UserTokenProviderConfig getUserTokenProviderConfig() {
        return UserTokenProviderConfig.DEFAULT_INSTANCE;
    }

    public static enum ResponseHeaderCheckPolicy {
        IGNORE, JUST_WARN, FAIL_TEST
    }

    public ResponseHeaderCheckPolicy getResponseHeaderCheckPolicy() {
        String setting = EnvironmentVariableUtils.getOptionalVariable("BEFTA_RESPONSE_HEADER_CHECK_POLICY");
        return setting == null ? ResponseHeaderCheckPolicy.FAIL_TEST : ResponseHeaderCheckPolicy.valueOf(setting);
    }

    public double getTestDataLoadSkipPeriod() {
        double testDataLoadSkipPeriod = DEFAULT_TEST_DATA_LOAD_SKIP_PERIOD;
        String envVar = EnvironmentVariableUtils.getOptionalVariable("TEST_DATA_LOAD_SKIP_PERIOD");
        if (envVar != null) {
            testDataLoadSkipPeriod = Double.parseDouble(envVar);
        }
        return testDataLoadSkipPeriod;
    }

}


