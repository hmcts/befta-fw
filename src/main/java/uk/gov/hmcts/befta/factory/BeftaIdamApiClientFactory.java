/**
 * 
 */
package uk.gov.hmcts.befta.factory;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.auth.AuthApi;

/**
 * @author korneleehenry
 *
 */
public class BeftaIdamApiClientFactory {
	private BeftaIdamApiClientFactory() {}
	public static AuthApi createAuthorizationClient() {
		return Feign.builder().encoder(new JacksonEncoder()).decoder(new JacksonDecoder()).target(AuthApi.class,
                BeftaMain.getConfig().getIdamURL());
	}
}
