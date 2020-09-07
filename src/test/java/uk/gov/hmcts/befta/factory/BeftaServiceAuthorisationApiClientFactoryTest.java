/**
 * 
 */
package uk.gov.hmcts.befta.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

/**
 * @author korneleehenry
 *
 */
class BeftaServiceAuthorisationApiClientFactoryTest {

    public static final String S2S_URL_KEY = "S2S_URL";
	public static final String S2S_URL_VALUE = "S2S_URL_VALUE";

	/**
	 * Test method for {@link uk.gov.hmcts.befta.factory.BeftaServiceAuthorisationApiClientFactory#createServiceAuthorisationApiClient()}.
	 */
	@Test
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
	void testCreateServiceAuthorisationApiClient() {
		ServiceAuthorisationApi actual = BeftaServiceAuthorisationApiClientFactory.createServiceAuthorisationApiClient();
		assertNotNull(actual);
	}

}
