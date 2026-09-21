package uk.gov.hmcts.befta.util;

import static io.restassured.config.ConnectionConfig.connectionConfig;

import io.restassured.RestAssured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.befta.BeftaMain;

public final class RestAssuredConfigurator {

    private static final Logger log = LoggerFactory.getLogger(RestAssuredConfigurator.class);

    private RestAssuredConfigurator() {
    }

    public static void configure() {
        RestAssured.useRelaxedHTTPSValidation();

        if (BeftaMain.getConfig().isHttpCloseConnectionAfterResponseEnabled()) {
            log.info("BEFTA HTTP close connection after each response is ENABLED");
            RestAssured.config = RestAssured.config()
                    .connectionConfig(connectionConfig().closeIdleConnectionsAfterEachResponse());
        }
    }
}
