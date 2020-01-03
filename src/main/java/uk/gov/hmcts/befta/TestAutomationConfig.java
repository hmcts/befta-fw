package uk.gov.hmcts.befta;

import uk.gov.hmcts.befta.auth.OAuth2;
import uk.gov.hmcts.befta.util.EnvUtils;

public class TestAutomationConfig {

    public static final TestAutomationConfig INSTANCE = new TestAutomationConfig();

    private TestAutomationConfig() {
    }

    public String getTestUrl() {
        return EnvUtils.require("TEST_URL");
    }

    public String getIdamURL() {
        return EnvUtils.require("IDAM_URL");
    }

    public String getS2SURL() {
        return EnvUtils.require("S2S_URL");
    }

    public String getGatewayServiceName() {
        return EnvUtils.require("CCD_GW_SERVICE_NAME");
    }

    public String getGatewayServiceSecret() {
        return EnvUtils.require("CCD_GW_SERVICE_SECRET");
    }

    public String getDefinitionStoreUrl() {
        return EnvUtils.require("DEFINITION_STORE_HOST");
    }

    public String getImporterAutoTestEmail() {
        return EnvUtils.require("CCD_IMPORT_AUTOTEST_EMAIL");
    }

    public String getImporterAutoTestPassword() {
        return EnvUtils.require("CCD_IMPORT_AUTOTEST_PASSWORD");
    }

    public OAuth2 getOauth2Config() {
        return OAuth2.INSTANCE;
    }

}
