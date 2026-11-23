/**
 * 
 */
package uk.gov.hmcts.befta.factory;

import org.springframework.cloud.openfeign.support.SpringMvcContract;

import feign.Feign;
import feign.jackson3.Jackson3Encoder;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

/**
 * @author korneleehenry
 *
 */
public class BeftaServiceAuthorisationApiClientFactory {

    private BeftaServiceAuthorisationApiClientFactory() {
    }

    public static ServiceAuthorisationApi createServiceAuthorisationApiClient() {
        return Feign.builder().encoder(new Jackson3Encoder()).contract(
                new SpringMvcContract())
                .target(ServiceAuthorisationApi.class, BeftaMain.getConfig().getS2SURL());
    }

}
