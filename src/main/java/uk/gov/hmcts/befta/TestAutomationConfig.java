package uk.gov.hmcts.befta;

import uk.gov.hmcts.befta.auth.OAuth2;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;

public class TestAutomationConfig {

    public static final TestAutomationConfig INSTANCE = new TestAutomationConfig();

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

    public String getGatewayServiceName() {
        return EnvironmentVariableUtils.getRequiredVariable("CCD_GW_SERVICE_NAME");
    }

    public String getGatewayServiceSecret() {
        return EnvironmentVariableUtils.getRequiredVariable("CCD_GW_SERVICE_SECRET");
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

    public OAuth2 getOauth2Config() {
        return OAuth2.INSTANCE;
    }

    public static enum ResponseHeaderCheckPolicy {
        IGNORE, JUST_WARN, FAIL_TEST
    }

    public ResponseHeaderCheckPolicy getResponseHeaderCheckPolicy() {
        String setting = EnvironmentVariableUtils.getOptionalVariable("BEFTA_RESPONSE_HEADER_CHECK_POLICY");
        return setting == null ? ResponseHeaderCheckPolicy.FAIL_TEST : ResponseHeaderCheckPolicy.valueOf(setting);
    }

}
