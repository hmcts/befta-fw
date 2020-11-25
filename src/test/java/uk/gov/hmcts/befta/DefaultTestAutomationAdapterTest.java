/**
 * 
 */
package uk.gov.hmcts.befta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.Matchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;

import uk.gov.hmcts.befta.auth.AuthApi;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.RequestData;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.factory.BeftaIdamApiClientFactory;
import uk.gov.hmcts.befta.factory.BeftaServiceAuthorisationApiClientFactory;
import uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

/**
 * @author korneleehenry
 *
 */
@SuppressWarnings("deprecation")
class DefaultTestAutomationAdapterTest {
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
    private DefaultTestAutomationAdapter tad = null;
    private MockedStatic<BeftaServiceAuthorisationApiClientFactory> beftaServiceapi = null;
    private MockedStatic<BeftaIdamApiClientFactory> beftaIdamapi = null;

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.DefaultTestAutomationAdapter#DefaultTestAutomationAdapter()}.
     */
    @BeforeEach
    void setup() {
        try {
            beftaServiceapi = mockStatic(BeftaServiceAuthorisationApiClientFactory.class);
            beftaIdamapi = mockStatic(BeftaIdamApiClientFactory.class);
            ServiceAuthorisationApi serviceAuthorisationApi = mock(ServiceAuthorisationApi.class);
            AuthApi idamApi = mock(AuthApi.class);
            AuthApi.User idamUser = new AuthApi.User();
            AuthApi.TokenExchangeResponse idamtockenExch = new AuthApi.TokenExchangeResponse();
            ObjectMapper mapper = new ObjectMapper();
            String json = "{\"code\" : \"abc\"}";
            AuthApi.AuthenticateUserResponse auresponse = mapper.readerFor(AuthApi.AuthenticateUserResponse.class)
                    .readValue(json);

            Mockito.when(idamApi.getUser(null)).thenReturn(idamUser);
            Mockito.when(idamApi.generateOIDCToken(isA(String.class), isA(String.class), isA(String.class),
                    isA(String.class), isA(String.class), isA(String.class))).thenReturn(idamtockenExch);
            Mockito.when(idamApi.authenticateUser(isA(String.class), isA(String.class), isA(String.class),
                    isA(String.class))).thenReturn(auresponse);
            Mockito.when(idamApi.exchangeCode(isA(String.class), isA(String.class), isA(String.class),
                    isA(String.class), isA(String.class))).thenReturn(idamtockenExch);

            Mockito.when(serviceAuthorisationApi.serviceToken(Matchers.anyMapOf(String.class, String.class)))
                    .thenReturn("adad");
            Mockito.when(BeftaServiceAuthorisationApiClientFactory.createServiceAuthorisationApiClient())
                    .thenReturn(serviceAuthorisationApi);
            Mockito.when(BeftaIdamApiClientFactory.createAuthorizationClient()).thenReturn(idamApi);
            tad = new DefaultTestAutomationAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void cleanUp() {
        try {
            beftaServiceapi.close();
            beftaIdamapi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.DefaultTestAutomationAdapter#getNewS2SToken()}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testGetNewS2SToken() {
//        HashMap<String, String> map = new HashMap<String, String>() {
//            private static final long serialVersionUID = 1L;
//            {
//                put("invokingUser", "userData");
//            }
//        };

        assertNotNull(tad);
        assertEquals("adad", tad.getNewS2SToken());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.DefaultTestAutomationAdapter#getNewS2SToken(java.lang.String)}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = "BEFTA_S2S_REDIRECT_URI_OF_OTHER", value = "BEFTA_S2S_REDIRECT_URI_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_S2S_CLIENT_SECRET_OF_OTHER", value = "BEFTA_S2S_CLIENT_SECRET_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_S2S_SCOPE_VARIABLES_OF_OTHER", value = "BEFTA_S2S_SCOPE_VARIABLES_OF_OTHER_VALUE")
    void testGetNewS2STokenString() {
        assertNotNull(tad);
        assertEquals("adad", tad.getNewS2SToken("OTHER"));
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.DefaultTestAutomationAdapter#authenticate(uk.gov.hmcts.befta.data.UserData, java.lang.String)}.
     */
    @Test
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_REDIRECT_URI_OF_OTHER", value = "BEFTA_OAUTH2_REDIRECT_URI_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_CLIENT_SECRET_OF_OTHER", value = "BEFTA_OAUTH2_CLIENT_SECRET_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_SCOPE_VARIABLES_OF_OTHER", value = "BEFTA_OAUTH2_SCOPE_VARIABLES_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_ACCESS_TOKEN_TYPE_OF_OTHER", value = "OIDC")
    void testAuthenticate() {
        UserData user = new UserData("user", "pwd");
        user.setId("id");
        String userTokenClientId = "OTHER";
        assertNotNull(tad);
        tad.authenticate(user, userTokenClientId);
    }

    @Test
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_REDIRECT_URI_OF_OTHER", value = "BEFTA_OAUTH2_REDIRECT_URI_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_CLIENT_SECRET_OF_OTHER", value = "BEFTA_OAUTH2_CLIENT_SECRET_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_SCOPE_VARIABLES_OF_OTHER", value = "BEFTA_OAUTH2_SCOPE_VARIABLES_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_OAUTH2_ACCESS_TOKEN_TYPE_OF_OTHER", value = "OUTH2")
    void testAuthenticateOuth2() {
        UserData user = new UserData("user", "pwd");
        user.setId("id");
        String userTokenClientId = "OTHER";
        assertNotNull(tad);
//		AuthApi idamApi = mock(AuthApi.class);
//		AuthApi.User idamUser = new AuthApi.User();
//		AuthApi.TokenExchangeResponse idamtockenExch = new AuthApi.TokenExchangeResponse ();
//		AuthApi.AuthenticateUserResponse auresponse = new AuthApi.AuthenticateUserResponse();
//        Mockito.when(idamApi.getUser(null)).thenReturn(idamUser);
//        Mockito.when(idamApi.generateOIDCToken(isA(String.class), isA(String.class), isA(String.class), isA(String.class), isA(String.class), isA(String.class))).thenReturn(idamtockenExch);
//        Mockito.when(idamApi.authenticateUser(isA(String.class), isA(String.class), isA(String.class), isA(String.class))).thenReturn(auresponse);
//        Mockito.when(idamApi.exchangeCode(isA(String.class), isA(String.class), isA(String.class), isA(String.class), isA(String.class))).thenReturn(idamtockenExch);

        tad.authenticate(user, userTokenClientId);
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.DefaultTestAutomationAdapter#loadTestDataIfNecessary()}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = TEST_DATA_LOAD_SKIP_PERIOD_KEY, value = TEST_DATA_LOAD_SKIP_PERIOD_VALUE)
    void testLoadTestDataIfNecessary() {
        assertNotNull(tad);
        assertFalse(tad.getDataLoader().isTestDataLoadedForCurrentRound());
        new File(TestAutomationAdapter.EXECUTION_INFO_FILE).delete();
        tad.getDataLoader().loadTestDataIfNecessary();
        new File(TestAutomationAdapter.EXECUTION_INFO_FILE).deleteOnExit();
        assertTrue(tad.getDataLoader().isTestDataLoadedForCurrentRound());
        tad.getDataLoader().loadTestDataIfNecessary();
        assertTrue(tad.getDataLoader().isTestDataLoadedForCurrentRound());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.DefaultTestAutomationAdapter#getNewS2sClient(java.lang.String)}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = "BEFTA_S2S_REDIRECT_URI_OF_OTHER", value = "BEFTA_S2S_REDIRECT_URI_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_S2S_CLIENT_SECRET_OF_OTHER", value = "BEFTA_S2S_CLIENT_SECRET_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_S2S_SCOPE_VARIABLES_OF_OTHER", value = "BEFTA_S2S_SCOPE_VARIABLES_OF_OTHER_VALUE")
    void testGetNewS2sClient() {
        String s2sClientId = "OTHER";
        assertNotNull(tad);
        assertNotNull(tad.getNewS2sClient(s2sClientId));
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.DefaultTestAutomationAdapter#getNewS2sClientWithCredentials(java.lang.String, java.lang.String)}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = "BEFTA_S2S_REDIRECT_URI_OF_OTHER", value = "BEFTA_S2S_REDIRECT_URI_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_S2S_CLIENT_SECRET_OF_OTHER", value = "BEFTA_S2S_CLIENT_SECRET_OF_OTHER_VALUE")
    @SetEnvironmentVariable(key = "BEFTA_S2S_SCOPE_VARIABLES_OF_OTHER", value = "BEFTA_S2S_SCOPE_VARIABLES_OF_OTHER_VALUE")
    void testGetNewS2sClientWithCredentials() {
        String s2sClientId = "OTHER";
        assertNotNull(tad);
        assertNotNull(tad.getNewS2sClientWithCredentials(s2sClientId, "BEFTA_S2S_CLIENT_SECRET_OF_OTHER_VALUE"));
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.DefaultTestAutomationAdapter#calculateCustomValue(uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext, java.lang.Object)}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueNull() {
        BackEndFunctionalTestScenarioContext contextUnderTest = new BackEndFunctionalTestScenarioContext();
        assertNotNull(tad);
        assertEquals(null, tad.calculateCustomValue(contextUnderTest, null));
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.DefaultTestAutomationAdapter#calculateCustomValue(uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext, java.lang.Object)}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequest() {
        String key = "request";
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getRequest()).thenReturn(new RequestData());
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.DefaultTestAutomationAdapter#calculateCustomValue(uk.gov.hmcts.befta.player.BackEndFunctionalTestScenarioContext, java.lang.Object)}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestBody() {
        String key = "requestbody";
        RequestData request = mock(RequestData.class);
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getRequest()).thenReturn(request);
        when(request.getBody()).thenReturn(new HashMap<String, Object>());
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestHeaders() {
        String key = "requestheaders";
        RequestData request = mock(RequestData.class);
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(new HashMap<String, Object>());
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestPaths() {
        String key = "requestpathvars";
        RequestData request = mock(RequestData.class);
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getRequest()).thenReturn(request);
        when(request.getPathVariables()).thenReturn(new HashMap<String, Object>());
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestQuery() {
        String key = "requestqueryparams";
        RequestData request = mock(RequestData.class);
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getRequest()).thenReturn(request);
        when(request.getQueryParams()).thenReturn(new HashMap<String, Object>());
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestExpectedresponse() {
        String key = "expectedresponse";
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getExpectedResponse()).thenReturn(new ResponseData());
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestExpectedresponseHeaders() {
        String key = "expectedresponseheaders";
        ResponseData response = mock(ResponseData.class);
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getExpectedResponse()).thenReturn(response);
        when(response.getHeaders()).thenReturn(new HashMap<String, Object>());
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestExpectedresponseBody() {
        String key = "expectedresponsebody";
        ResponseData response = mock(ResponseData.class);
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getExpectedResponse()).thenReturn(response);
        when(response.getBody()).thenReturn(new HashMap<String, Object>());
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestActualresponse() {
        String key = "actualresponse";
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getActualResponse()).thenReturn(new ResponseData());
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestActualresponseHeaders() {
        String key = "actualresponseheaders";
        ResponseData response = mock(ResponseData.class);
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getActualResponse()).thenReturn(response);
        when(response.getHeaders()).thenReturn(new HashMap<String, Object>());
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestActualresponseBody() {
        String key = "actualresponsebody";
        ResponseData response = mock(ResponseData.class);
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.getActualResponse()).thenReturn(response);
        when(response.getBody()).thenReturn(new HashMap<String, Object>());
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestedToken() {
        String key = "tokenvaluefromaccompanyingtokencall";
        HttpTestData testData = mock(HttpTestData.class);
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        when(context.getTestData()).thenReturn(testData);
        when(testData.get_guid_()).thenReturn("1");
        assertNotNull(tad);
        Assertions.assertThrows(FunctionalTestException.class, () -> {
            assertNotNull(tad.calculateCustomValue(context, key));
        });
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testCalculateCustomValueRequestedToday() {
        String key = "today";
        BackEndFunctionalTestScenarioContext context = mock(BackEndFunctionalTestScenarioContext.class);
        assertNotNull(tad);
        assertNotNull(tad.calculateCustomValue(context, key));
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.DefaultTestAutomationAdapter#getDateTimeFormatRequested(java.lang.String)}.
     */
    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testGetDateTimeFormatRequestedToday() {
        assertNotNull(tad);
        String key = "today";
        String keyValue = "yyyy-MM-dd";
        assertEquals(keyValue, tad.getDateTimeFormatRequested(key));

    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testGetDateTimeFormatRequestedNow() {
        assertNotNull(tad);
        String key = "now";
        String keyValue = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        assertEquals(keyValue, tad.getDateTimeFormatRequested(key));

    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testGetDateTimeFormatRequestedNowSub() {
        assertNotNull(tad);
        String key = "now(yyyy-MM-dd'T'HH:mm:ss.SSS)";
        String keyValue = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        assertEquals(keyValue, tad.getDateTimeFormatRequested(key));

    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testGetDateTimeFormatRequestedNull() {
        assertNotNull(tad);
        String key = "";
        assertNull(tad.getDateTimeFormatRequested(key));

    }

}
