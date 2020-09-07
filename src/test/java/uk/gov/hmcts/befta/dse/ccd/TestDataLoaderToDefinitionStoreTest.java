/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.TestAutomationAdapter;
import uk.gov.hmcts.befta.exception.FunctionalTestException;

/**
 * @author korneleehenry
 *
 */
class TestDataLoaderToDefinitionStoreTest {
	public static final String DEFINITION_STORE_HOST_KEY = "DEFINITION_STORE_HOST";
	public static final String DEFINITION_STORE_HOST_VALUE = "http://127.0.0.1:8089/";
	public static final String BEFTA_S2S_CLIENT_ID_KEY = "BEFTA_S2S_CLIENT_ID";
	public static final String BEFTA_S2S_CLIENT_ID_VALUE = "BEFTA_S2S_CLIENT_ID_VALUE";
	public static final String IDAM_URL_KEY = "IDAM_URL";
	public static final String IDAM_URL_VALUE = "IDAM_URL_VALUE";
	public static final String BEFTA_S2S_CLIENT_SECRET_KEY = "BEFTA_S2S_CLIENT_SECRET";
	public static final String BEFTA_S2S_CLIENT_SECRET_VALUE = "BEFTA_S2S_CLIENT_SECRET_VALUE";
	public static final String S2S_URL_KEY = "S2S_URL";
	public static final String S2S_URL_VALUE = "S2S_URL_VALUE";
	public static final String DEFAULT_DEFINITIONS_PATH_JSON = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid";
	public static final String CCD_IMPORT_AUTOTEST_EMAIL = "CCD_IMPORT_AUTOTEST_EMAIL";
	public static final String CCD_IMPORT_AUTOTEST_EMAIL_VALUE = "CCD_IMPORT_AUTOTEST_EMAIL_VALUE";
	public static final String CCD_IMPORT_AUTOTEST_PASSWORD = "CCD_IMPORT_AUTOTEST_PASSWORD";
	public static final String CCD_IMPORT_AUTOTEST_PASSWORD_VALUE = "CCD_IMPORT_AUTOTEST_PASSWORD_VALUE";
    private MockedStatic<RestAssured> restAssuredMock = null; 
    @BeforeEach
    public void prepareMockedObjectUnderTest() {
        try {
        	restAssuredMock = mockStatic(RestAssured.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @AfterEach
    public void closeMockedObjectUnderTest() {
        try {
        	restAssuredMock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }	
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#TestDataLoaderToDefinitionStore(uk.gov.hmcts.befta.TestAutomationAdapter)}.
	 */
	@Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
	void testTestDataLoaderToDefinitionStoreTestAutomationAdapter() {
		DefaultTestAutomationAdapter defaultTestAutomationAdapter = new DefaultTestAutomationAdapter();
		TestDataLoaderToDefinitionStore testDataLoaderToDefinitionStore = new TestDataLoaderToDefinitionStore(defaultTestAutomationAdapter);
		assertNotNull(testDataLoaderToDefinitionStore);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#TestDataLoaderToDefinitionStore(uk.gov.hmcts.befta.TestAutomationAdapter, java.lang.String, java.lang.String)}.
	 */
	@Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
	void testTestDataLoaderToDefinitionStoreTestAutomationAdapterStringString() {
		DefaultTestAutomationAdapter defaultTestAutomationAdapter = new DefaultTestAutomationAdapter();
		TestDataLoaderToDefinitionStore testDataLoaderToDefinitionStore = new TestDataLoaderToDefinitionStore(defaultTestAutomationAdapter,DEFAULT_DEFINITIONS_PATH_JSON,S2S_URL_VALUE);
		assertNotNull(testDataLoaderToDefinitionStore);
	}


	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#importDefinitions()}.
	 */
	@Disabled
	@Test
	void testImportDefinitions() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#addCcdRoles()}.
	 */
	@Test	
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_EMAIL, value = CCD_IMPORT_AUTOTEST_EMAIL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_PASSWORD, value = CCD_IMPORT_AUTOTEST_PASSWORD_VALUE)
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
	void testAddCcdRoles() {
		TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
		RequestSpecification requestSpecification = mock (RequestSpecification.class);
		Response rs = mock(io.restassured.response.Response.class);

		when(mockAdapter.getNewS2SToken()).thenReturn("s2s_token");
		CcdRoleConfig roleConfig = new CcdRoleConfig("caseworker-autotest1", "PUBLIC");
		TestDataLoaderToDefinitionStore testDataLoaderToDefinitionStore = new TestDataLoaderToDefinitionStore(mockAdapter);
		ResponseBody responseBody = mock(io.restassured.response.ResponseBody.class);
        when(RestAssured.given(any())).thenReturn(requestSpecification);
        when(requestSpecification.header(any(), any(), ArgumentMatchers.<String>any())).thenReturn(requestSpecification);
        when(requestSpecification.given()).thenReturn(requestSpecification);
        when(requestSpecification.body(any(Object.class))).thenReturn(requestSpecification);
        when(requestSpecification.when()).thenReturn(requestSpecification);
        when(requestSpecification.put("/api/user-role")).thenReturn(rs);
        when(rs.getStatusCode()).thenReturn(200);
		assertNotNull(testDataLoaderToDefinitionStore);
		testDataLoaderToDefinitionStore.addCcdRoles();
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#addCcdRole(uk.gov.hmcts.befta.dse.ccd.CcdRoleConfig)}.
	 */
	@Test	
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_EMAIL, value = CCD_IMPORT_AUTOTEST_EMAIL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_PASSWORD, value = CCD_IMPORT_AUTOTEST_PASSWORD_VALUE)
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
	void testAddCcdRole() {
		TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
		RequestSpecification requestSpecification = mock (RequestSpecification.class);
		Response rs = mock(io.restassured.response.Response.class);

		when(mockAdapter.getNewS2SToken()).thenReturn("s2s_token");
		CcdRoleConfig roleConfig = new CcdRoleConfig("caseworker-autotest1", "PUBLIC");
		TestDataLoaderToDefinitionStore testDataLoaderToDefinitionStore = new TestDataLoaderToDefinitionStore(mockAdapter);
		ResponseBody responseBody = mock(io.restassured.response.ResponseBody.class);
        when(RestAssured.given(any())).thenReturn(requestSpecification);
        when(requestSpecification.header(any(), any(), ArgumentMatchers.<String>any())).thenReturn(requestSpecification);
        when(requestSpecification.given()).thenReturn(requestSpecification);
        when(requestSpecification.body(any(Object.class))).thenReturn(requestSpecification);
        when(requestSpecification.when()).thenReturn(requestSpecification);
        when(requestSpecification.put("/api/user-role")).thenReturn(rs);
        when(rs.getStatusCode()).thenReturn(200);
		assertNotNull(testDataLoaderToDefinitionStore);
		testDataLoaderToDefinitionStore.addCcdRole(roleConfig);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#addCcdRole(uk.gov.hmcts.befta.dse.ccd.CcdRoleConfig)}.
	 */
	@Test	
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_EMAIL, value = CCD_IMPORT_AUTOTEST_EMAIL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_PASSWORD, value = CCD_IMPORT_AUTOTEST_PASSWORD_VALUE)
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
	void testAddCcdRoleException() {
		TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
		RequestSpecification requestSpecification = mock (RequestSpecification.class);
		Response rs = mock(io.restassured.response.Response.class);

		when(mockAdapter.getNewS2SToken()).thenReturn("s2s_token");
		CcdRoleConfig roleConfig = new CcdRoleConfig("caseworker-autotest1", "PUBLIC");
		TestDataLoaderToDefinitionStore testDataLoaderToDefinitionStore = new TestDataLoaderToDefinitionStore(mockAdapter);
		ResponseBody responseBody = mock(io.restassured.response.ResponseBody.class);
        when(RestAssured.given(any())).thenReturn(requestSpecification);
        when(requestSpecification.header(any(), any(), ArgumentMatchers.<String>any())).thenReturn(requestSpecification);
        when(requestSpecification.given()).thenReturn(requestSpecification);
        when(requestSpecification.body(any(Object.class))).thenReturn(requestSpecification);
        when(requestSpecification.when()).thenReturn(requestSpecification);
        when(requestSpecification.put("/api/user-role")).thenReturn(rs);
        when(rs.body()).thenReturn(responseBody);;
        when(responseBody.prettyPrint()).thenReturn("");
		assertNotNull(testDataLoaderToDefinitionStore);
        Assertions.assertThrows(FunctionalTestException.class, () -> {
    		testDataLoaderToDefinitionStore.addCcdRole(roleConfig);
          });
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#getAllDefinitionFilesToLoad()}.
	 */
	@Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_EMAIL, value = CCD_IMPORT_AUTOTEST_EMAIL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_PASSWORD, value = CCD_IMPORT_AUTOTEST_PASSWORD_VALUE)
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "OAUTH2_REDIRECT_URI", value = "OAUTH2_REDIRECT_URI_VALUE")
	void testGetAllDefinitionFilesToLoad() {
		DefaultTestAutomationAdapter defaultTestAutomationAdapter = new DefaultTestAutomationAdapter();
		TestDataLoaderToDefinitionStore testDataLoaderToDefinitionStore = new TestDataLoaderToDefinitionStore(defaultTestAutomationAdapter);
		testDataLoaderToDefinitionStore.getAllDefinitionFilesToLoad();
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#importDefinition(java.lang.String)}.
	 */
	@Disabled
	@Test	
	void testImportDefinition() {
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#asAutoTestImporter()}.
	 */
	@Disabled
	@Test
	void testAsAutoTestImporter() {
		fail("Not yet implemented");
	}

}
