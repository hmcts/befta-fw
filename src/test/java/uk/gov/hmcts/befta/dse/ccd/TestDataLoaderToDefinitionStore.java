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
import org.mockito.MockedStatic;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.TestAutomationAdapter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"LineLength","VariableDeclarationUsageDistance"})
class TestDataLoaderToDefinitionStore {

    public static final String DEFINITION_STORE_HOST_KEY = "DEFINITION_STORE_URL_BASE";
    public static final String DEFINITION_STORE_HOST_VALUE = "http://127.0.0.1:8089/";
    public static final String BEFTA_S2S_CLIENT_ID_KEY = "BEFTA_S2S_CLIENT_ID";
    public static final String BEFTA_S2S_CLIENT_ID_VALUE = "BEFTA_S2S_CLIENT_ID_VALUE";
    public static final String IDAM_URL_KEY = "IDAM_API_URL_BASE";
    public static final String IDAM_URL_VALUE = "IDAM_URL_VALUE";
    public static final String BEFTA_S2S_CLIENT_SECRET_KEY = "BEFTA_S2S_CLIENT_SECRET";
    public static final String BEFTA_S2S_CLIENT_SECRET_VALUE = "BEFTA_S2S_CLIENT_SECRET_VALUE";
    public static final String S2S_URL_KEY = "S2S_URL_BASE";
    public static final String S2S_URL_VALUE = "S2S_URL_VALUE";
    public static final String CCD_IMPORT_AUTOTEST_EMAIL = "DEFINITION_IMPORTER_USERNAME";
    public static final String CCD_IMPORT_AUTOTEST_EMAIL_VALUE = "CCD_IMPORT_AUTOTEST_EMAIL_VALUE";
    public static final String CCD_IMPORT_AUTOTEST_PASSWORD = "DEFINITION_IMPORTER_PASSWORD";
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

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testTestDataLoaderToDefinitionStoreTestAutomationAdapter() {
        DefaultTestAutomationAdapter defaultTestAutomationAdapter = new DefaultTestAutomationAdapter();
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(defaultTestAutomationAdapter);
        assertNotNull(dataLoaderToDefinitionStore);
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    void testTestDataLoaderToDefinitionStoreTestAutomationAdapterStringString() {
        DefaultTestAutomationAdapter defaultTestAutomationAdapter = new DefaultTestAutomationAdapter();
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(defaultTestAutomationAdapter,S2S_URL_VALUE);
        assertNotNull(dataLoaderToDefinitionStore);
    }


    @Disabled("Not yet implemented")
    @Test
    void testImportDefinitions() {
        fail("Not yet implemented");
    }

    @ParameterizedTest(name = "testAddCcdRoles: definitionsPath: {0}, numberOfAddCcdRoleInvocations: {1}")
    @MethodSource("addCcdRoleParams")
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_EMAIL, value = CCD_IMPORT_AUTOTEST_EMAIL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_PASSWORD, value = CCD_IMPORT_AUTOTEST_PASSWORD_VALUE)
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_REDIRECT_URL", value = "OAUTH2_REDIRECT_URI_VALUE")
    void testAddCcdRoles(String definitionsPath, int numberOfAddCcdRoleInvocations)  {

        // GIVEN
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        when(mockAdapter.getNewS2SToken()).thenReturn("s2s_token");

        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);

        mockAddCcdRoleApiCalls(requestSpecification, rs);
        when(rs.getStatusCode()).thenReturn(200);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore
                = new DataLoaderToDefinitionStore(mockAdapter, definitionsPath);

        // WHEN
        dataLoaderToDefinitionStore.addCcdRoles();

