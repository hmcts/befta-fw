package uk.gov.hmcts.befta.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.TestAutomationConfig;

class RestAssuredConfiguratorTest {

    @BeforeEach
    void setUp() {
        BeftaMain.setConfig(TestAutomationConfig.INSTANCE);
        RestAssured.reset();
    }

    @AfterEach
    void tearDown() {
        RestAssured.reset();
    }

    @Test
    @SetEnvironmentVariable(key = "BEFTA_HTTP_CLOSE_CONNECTION_AFTER_RESPONSE", value = "true")
    void shouldCloseIdleConnectionsAfterEachResponseWhenEnabled() {
        RestAssuredConfigurator.configure();

        assertTrue(RestAssured.config()
                .getConnectionConfig()
                .shouldCloseIdleConnectionsAfterEachResponse());
    }

    @Test
    void shouldKeepRestAssuredDefaultConnectionConfigWhenNotEnabled() {
        RestAssuredConfigurator.configure();

        assertFalse(RestAssured.config()
                .getConnectionConfig()
                .shouldCloseIdleConnectionsAfterEachResponse());
    }
}
