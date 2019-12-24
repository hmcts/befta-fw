package uk.gov.hmcts.befta.ccd;

import java.util.function.Supplier;

import io.restassured.specification.RequestSpecification;
import uk.gov.hmcts.befta.ccd.helper.idam.IdamHelper;
import uk.gov.hmcts.befta.ccd.helper.idam.OAuth2;
import uk.gov.hmcts.befta.util.EnvUtils;

public enum AATHelper {

    INSTANCE;

    private final IdamHelper idamHelper;
    private final S2SHelper s2SHelper;
    private final CCDHelper ccdHelper;

    AATHelper() {
        idamHelper = new IdamHelper(getIdamURL(), OAuth2.INSTANCE);
        s2SHelper = new S2SHelper(getS2SURL(), getGatewayServiceSecret(), getGatewayServiceName());
        ccdHelper = new CCDHelper();
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

    public IdamHelper getIdamHelper() {
        return idamHelper;
    }

    public S2SHelper getS2SHelper() {
        return s2SHelper;
    }

    public CCDHelper getCcdHelper() {
        return ccdHelper;
    }

    public String generateTokenUpdateCase(Supplier<RequestSpecification> asUser,
                                          String jurisdiction,
                                          String caseType,
                                          Long caseReference,
                                          String event) {
        return ccdHelper.generateTokenUpdateCase(asUser, jurisdiction, caseType, caseReference, event);
    }

    public String getCaseworkerAutoTestEmail() {
        return EnvUtils.require("CCD_CASEWORKER_AUTOTEST_EMAIL");
    }

    public String getCaseworkerAutoTestPassword() {
        return EnvUtils.require("CCD_CASEWORKER_AUTOTEST_PASSWORD");
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

    public String getPrivateCaseworkerEmail() {
        return EnvUtils.require("CCD_PRIVATE_CASEWORKER_EMAIL");
    }

    public String getPrivateCaseworkerPassword() {
        return EnvUtils.require("CCD_PRIVATE_CASEWORKER_PASSWORD");
    }

    public String getRestrictedCaseworkerEmail() {
        return EnvUtils.require("CCD_RESTRICTED_CASEWORKER_EMAIL");
    }

    public String getRestrictedCaseworkerPassword() {
        return EnvUtils.require("CCD_RESTRICTED_CASEWORKER_PASSWORD");
    }

    public String getPrivateCaseworkerSolicitorEmail() {
        return EnvUtils.require("CCD_PRIVATE_CASEWORKER_SOLICITOR_EMAIL");
    }

    public String getPrivateCaseworkerSolicitorPassword() {
        return EnvUtils.require("CCD_PRIVATE_CASEWORKER_SOLICITOR_PASSWORD");
    }

    public String getPrivateCrossCaseTypeCaseworkerEmail() {
        return EnvUtils.require("CCD_PRIVATE_CROSS_CASE_TYPE_CASEWORKER_EMAIL");
    }

    public String getPrivateCrossCaseTypeCaseworkerPassword() {
        return EnvUtils.require("CCD_PRIVATE_CROSS_CASE_TYPE_CASEWORKER_PASSWORD");
    }

    public String getPrivateCrossCaseTypeSolicitorEmail() {
        return EnvUtils.require("CCD_PRIVATE_CROSS_CASE_TYPE_SOLICITOR_EMAIL");
    }

    public String getPrivateCrossCaseTypeSolicitorPassword() {
        return EnvUtils.require("CCD_PRIVATE_CROSS_CASE_TYPE_SOLICITOR_PASSWORD");
    }

    public String getRestrictedCrossCaseTypeCaseworkerEmail() {
        return EnvUtils.require("CCD_RESTRICTED_CROSS_CASE_TYPE_CASEWORKER_EMAIL");
    }

    public String getRestrictedCrossCaseTypeCaseworkerPassword() {
        return EnvUtils.require("CCD_RESTRICTED_CROSS_CASE_TYPE_CASEWORKER_PASSWORD");
    }

}
