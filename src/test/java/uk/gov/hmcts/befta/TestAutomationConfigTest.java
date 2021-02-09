/**
 * 
 */
package uk.gov.hmcts.befta;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import uk.gov.hmcts.befta.TestAutomationConfig.ResponseHeaderCheckPolicy;
import uk.gov.hmcts.befta.auth.UserTokenProviderConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.befta.data.CollectionVerificationConfig.Ordering;

/**
 * @author korneleehenry
 *
 */
class TestAutomationConfigTest {
    private static final String TEST_URL_KEY = "TEST_URL";
    private static final String TEST_URL_VALUE = "TEST_URL_VALUE";
    private static final String IDAM_URL_KEY = "IDAM_URL";
    private static final String IDAM_URL_VALUE = "IDAM_URL_VALUE";
    private static final String S2S_URL_KEY = "S2S_URL";
    private static final String S2S_URL_VALUE = "S2S_URL_VALUE";
    private static final String BEFTA_S2S_CLIENT_ID_KEY = "BEFTA_S2S_CLIENT_ID";
    private static final String BEFTA_S2S_CLIENT_ID_VALUE = "BEFTA_S2S_CLIENT_ID_VALUE";
    private static final String BEFTA_S2S_CLIENT_SECRET_KEY = "BEFTA_S2S_CLIENT_SECRET";
    private static final String BEFTA_S2S_CLIENT_SECRET_VALUE = "BEFTA_S2S_CLIENT_SECRET_VALUE";
    private static final String DEFINITION_STORE_HOST_KEY = "DEFINITION_STORE_HOST";
    private static final String DEFINITION_STORE_HOST_VALUE = "DEFINITION_STORE_HOST_VALUE";
    private static final String CCD_IMPORT_AUTOTEST_EMAIL = "CCD_IMPORT_AUTOTEST_EMAIL";
    private static final String CCD_IMPORT_AUTOTEST_EMAIL_VALUE = "CCD_IMPORT_AUTOTEST_EMAIL_VALUE";
    private static final String CCD_IMPORT_AUTOTEST_PASSWORD = "CCD_IMPORT_AUTOTEST_PASSWORD";
    private static final String CCD_IMPORT_AUTOTEST_PASSWORD_VALUE = "CCD_IMPORT_AUTOTEST_PASSWORD_VALUE";
    private static final String BEFTA_RESPONSE_HEADER_CHECK_POLICY = "BEFTA_RESPONSE_HEADER_CHECK_POLICY";
    private static final String BEFTA_RESPONSE_HEADER_CHECK_POLICY_VALUE = "JUST_WARN";
    private static final String DEFAULT_COLLECTION_ASSERTION_MODE = "DEFAULT_COLLECTION_ASSERTION_MODE";

    /**
     * Test method for {@link uk.gov.hmcts.befta.TestAutomationConfig#getTestUrl()}.
     */
    @Test
    @SetEnvironmentVariable(key = TEST_URL_KEY, value = TEST_URL_VALUE)
    void testGetTestUrl() {
        assertEquals(TEST_URL_VALUE, TestAutomationConfig.INSTANCE.getTestUrl());
    }

    /**
     * Test method for {@link uk.gov.hmcts.befta.TestAutomationConfig#getIdamURL()}.
     */
    @Test
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    void testGetIdamURL() {
        assertEquals(IDAM_URL_VALUE, TestAutomationConfig.INSTANCE.getIdamURL());
    }

    /**
     * Test method for {@link uk.gov.hmcts.befta.TestAutomationConfig#getS2SURL()}.
     */
    @Test
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testGetS2SURL() {
        assertEquals(S2S_URL_VALUE, TestAutomationConfig.INSTANCE.getS2SURL());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.TestAutomationConfig#getS2SClientId()}.
     */
    @Test
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    void testGetS2SClientId() {
        assertEquals(BEFTA_S2S_CLIENT_ID_VALUE, TestAutomationConfig.INSTANCE.getS2SClientId());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.TestAutomationConfig#getS2SClientSecret()}.
     */
    @Test
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    void testGetS2SClientSecret() {
        assertEquals(BEFTA_S2S_CLIENT_SECRET_VALUE, TestAutomationConfig.INSTANCE.getS2SClientSecret());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.TestAutomationConfig#getDefinitionStoreUrl()}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    void testGetDefinitionStoreUrl() {
        assertEquals(DEFINITION_STORE_HOST_VALUE, TestAutomationConfig.INSTANCE.getDefinitionStoreUrl());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.TestAutomationConfig#getImporterAutoTestEmail()}.
     */
    @Test
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_EMAIL, value = CCD_IMPORT_AUTOTEST_EMAIL_VALUE)
    void testGetImporterAutoTestEmail() {
        assertEquals(CCD_IMPORT_AUTOTEST_EMAIL_VALUE, TestAutomationConfig.INSTANCE.getImporterAutoTestEmail());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.TestAutomationConfig#getImporterAutoTestPassword()}.
     */
    @Test
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_PASSWORD, value = CCD_IMPORT_AUTOTEST_PASSWORD_VALUE)
    void testGetImporterAutoTestPassword() {
        assertEquals(CCD_IMPORT_AUTOTEST_PASSWORD_VALUE, TestAutomationConfig.INSTANCE.getImporterAutoTestPassword());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.TestAutomationConfig#getUserTokenProviderConfig()}.
     */
    @Test
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
    void testGetUserTokenProviderConfig() {
        UserTokenProviderConfig actual = TestAutomationConfig.INSTANCE.getUserTokenProviderConfig();
        assertNotNull(actual);
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.TestAutomationConfig#getResponseHeaderCheckPolicy()}.
     */
    @Test
    @SetEnvironmentVariable(key = BEFTA_RESPONSE_HEADER_CHECK_POLICY, value = BEFTA_RESPONSE_HEADER_CHECK_POLICY_VALUE)
    void testGetResponseHeaderCheckPolicy() {

        assertEquals(ResponseHeaderCheckPolicy.JUST_WARN, TestAutomationConfig.INSTANCE.getResponseHeaderCheckPolicy());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.TestAutomationConfig#getResponseHeaderCheckPolicy()}.
     */
    @Test
    @SetEnvironmentVariable(key = "BEFTA_RESPONSE_HEADER_CHECK_POLICY", value = "FAIL_TEST")
    void testGetResponseHeaderCheckPolicyNull() {
        assertEquals(ResponseHeaderCheckPolicy.FAIL_TEST, TestAutomationConfig.INSTANCE.getResponseHeaderCheckPolicy());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#getDefaultCollectionAssertionMode()} ()}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFAULT_COLLECTION_ASSERTION_MODE, value = "SetValue")
    void testDefaultCollectionAssertionModeSetToUnexpectedValue() {
        assertEquals(Ordering.ORDERED, TestAutomationConfig.INSTANCE.getDefaultCollectionAssertionMode());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#getDefaultCollectionAssertionMode()} ()}.
     */
    @Test
    void testDefaultCollectionAssertionModeReturnsDefaultWhenNotSet() {
        assertEquals(Ordering.ORDERED, TestAutomationConfig.INSTANCE.getDefaultCollectionAssertionMode());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#getDefaultCollectionAssertionMode()} ()}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFAULT_COLLECTION_ASSERTION_MODE, value = "UNORDERED")
    void testDefaultCollectionAssertionModeReturnsValue() {
        assertEquals(Ordering.UNORDERED, TestAutomationConfig.INSTANCE.getDefaultCollectionAssertionMode());
    }
}
