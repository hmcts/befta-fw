/**
 * 
 */
package uk.gov.hmcts.befta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import uk.gov.hmcts.befta.TestAutomationConfig.ResponseHeaderCheckPolicy;
import uk.gov.hmcts.befta.auth.UserTokenProviderConfig;
import uk.gov.hmcts.befta.data.CollectionVerificationConfig.Ordering;

/**
 * @author korneleehenry
 *
 */
class TestAutomationConfigTest {
    private static final String TEST_URL_KEY = "TEST_URL";
    private static final String TEST_URL_VALUE = "TEST_URL_VALUE";
    private static final String IDAM_URL_KEY = "IDAM_API_URL_BASE";
    private static final String IDAM_URL_VALUE = "IDAM_URL_VALUE";
    private static final String S2S_URL_KEY = "S2S_URL_BASE";
    private static final String S2S_URL_VALUE = "S2S_URL_VALUE";
    private static final String BEFTA_S2S_CLIENT_ID_KEY = "BEFTA_S2S_CLIENT_ID";
    private static final String BEFTA_S2S_CLIENT_ID_VALUE = "BEFTA_S2S_CLIENT_ID_VALUE";
    private static final String BEFTA_S2S_CLIENT_SECRET_KEY = "BEFTA_S2S_CLIENT_SECRET";
    private static final String BEFTA_S2S_CLIENT_SECRET_VALUE = "BEFTA_S2S_CLIENT_SECRET_VALUE";
    private static final String DEFINITION_STORE_HOST_KEY = "DEFINITION_STORE_URL_BASE";
    private static final String DEFINITION_STORE_HOST_VALUE = "DEFINITION_STORE_HOST_VALUE";
    private static final String CCD_IMPORT_AUTOTEST_EMAIL = "DEFINITION_IMPORTER_USERNAME";
    private static final String CCD_IMPORT_AUTOTEST_EMAIL_VALUE = "CCD_IMPORT_AUTOTEST_EMAIL_VALUE";
    private static final String CCD_IMPORT_AUTOTEST_PASSWORD = "DEFINITION_IMPORTER_PASSWORD";
    private static final String CCD_IMPORT_AUTOTEST_PASSWORD_VALUE = "CCD_IMPORT_AUTOTEST_PASSWORD_VALUE";
    private static final String BEFTA_RESPONSE_HEADER_CHECK_POLICY = "BEFTA_RESPONSE_HEADER_CHECK_POLICY";
    private static final String BEFTA_RESPONSE_HEADER_CHECK_POLICY_VALUE = "JUST_WARN";
    private static final String DEFAULT_COLLECTION_ASSERTION_MODE = "DEFAULT_COLLECTION_ASSERTION_MODE";
    private static final String BEFTA_USER_AUTHENTICATION_RETRY_MAX_ATTEMPTS_VALUE = "5";
    private static final String BEFTA_USER_AUTHENTICATION_RETRY_MAX_TIME_VALUE = "9";
    private static final String BEFTA_USER_AUTHENTICATION_RETRY_MULTIPLIER_VALUE = "1500";
    private static final String BEFTA_USER_TOKEN_CACHE_TTL_VALUE = "3";
    private static final String BEFTA_S2S_TOKEN_CACHE_TTL_VALUE = "4";

    @BeforeEach
//    @SetEnvironmentVariable(key = "TEST_URL", value = TEST_URL_VALUE)
    void setup() {
    }

