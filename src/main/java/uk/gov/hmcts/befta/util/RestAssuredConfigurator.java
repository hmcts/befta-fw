package uk.gov.hmcts.befta.util;

import io.restassured.RestAssured;
import io.restassured.config.ConnectionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.befta.BeftaMain;

public final class RestAssuredConfigurator {

    private static final Logger log = LoggerFactory.getLogger(RestAssuredConfigurator.class);

    private RestAssuredConfigurator() {
    }

    public static void configure() {
        RestAssured.useRelaxedHTTPSValidation();

        if (BeftaMain.getConfig().shouldUseFreshHttpClientForEachRequest()) {
            log.info("BEFTA HTTP fresh client per request is ENABLED");
            RestAssured.config = RestAssured.config()
                    .httpClient(RestAssured.config().getHttpClientConfig().dontReuseHttpClientInstance())
                    .connectionConfig(new ConnectionConfig());
        }
    }
}
