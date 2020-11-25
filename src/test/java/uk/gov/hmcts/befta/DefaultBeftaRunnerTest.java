/**
 * 
 */
package uk.gov.hmcts.befta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

/**
 * @author korneleehenry
 *
 */
class DefaultBeftaRunnerTest {
    public static final String DEFINITION_STORE_HOST_KEY = "DEFINITION_STORE_HOST";
    public static final String DEFINITION_STORE_HOST_VALUE = "http://127.0.0.1:8089/";
    public static final String IDAM_URL_KEY = "IDAM_URL";
    public static final String IDAM_URL_VALUE = "IDAM_URL_VALUE";
    public static final String S2S_URL_KEY = "S2S_URL";
    public static final String S2S_URL_VALUE = "S2S_URL_VALUE";
    public static final String BEFTA_S2S_CLIENT_ID_KEY = "BEFTA_S2S_CLIENT_ID";
    public static final String BEFTA_S2S_CLIENT_ID_VALUE = "BEFTA_S2S_CLIENT_ID_VALUE";
    public static final String BEFTA_S2S_CLIENT_SECRET_KEY = "BEFTA_S2S_CLIENT_SECRET";
    public static final String BEFTA_S2S_CLIENT_SECRET_VALUE = "BEFTA_S2S_CLIENT_SECRET_VALUE";
    public static final String TEST_DATA_LOAD_SKIP_PERIOD_KEY = "TEST_DATA_LOAD_SKIP_PERIOD";
    public static final String TEST_DATA_LOAD_SKIP_PERIOD_VALUE = "0";

    /**
     * Test method for {@link uk.gov.hmcts.befta.DefaultBeftaRunner#setUp()}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = TEST_DATA_LOAD_SKIP_PERIOD_KEY, value = TEST_DATA_LOAD_SKIP_PERIOD_VALUE)
    void testSetUp() {
        DefaultBeftaRunner.setUp();
        assertNotNull(BeftaMain.getAdapter());
        assertEquals(TestAutomationConfig.INSTANCE, BeftaMain.getConfig());
    }

    /**
     * Test method for {@link uk.gov.hmcts.befta.DefaultBeftaRunner#tearDown()}.
     */
    @Test
    void testTearDown() {
        DefaultBeftaRunner.tearDown();
        ;
    }

}
