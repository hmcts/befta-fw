/**
 * 
 */
package uk.gov.hmcts.befta.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import uk.gov.hmcts.befta.auth.AuthApi;

/**
 * @author korneleehenry
 *
 */
class BeftaIdamApiClientFactoryTest {

    public static final String IDAM_URL_KEY = "IDAM_API_URL_BASE";
	public static final String IDAM_URL_VALUE = "IDAM_URL_VALUE\"";

	/**
	 * Test method for {@link uk.gov.hmcts.befta.factory.BeftaIdamApiClientFactory#createAuthorizationClient()}.
	 */
	@Test
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
	void testCreateAuthorizationClient() {
		AuthApi authApi = BeftaIdamApiClientFactory.createAuthorizationClient();
		assertNotNull(authApi);
	}

}