    @AfterEach
    void cleanup() {
    }

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
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_REDIRECT_URL", value = "OAUTH2_REDIRECT_URI_VALUE")
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
     * {@link uk.gov.hmcts.befta.TestAutomationConfig#getAuthenticationRetryConfiguration()}.
     */
    @Test
    @SetEnvironmentVariable(key = "BEFTA_USER_AUTHENTICATION_RETRY_MAX_ATTEMPTS",
            value = BEFTA_USER_AUTHENTICATION_RETRY_MAX_ATTEMPTS_VALUE)
    void testGetRetryAttempts() {
        assertEquals(Integer.parseInt(BEFTA_USER_AUTHENTICATION_RETRY_MAX_ATTEMPTS_VALUE),
                TestAutomationConfig.INSTANCE.getAuthenticationRetryConfiguration().getRetryAttempts());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#getAuthenticationRetryConfiguration()}.
     */
    @Test
    @SetEnvironmentVariable(key = "BEFTA_USER_AUTHENTICATION_RETRY_MAX_TIME_SECONDS",
            value = BEFTA_USER_AUTHENTICATION_RETRY_MAX_TIME_VALUE)
    void testGetRetryMaxTimeInSeconds() {
        assertEquals(Integer.parseInt(BEFTA_USER_AUTHENTICATION_RETRY_MAX_TIME_VALUE),
                TestAutomationConfig.INSTANCE.getAuthenticationRetryConfiguration().getRetryMaxTimeInSeconds());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#getAuthenticationRetryConfiguration()}.
     */
    @Test
    @SetEnvironmentVariable(key = "BEFTA_USER_AUTHENTICATION_RETRY_MULTIPLIER_MILLISECONDS",
            value = BEFTA_USER_AUTHENTICATION_RETRY_MULTIPLIER_VALUE)
    void testGetRetryMultiplierTimeinMilliseconds() {
        assertEquals(Integer.parseInt(BEFTA_USER_AUTHENTICATION_RETRY_MULTIPLIER_VALUE),
                TestAutomationConfig.INSTANCE.getAuthenticationRetryConfiguration().getRetryMultiplierTimeinMilliseconds());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#getUserTokenCacheTtlInSeconds()}.
     */
    @Test
    @SetEnvironmentVariable(key = "BEFTA_USER_TOKEN_CACHE_TTL_SECONDS",
            value = BEFTA_USER_TOKEN_CACHE_TTL_VALUE)
    void testGetUserTokenCacheTtlInSeconds() {
        assertEquals(Long.parseLong(BEFTA_USER_TOKEN_CACHE_TTL_VALUE),
                TestAutomationConfig.INSTANCE.getUserTokenCacheTtlInSeconds());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#getUserTokenCacheTtlInSeconds()}.
     */
    @Test
    void testGetUserTokenCacheTtlInSecondsNoValueSet() {
        assertEquals(0L,
                TestAutomationConfig.INSTANCE.getUserTokenCacheTtlInSeconds());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#getS2STokenCacheTtlInSeconds()}.
     */
    @Test
    @SetEnvironmentVariable(key = "BEFTA_S2S_TOKEN_CACHE_TTL_SECONDS",
            value = BEFTA_S2S_TOKEN_CACHE_TTL_VALUE)
    void testGetS2STokenCacheTtlInSeconds() {
        assertEquals(Long.parseLong(BEFTA_S2S_TOKEN_CACHE_TTL_VALUE),
                TestAutomationConfig.INSTANCE.getS2STokenCacheTtlInSeconds());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#getS2STokenCacheTtlInSeconds()}.
     */
    @Test
    void testGetS2STokenCacheTtlInSecondsNoValuesSet() {
        assertEquals(0L,
                TestAutomationConfig.INSTANCE.getS2STokenCacheTtlInSeconds());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#isHttpLoggingEnabled()}.
     */
    @Test
    @SetEnvironmentVariable(key = "BEFTA_HTTP_LOGGING_ENABLED",
            value = "TRUE")
    void testIsHttpLoggingEnabled() {
        assertTrue(TestAutomationConfig.INSTANCE.isHttpLoggingEnabled());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#isHttpLoggingEnabled()}.
     */
    @Test
    @SetEnvironmentVariable(key = "BEFTA_HTTP_LOGGING_ENABLED",
            value = "Not True")
    void testIsHttpLoggingEnabledValueNotBoolean() {
        assertFalse(TestAutomationConfig.INSTANCE.isHttpLoggingEnabled());
    }

    /**
     * Test method for
     * {@link TestAutomationConfig#isHttpLoggingEnabled()}.
     */
    @Test
    void testIsHttpLoggingEnabledEnvVarNotPresent() {
        assertFalse(TestAutomationConfig.INSTANCE.isHttpLoggingEnabled());
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
