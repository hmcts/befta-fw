/**
 * 
 */
package uk.gov.hmcts.befta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author korneleehenry
 *
 */
class BeftaMainTest {
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
    private MockedStatic<io.cucumber.core.cli.Main> mainrMock = null;

    @BeforeEach
    public void prepareMockedObjectUnderTest() {
        try {
            mainrMock = mockStatic(io.cucumber.core.cli.Main.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void closeMockedObjectUnderTest() {
        try {
            mainrMock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.BeftaMain#main(java.lang.String[])}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = TEST_DATA_LOAD_SKIP_PERIOD_KEY, value = TEST_DATA_LOAD_SKIP_PERIOD_VALUE)
    void testMainStringArray() {
        String[] args = {};
        byte success = 0;
        Mockito.when(io.cucumber.core.cli.Main.run(isA(String[].class), isA(ClassLoader.class))).thenReturn(success);
        BeftaMain.main(args);
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.BeftaMain#main(java.lang.String[], uk.gov.hmcts.befta.TestAutomationConfig)}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = TEST_DATA_LOAD_SKIP_PERIOD_KEY, value = TEST_DATA_LOAD_SKIP_PERIOD_VALUE)
    void testMainStringArrayTestAutomationConfig() {
        String[] args = {};
        byte success = 0;
        Mockito.when(io.cucumber.core.cli.Main.run(isA(String[].class), isA(ClassLoader.class))).thenReturn(success);
        BeftaMain.main(args, TestAutomationConfig.INSTANCE);
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.BeftaMain#main(java.lang.String[], uk.gov.hmcts.befta.TestAutomationAdapter)}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = TEST_DATA_LOAD_SKIP_PERIOD_KEY, value = TEST_DATA_LOAD_SKIP_PERIOD_VALUE)
    void testMainStringArrayTestAutomationAdapter() {
        BeftaMain.setUp(TestAutomationConfig.INSTANCE);
        assertEquals(TestAutomationConfig.INSTANCE, BeftaMain.getConfig());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.BeftaMain#main(java.lang.String[], uk.gov.hmcts.befta.TestAutomationConfig, uk.gov.hmcts.befta.TestAutomationAdapter)}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = TEST_DATA_LOAD_SKIP_PERIOD_KEY, value = TEST_DATA_LOAD_SKIP_PERIOD_VALUE)
    void testMainStringArrayTestAutomationConfigTestAutomationAdapter() {
        TestAutomationAdapter taAdapter = new DefaultTestAutomationAdapter();
        BeftaMain.setUp(taAdapter);
        ;
        assertEquals(taAdapter, BeftaMain.getAdapter());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.BeftaMain#runCucumberMain(java.lang.String[])}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = TEST_DATA_LOAD_SKIP_PERIOD_KEY, value = TEST_DATA_LOAD_SKIP_PERIOD_VALUE)
    void testRunCucumberMain() {
        String[] args = {};
        byte success = 0;
        Mockito.when(io.cucumber.core.cli.Main.run(isA(String[].class), isA(ClassLoader.class))).thenReturn(success);
        BeftaMain.setUp();
        BeftaMain.runCucumberMain(args);
        assertEquals(TestAutomationConfig.INSTANCE, BeftaMain.getConfig());
        assertNotNull(BeftaMain.getAdapter());
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = TEST_DATA_LOAD_SKIP_PERIOD_KEY, value = TEST_DATA_LOAD_SKIP_PERIOD_VALUE)
    void testsetUpMain() {
        BeftaMain.setUp();
        assertEquals(TestAutomationConfig.INSTANCE, BeftaMain.getConfig());
        assertNotNull(BeftaMain.getAdapter());
    }

}
