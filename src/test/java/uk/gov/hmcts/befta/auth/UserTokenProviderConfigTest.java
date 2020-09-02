/**
 * 
 */
package uk.gov.hmcts.befta.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

/**
 * @author korneleehenry
 *
 */
class UserTokenProviderConfigTest {

	/**BEFTA_OAUTH2_ACCESS_TOKEN_TYPE_OF_
	 * Test method for {@link uk.gov.hmcts.befta.auth.UserTokenProviderConfig#of(java.lang.String)}.
	 */
	@Test
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
	void testOf() {
		String tokenProviderClientId = "OAUTH2_CLIENT_ID_VALUE";
		UserTokenProviderConfig actual = UserTokenProviderConfig.of(tokenProviderClientId);
		assertNotNull(actual);
		assertEquals(UserTokenProviderConfig.DEFAULT_INSTANCE,actual);
		assertEquals(UserTokenProviderConfig.DEFAULT_INSTANCE.hashCode(),actual.hashCode());
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.auth.UserTokenProviderConfig#isForOidc()}.
	 */
	@Test
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
	void testIsForOidc() {
		String tokenProviderClientId = "OAUTH2_CLIENT_ID_VALUE";
		UserTokenProviderConfig actual = UserTokenProviderConfig.of(tokenProviderClientId);
		assertFalse(actual.isForOidc());
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.auth.UserTokenProviderConfig#isForOauth2()}.
	 */
	@Test
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
	void testIsForOauth2() {
		String tokenProviderClientId = "OAUTH2_CLIENT_ID_VALUE";
		UserTokenProviderConfig actual = UserTokenProviderConfig.of(tokenProviderClientId);
		assertTrue(actual.isForOauth2());
	}

	@Test
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_REDIRECT_URI_OF_OTHER", value = "BEFTA_OAUTH2_REDIRECT_URI_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_CLIENT_SECRET_OF_OTHER", value = "BEFTA_OAUTH2_CLIENT_SECRET_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_SCOPE_VARIABLES_OF_OTHER", value = "BEFTA_OAUTH2_SCOPE_VARIABLES_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_ACCESS_TOKEN_TYPE_OF_OTHER", value = "OIDC")
	void testOfOther() {
		String tokenProviderClientId = "OTHER";
		UserTokenProviderConfig actual = UserTokenProviderConfig.of(tokenProviderClientId);
		assertNotNull(actual);
		assertNotEquals(UserTokenProviderConfig.DEFAULT_INSTANCE,actual);
		assertNotEquals(UserTokenProviderConfig.DEFAULT_INSTANCE.hashCode(),actual.hashCode());
		assertEquals("OTHER",actual.getClientId());
		assertEquals("BEFTA_OAUTH2_CLIENT_SECRET_OF_OTHER_VALUE",actual.getClientSecret());
		assertEquals("BEFTA_OAUTH2_REDIRECT_URI_OF_OTHER_VALUE",actual.getRedirectUri());
		assertEquals("BEFTA_OAUTH2_SCOPE_VARIABLES_OF_OTHER_VALUE",actual.getScopeVariables());
		assertTrue(actual.isForOidc());
		assertNotNull(actual.toString());
	}
}