        // THEN
        verify(requestSpecification, times(numberOfAddCcdRoleInvocations)).put("/api/user-role");
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_EMAIL, value = CCD_IMPORT_AUTOTEST_EMAIL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_PASSWORD, value = CCD_IMPORT_AUTOTEST_PASSWORD_VALUE)
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_REDIRECT_URL", value = "OAUTH2_REDIRECT_URI_VALUE")
    void testAddCcdRole() {

        // GIVEN
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        when(mockAdapter.getNewS2SToken()).thenReturn("s2s_token");

        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);

        mockAddCcdRoleApiCalls(requestSpecification, rs);
        when(rs.getStatusCode()).thenReturn(200);

        CcdRoleConfig roleConfig = new CcdRoleConfig("caseworker-autotest1", "PUBLIC");
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(mockAdapter);

        // WHEN
        dataLoaderToDefinitionStore.addCcdRole(roleConfig);

        // THEN
        verify(requestSpecification, times(1)).put("/api/user-role");
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_EMAIL, value = CCD_IMPORT_AUTOTEST_EMAIL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_PASSWORD, value = CCD_IMPORT_AUTOTEST_PASSWORD_VALUE)
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_REDIRECT_URL", value = "OAUTH2_REDIRECT_URI_VALUE")
    void testAddCcdRolesException() {

        // GIVEN
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        when(mockAdapter.getNewS2SToken()).thenReturn("s2s_token");

        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);

        mockAddCcdRoleApiCalls(requestSpecification, rs);
        when(rs.getStatusCode()).thenReturn(500);
        ResponseBody<?> responseBody = mock(io.restassured.response.ResponseBody.class);
        when(rs.body()).thenReturn(responseBody);
        when(responseBody.prettyPrint()).thenReturn("");

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(mockAdapter);

        // WHEN / THEN
        Assertions.assertThrows(RuntimeException.class, dataLoaderToDefinitionStore::addCcdRoles);
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_EMAIL, value = CCD_IMPORT_AUTOTEST_EMAIL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_PASSWORD, value = CCD_IMPORT_AUTOTEST_PASSWORD_VALUE)
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_REDIRECT_URL", value = "OAUTH2_REDIRECT_URI_VALUE")
    void testAddCcdRoleException() {

        // GIVEN
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        when(mockAdapter.getNewS2SToken()).thenReturn("s2s_token");

        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);

        mockAddCcdRoleApiCalls(requestSpecification, rs);
        when(rs.getStatusCode()).thenReturn(500);
        ResponseBody<?> responseBody = mock(io.restassured.response.ResponseBody.class);
        when(rs.body()).thenReturn(responseBody);
        when(responseBody.prettyPrint()).thenReturn("");

        CcdRoleConfig roleConfig = new CcdRoleConfig("caseworker-autotest1", "PUBLIC");
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(mockAdapter);

        // WHEN / THEN
        Assertions.assertThrows(RuntimeException.class, () -> dataLoaderToDefinitionStore.addCcdRole(roleConfig));
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = IDAM_URL_KEY, value = IDAM_URL_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_ID_KEY, value = BEFTA_S2S_CLIENT_ID_VALUE)
    @SetEnvironmentVariable(key = BEFTA_S2S_CLIENT_SECRET_KEY, value = BEFTA_S2S_CLIENT_SECRET_VALUE)
    @SetEnvironmentVariable(key = S2S_URL_KEY, value = S2S_URL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_EMAIL, value = CCD_IMPORT_AUTOTEST_EMAIL_VALUE)
    @SetEnvironmentVariable(key = CCD_IMPORT_AUTOTEST_PASSWORD, value = CCD_IMPORT_AUTOTEST_PASSWORD_VALUE)
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_ID", value = "OAUTH2_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_CLIENT_SECRET", value = "OAUTH2_CLIENT_SECRET_VALUE")
    @SetEnvironmentVariable(key = "CCD_API_GATEWAY_OAUTH2_REDIRECT_URL", value = "OAUTH2_REDIRECT_URI_VALUE")
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
    void testGetAllDefinitionFilesToLoad() {
        DefaultTestAutomationAdapter defaultTestAutomationAdapter = new DefaultTestAutomationAdapter();
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(defaultTestAutomationAdapter);
        List<String> files = dataLoaderToDefinitionStore.getAllDefinitionFilesToLoadAt(
                DataLoaderToDefinitionStore.VALID_CCD_TEST_DEFINITIONS_PATH
        );
        assertNotNull(files);
    }

    @Disabled("Not yet implemented")
    @Test
    void testImportDefinition() {
        fail("Not yet implemented");
    }

    @Disabled("Not yet implemented")
    @Test
    void testAsAutoTestImporter() {
        fail("Not yet implemented");
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_API_GATEWAY_S2S_CLIENT_ID", value = "ROLE_ASSIGNMENT_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_API_GATEWAY_S2S_CLIENT_KEY", value = "ROLE_ASSIGNMENT_CLIENT_KEY_VALUE")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_USER_EMAIL", value = "ROLE_ASSIGNMENT_USER_EMAIL")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_USER_PASSWORD", value = "ROLE_ASSIGNMENT_USER_PASSWORD")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_HOST", value = "ROLE_ASSIGNMENT_HOST")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_SOLICITOR_USER", value = "ROLE_ASSIGNMENT_SOLICITOR_USER")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_SOLICITOR_USER_PWD", value = "ROLE_ASSIGNMENT_SOLICITOR_USER_PWD")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_SUPER_USER", value = "ROLE_ASSIGNMENT_SUPER_USER")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_SUPER_USER_PWD", value = "ROLE_ASSIGNMENT_SUPER_USER_PWD")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_STAFF1_USER", value = "ROLE_ASSIGNMENT_STAFF1_USER")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_STAFF1_USER_PWD", value = "ROLE_ASSIGNMENT_STAFF1_USER_PWD")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_STAFF2_USER", value = "ROLE_ASSIGNMENT_STAFF2_USER")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_STAFF2_USER_PWD", value = "ROLE_ASSIGNMENT_STAFF2_USER_PWD")
       void testCreateRoleAssignments() {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        when(mockAdapter.getNewS2SToken()).thenReturn("s2s_token");
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(mockAdapter);
        when(RestAssured.given(any())).thenReturn(requestSpecification);
        when(requestSpecification.header(any())).thenReturn(requestSpecification);
        when(requestSpecification.given()).thenReturn(requestSpecification);
        when(requestSpecification.body(any(String.class))).thenReturn(requestSpecification);
        when(requestSpecification.when()).thenReturn(requestSpecification);
        when(requestSpecification.post("/am/role-assignments")).thenReturn(rs);
        when(rs.getStatusCode()).thenReturn(200);
        assertNotNull(dataLoaderToDefinitionStore);
        dataLoaderToDefinitionStore.createRoleAssignments();
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_API_GATEWAY_S2S_CLIENT_ID", value = "ROLE_ASSIGNMENT_CLIENT_ID_VALUE")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_API_GATEWAY_S2S_CLIENT_KEY", value = "ROLE_ASSIGNMENT_CLIENT_KEY_VALUE")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_USER_EMAIL", value = "ROLE_ASSIGNMENT_USER_EMAIL")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_USER_PASSWORD", value = "ROLE_ASSIGNMENT_USER_PASSWORD")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_HOST", value = "ROLE_ASSIGNMENT_HOST")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_SOLICITOR_USER", value = "ROLE_ASSIGNMENT_SOLICITOR_USER")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_SOLICITOR_USER_PWD", value = "ROLE_ASSIGNMENT_SOLICITOR_USER_PWD")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_SUPER_USER", value = "ROLE_ASSIGNMENT_SUPER_USER")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_SUPER_USER_PWD", value = "ROLE_ASSIGNMENT_SUPER_USER_PWD")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_STAFF1_USER", value = "ROLE_ASSIGNMENT_STAFF1_USER")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_STAFF1_USER_PWD", value = "ROLE_ASSIGNMENT_STAFF1_USER_PWD")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_STAFF2_USER", value = "ROLE_ASSIGNMENT_STAFF2_USER")
    @SetEnvironmentVariable(key = "ROLE_ASSIGNMENT_STAFF2_USER_PWD", value = "ROLE_ASSIGNMENT_STAFF2_USER_PWD")
    void testCreateRoleAssignmentException() {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        when(mockAdapter.getNewS2SToken()).thenReturn("s2s_token");
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(mockAdapter);
        ResponseBody<?> responseBody = mock(io.restassured.response.ResponseBody.class);
        when(RestAssured.given(any())).thenReturn(requestSpecification);
        when(requestSpecification.header(any(), any(), ArgumentMatchers.<String>any())).thenReturn(requestSpecification);
        when(requestSpecification.given()).thenReturn(requestSpecification);
        when(requestSpecification.body(any(String.class))).thenReturn(requestSpecification);
        when(requestSpecification.when()).thenReturn(requestSpecification);
        when(requestSpecification.post("/am/role-assignments")).thenReturn(rs);
        when(rs.body()).thenReturn(responseBody);
        when(responseBody.prettyPrint()).thenReturn("");
        assertNotNull(dataLoaderToDefinitionStore);
        Assertions.assertThrows(RuntimeException.class, () ->
                dataLoaderToDefinitionStore.createRoleAssignment("resource", "filename")
        );
    }

    private void mockAddCcdRoleApiCalls(RequestSpecification requestSpecification, Response rs) {
        when(RestAssured.given(any())).thenReturn(requestSpecification);
        when(requestSpecification.header(any())).thenReturn(requestSpecification);
        when(requestSpecification.given()).thenReturn(requestSpecification);
        when(requestSpecification.body(any(Object.class))).thenReturn(requestSpecification);
        when(requestSpecification.when()).thenReturn(requestSpecification);
        when(requestSpecification.put("/api/user-role")).thenReturn(rs);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> addCcdRoleParams() {

        final int numberOfCcdRolesInDefaultList = 29; // see DataLoaderToDefinitionStore.CCD_ROLES_NEEDED_FOR_TA

        return Stream.of(
                // NB: params correspond to:
                // * definitionsPath :: path to test files in /src/test/resources/
                // * numberOfAddCcdRoleInvocations :: number of expected calls to Add CCD Role mack
                Arguments.of(null, numberOfCcdRolesInDefaultList), // no definition files (i.e. null)
                Arguments.of("", numberOfCcdRolesInDefaultList), // no definition files (i.e. empty)
                Arguments.of("ccd-roles-test-data/with-ccd-roles/definitions", 3),
                Arguments.of("ccd-roles-test-data/without-ccd-roles/definitions", numberOfCcdRolesInDefaultList),
                Arguments.of("ccd-roles-test-data/with-bad-ccd-roles/definitions", numberOfCcdRolesInDefaultList)
        );
    }

}
