/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;
import static io.restassured.RestAssured.*;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;
import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.TestAutomationAdapter;
import uk.gov.hmcts.befta.auth.AuthApi;

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
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#addCcdRoles()}.
	 */
	@Disabled
	@Test
	void testAddCcdRoles() {
		fail("Not yet implemented");
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
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#addCcdRole(uk.gov.hmcts.befta.dse.ccd.CcdRoleConfig)}.
	 */
	@Disabled
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
		AuthApi mockAuth = mock(AuthApi.class);
//        when(testData.meetsSpec(any())).thenReturn(true);
		TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
		AuthApi.TokenExchangeResponse mockVal= new AuthApi.TokenExchangeResponse();
		RestAssured mockTd = mock(RestAssured.class);
		RequestSpecification mockrsp = mock(RequestSpecification.class);
		Response rs = mock(Response.class);
		when(mockTd.put()).thenReturn(rs);
		when(mockAdapter.getNewS2SToken()).thenReturn("s2s_token");
		CcdRoleConfig roleConfig = new CcdRoleConfig("caseworker-autotest1", "PUBLIC");
		DefaultTestAutomationAdapter defaultTestAutomationAdapter = new DefaultTestAutomationAdapter();
		TestDataLoaderToDefinitionStore testDataLoaderToDefinitionStore = new TestDataLoaderToDefinitionStore(mockAdapter);
		testDataLoaderToDefinitionStore.addCcdRole(roleConfig);
		assertNotNull(testDataLoaderToDefinitionStore);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#getAllDefinitionFilesToLoad()}.
	 */
	@Disabled
	@Test
	void testGetAllDefinitionFilesToLoad() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.TestDataLoaderToDefinitionStore#importDefinition(java.lang.String)}.
	 */
	@Disabled
	@Test
	void testImportDefinition() {
		fail("Not yet implemented");
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
