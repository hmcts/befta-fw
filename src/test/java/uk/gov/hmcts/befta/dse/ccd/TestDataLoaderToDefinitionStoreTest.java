/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;

import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;

/**
 * @author korneleehenry
 *
 */
class TestDataLoaderToDefinitionStoreTest {
	public static final String DEFINITION_STORE_HOST_KEY = "DEFINITION_STORE_HOST";
	public static final String DEFINITION_STORE_HOST_VALUE = "DEFINITION_STORE_HOST_VALUE";
	public static final String BEFTA_S2S_CLIENT_ID_KEY = "BEFTA_S2S_CLIENT_ID";
	public static final String BEFTA_S2S_CLIENT_ID_VALUE = "BEFTA_S2S_CLIENT_ID_VALUE";
	public static final String IDAM_URL_KEY = "IDAM_URL";
	public static final String IDAM_URL_VALUE = "IDAM_URL_VALUE";
	public static final String BEFTA_S2S_CLIENT_SECRET_KEY = "BEFTA_S2S_CLIENT_SECRET";
	public static final String BEFTA_S2S_CLIENT_SECRET_VALUE = "BEFTA_S2S_CLIENT_SECRET_VALUE";
	public static final String S2S_URL_KEY = "S2S_URL";
	public static final String S2S_URL_VALUE = "S2S_URL_VALUE";
	public static final String DEFAULT_DEFINITIONS_PATH_JSON = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid";

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
	void testAddCcdRole() {
		fail("Not yet implemented");
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
