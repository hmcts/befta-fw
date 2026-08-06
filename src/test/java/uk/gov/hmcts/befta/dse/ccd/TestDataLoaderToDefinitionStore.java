package uk.gov.hmcts.befta.dse.ccd;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import feign.FeignException;
import feign.Request;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.net.ssl.SSLException;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.ClearEnvironmentVariable;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.ArgumentMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.TestAutomationAdapter;
import uk.gov.hmcts.befta.exception.ImportException;
import uk.gov.hmcts.befta.util.TestLogAppender;

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
    public static final String DEFINITION_IMPORT_JOB_ID = "BEFTA_DEFINITION_IMPORT_JOB_ID";
    public static final String DEFINITION_IMPORT_JOB_ID_VALUE = "11111111-2222-3333-4444-555555555555";
    public static final String DEFINITION_IMPORT_JOB_POLL_MAX_ATTEMPTS =
            "BEFTA_DEFINITION_IMPORT_JOB_POLL_MAX_ATTEMPTS";
    public static final String DEFINITION_IMPORT_JOB_POLL_INTERVAL_MILLISECONDS =
            "BEFTA_DEFINITION_IMPORT_JOB_POLL_INTERVAL_MILLISECONDS";
    public static final String DEFINITION_IMPORT_JOB_ID_HEADER = "X-Import-Job-Id";
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

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @ClearEnvironmentVariable(key = "BEFTA_FORCE_IMPORT_RETRY")
    @ClearEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID)
    void testImportDefinitionRecoveryDefaults() {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(mockAdapter);

        Assertions.assertEquals(1000L, dataLoaderToDefinitionStore.getDefinitionImportJobPollDelayInMilliseconds());
        UUID.fromString(dataLoaderToDefinitionStore.getDefinitionImportJobId());
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_POLL_INTERVAL_MILLISECONDS, value = "-1")
    void testDefinitionImportJobPollDelayClampsNegativeValueToZero() {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(mockAdapter);

        Assertions.assertEquals(0L, dataLoaderToDefinitionStore.getDefinitionImportJobPollDelayInMilliseconds());
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionJobIdUsesConfiguredUuid() {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(mockAdapter);

        Assertions.assertEquals(DEFINITION_IMPORT_JOB_ID_VALUE, dataLoaderToDefinitionStore.getDefinitionImportJobId());
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = "not-a-uuid")
    void testImportDefinitionJobIdRejectsInvalidConfiguredUuid() {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new DataLoaderToDefinitionStore(mockAdapter);

        Assertions.assertThrows(IllegalArgumentException.class, dataLoaderToDefinitionStore::getDefinitionImportJobId);
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @ClearEnvironmentVariable(key = DEFINITION_IMPORT_JOB_POLL_MAX_ATTEMPTS)
    void testDefinitionImportJobPollMaxAttemptsDefaultsTo300() {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        Assertions.assertEquals(300, dataLoaderToDefinitionStore.getDefinitionImportJobPollMaxAttempts());
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_POLL_MAX_ATTEMPTS, value = "50")
    void testDefinitionImportJobPollMaxAttemptsCanBeOverridden() {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        Assertions.assertEquals(50, dataLoaderToDefinitionStore.getDefinitionImportJobPollMaxAttempts());
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_POLL_MAX_ATTEMPTS, value = "0")
    void testDefinitionImportJobPollMaxAttemptsCanBeUnbounded() {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        Assertions.assertEquals(0, dataLoaderToDefinitionStore.getDefinitionImportJobPollMaxAttempts());
    }

    @Test
    @SetEnvironmentVariable(key = DEFINITION_STORE_HOST_KEY, value = DEFINITION_STORE_HOST_VALUE)
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionsRejectsConfiguredImportJobIdForMultipleFiles() {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        DataLoaderToDefinitionStore dataLoaderToDefinitionStore
                = new TestableDataLoaderToDefinitionStore(mockAdapter) {
                    @Override
                    protected List<String> getAllDefinitionFilesToLoadAt(String definitionsPath) {
                        return List.of("first.xlsx", "second.xlsx");
                    }
                };

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> dataLoaderToDefinitionStore.importDefinitionsAt("definitions")
        );
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
    @ClearEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID)
    void testImportDefinitionsPostsGeneratedImportJobIdsWhenNotConfigured() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Path firstFile = Files.createTempFile("definition-first", ".xlsx");
        Path secondFile = Files.createTempFile("definition-second", ".xlsx");
        ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(201);
        when(requestSpecification.post("/import")).thenReturn(rs);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore
                = new TestableDataLoaderToDefinitionStore(mockAdapter) {
                    @Override
                    protected List<String> getAllDefinitionFilesToLoadAt(String definitionsPath) {
                        return List.of(firstFile.toString(), secondFile.toString());
                    }
                };

        dataLoaderToDefinitionStore.importDefinitionsAt("definitions");

        verify(requestSpecification, atLeast(2)).header(headerCaptor.capture());
        List<String> importJobIds = headerCaptor.getAllValues().stream()
                .filter(header -> DEFINITION_IMPORT_JOB_ID_HEADER.equals(header.getName()))
                .map(Header::getValue)
                .toList();
        Assertions.assertEquals(2, importJobIds.size());
        importJobIds.forEach(UUID::fromString);
        Assertions.assertNotEquals(importJobIds.get(0), importJobIds.get(1));
        verify(requestSpecification, times(2)).post("/import");
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
    @ClearEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID)
    void testImportDefinitionClosesConnectionAndPostsImport() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(201);
        when(requestSpecification.post("/import")).thenReturn(rs);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        dataLoaderToDefinitionStore.importDefinition(file.toString());

        verify(requestSpecification).header(ArgumentMatchers.<Header>argThat(header ->
                "Connection".equals(header.getName()) && "close".equals(header.getValue())
        ));
        verify(requestSpecification).header(ArgumentMatchers.<Header>argThat(this::isImportJobHeaderWithValidUuid));
        verify(requestSpecification).post("/import");
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionPostsConfiguredImportJobId() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(201);
        when(requestSpecification.post("/import")).thenReturn(rs);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);
        Logger logger = (Logger) LoggerFactory.getLogger(DataLoaderToDefinitionStore.class);
        TestLogAppender testLogAppender = new TestLogAppender();
        logger.addAppender(testLogAppender);
        testLogAppender.start();

        try {
            dataLoaderToDefinitionStore.importDefinition(file.toString());
        } finally {
            logger.detachAppender(testLogAppender);
            testLogAppender.stop();
        }

        verify(requestSpecification).header(ArgumentMatchers.<Header>argThat(header ->
                DEFINITION_IMPORT_JOB_ID_HEADER.equals(header.getName())
                        && DEFINITION_IMPORT_JOB_ID_VALUE.equals(header.getValue())
        ));
        verify(requestSpecification).post("/import");
        Assertions.assertTrue(testLogAppender.getLogEvents().stream()
                .anyMatch(event -> event.getFormattedMessage()
                        .contains("Import is starting with " + DEFINITION_IMPORT_JOB_ID_VALUE)));
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
    void testGetImportJobUsesImportJobsEndpoint() {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);

        mockGetImportJobApiCalls(requestSpecification);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE)).thenReturn(rs);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        Response response = dataLoaderToDefinitionStore.getImportJob(DEFINITION_IMPORT_JOB_ID_VALUE);

        Assertions.assertEquals(rs, response);
        verify(requestSpecification).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionKeepsPollingAfterSslTransportExceptionWhenImportJobIsInitiallyNotFound() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response importJobNotFoundResponse = mock(io.restassured.response.Response.class);
        Response importJobCompletedResponse = mock(io.restassured.response.Response.class);
        JsonPath jsonPath = mock(JsonPath.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(importJobNotFoundResponse.getStatusCode()).thenReturn(404);
        when(importJobCompletedResponse.getStatusCode()).thenReturn(200);
        when(importJobCompletedResponse.jsonPath()).thenReturn(jsonPath);
        when(jsonPath.getString("status")).thenReturn("COMPLETED");
        when(requestSpecification.post("/import"))
                .thenThrow(new RuntimeException(new SSLException("Tag mismatch")));
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobNotFoundResponse)
                .thenReturn(importJobCompletedResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        dataLoaderToDefinitionStore.importDefinition(file.toString());

        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(2)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionPollsImportJobAfterSslTransportException() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response importJobInProgressResponse = mock(io.restassured.response.Response.class);
        Response importJobCompletedResponse = mock(io.restassured.response.Response.class);
        JsonPath inProgressJsonPath = mock(JsonPath.class);
        JsonPath completedJsonPath = mock(JsonPath.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(importJobInProgressResponse.getStatusCode()).thenReturn(200);
        when(importJobInProgressResponse.jsonPath()).thenReturn(inProgressJsonPath);
        when(inProgressJsonPath.getString("status")).thenReturn("IN_PROGRESS");
        when(importJobCompletedResponse.getStatusCode()).thenReturn(200);
        when(importJobCompletedResponse.jsonPath()).thenReturn(completedJsonPath);
        when(completedJsonPath.getString("status")).thenReturn("COMPLETED");
        when(requestSpecification.post("/import"))
                .thenThrow(new RuntimeException(new SSLException("Tag mismatch")));
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobInProgressResponse)
                .thenReturn(importJobCompletedResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        dataLoaderToDefinitionStore.importDefinition(file.toString());

        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(2)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionDoesNotPollClientErrorResponse() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        ResponseBody<?> responseBody = mock(io.restassured.response.ResponseBody.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(409);
        when(rs.statusCode()).thenReturn(409);
        when(rs.body()).thenReturn(responseBody);
        when(responseBody.prettyPrint()).thenReturn("");
        when(requestSpecification.post("/import")).thenReturn(rs);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        Assertions.assertThrows(ImportException.class, () -> dataLoaderToDefinitionStore.importDefinition(file.toString()));

        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(0)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionPollsImportJobAfterRequestTimeoutResponse() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Response importJobResponse = mock(io.restassured.response.Response.class);
        JsonPath jsonPath = mock(JsonPath.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(408);
        when(importJobResponse.getStatusCode()).thenReturn(200);
        when(importJobResponse.jsonPath()).thenReturn(jsonPath);
        when(jsonPath.getString("status")).thenReturn("COMPLETED");
        when(requestSpecification.post("/import")).thenReturn(rs);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        dataLoaderToDefinitionStore.importDefinition(file.toString());

        verify(requestSpecification).post("/import");
        verify(requestSpecification).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionDoesNotPollAfterRateLimitedSubmissionResponse() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        ResponseBody<?> responseBody = mock(io.restassured.response.ResponseBody.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(429);
        when(rs.statusCode()).thenReturn(429);
        when(rs.body()).thenReturn(responseBody);
        when(responseBody.prettyPrint()).thenReturn("");
        when(requestSpecification.post("/import")).thenReturn(rs);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        ImportException exception = Assertions.assertThrows(
                ImportException.class,
                () -> dataLoaderToDefinitionStore.importDefinition(file.toString())
        );

        Assertions.assertEquals(429, exception.getHttpStatusCode());
        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(0)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionPollsImportJobAfterServerErrorResponse() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Response importJobResponse = mock(io.restassured.response.Response.class);
        JsonPath jsonPath = mock(JsonPath.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(500);
        when(importJobResponse.getStatusCode()).thenReturn(200);
        when(importJobResponse.jsonPath()).thenReturn(jsonPath);
        when(jsonPath.getString("status")).thenReturn("COMPLETED");
        when(requestSpecification.post("/import")).thenReturn(rs);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        dataLoaderToDefinitionStore.importDefinition(file.toString());

        verify(requestSpecification).post("/import");
        verify(requestSpecification).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionTreatsExpiredImportJobAsFailure() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Response importJobResponse = mock(io.restassured.response.Response.class);
        JsonPath jsonPath = mock(JsonPath.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(500);
        when(importJobResponse.getStatusCode()).thenReturn(200);
        when(importJobResponse.jsonPath()).thenReturn(jsonPath);
        when(jsonPath.getString("status")).thenReturn("EXPIRED");
        when(requestSpecification.post("/import")).thenReturn(rs);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        ImportException exception = Assertions.assertThrows(
                ImportException.class,
                () -> dataLoaderToDefinitionStore.importDefinition(file.toString())
        );

        Assertions.assertTrue(exception.getMessage().contains("status 'EXPIRED'"));
        verify(requestSpecification).post("/import");
        verify(requestSpecification).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionKeepsPollingImportJobAfterPollException() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Response importJobCompletedResponse = mock(io.restassured.response.Response.class);
        JsonPath jsonPath = mock(JsonPath.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(500);
        when(importJobCompletedResponse.getStatusCode()).thenReturn(200);
        when(importJobCompletedResponse.jsonPath()).thenReturn(jsonPath);
        when(jsonPath.getString("status")).thenReturn("COMPLETED");
        when(requestSpecification.post("/import")).thenReturn(rs);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenThrow(new RuntimeException(new SSLException("poll failed")))
                .thenReturn(importJobCompletedResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        dataLoaderToDefinitionStore.importDefinition(file.toString());

        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(2)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionStopsPollingAfterClientErrorImportJobResponse() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Response importJobUnauthorizedResponse = mock(io.restassured.response.Response.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(500);
        when(importJobUnauthorizedResponse.getStatusCode()).thenReturn(401);
        when(requestSpecification.post("/import")).thenReturn(rs);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobUnauthorizedResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        ImportException exception = Assertions.assertThrows(
                ImportException.class,
                () -> dataLoaderToDefinitionStore.importDefinition(file.toString())
        );

        Assertions.assertEquals(401, exception.getHttpStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("non-retryable HTTP 401"));
        verify(requestSpecification).post("/import");
        verify(requestSpecification).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionKeepsPollingAfterRetryableClientErrorImportJobResponse() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Response importJobRateLimitedResponse = mock(io.restassured.response.Response.class);
        Response importJobCompletedResponse = mock(io.restassured.response.Response.class);
        JsonPath jsonPath = mock(JsonPath.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(500);
        when(importJobRateLimitedResponse.getStatusCode()).thenReturn(429);
        when(importJobCompletedResponse.getStatusCode()).thenReturn(200);
        when(importJobCompletedResponse.jsonPath()).thenReturn(jsonPath);
        when(jsonPath.getString("status")).thenReturn("COMPLETED");
        when(requestSpecification.post("/import")).thenReturn(rs);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobRateLimitedResponse)
                .thenReturn(importJobCompletedResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        dataLoaderToDefinitionStore.importDefinition(file.toString());

        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(2)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_POLL_MAX_ATTEMPTS, value = "0")
    void testImportDefinitionAllowsUnboundedImportJobPolling() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Response importJobNotFoundResponse = mock(io.restassured.response.Response.class);
        Response importJobCompletedResponse = mock(io.restassured.response.Response.class);
        JsonPath jsonPath = mock(JsonPath.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(500);
        when(importJobNotFoundResponse.getStatusCode()).thenReturn(404);
        when(importJobCompletedResponse.getStatusCode()).thenReturn(200);
        when(importJobCompletedResponse.jsonPath()).thenReturn(jsonPath);
        when(jsonPath.getString("status")).thenReturn("COMPLETED");
        when(requestSpecification.post("/import")).thenReturn(rs);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobNotFoundResponse)
                .thenReturn(importJobCompletedResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        dataLoaderToDefinitionStore.importDefinition(file.toString());

        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(2)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_POLL_MAX_ATTEMPTS, value = "2")
    void testImportDefinitionFailsWhenImportJobPollMaxAttemptsExceeded() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Response importJobNotFoundResponse = mock(io.restassured.response.Response.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(500);
        when(importJobNotFoundResponse.getStatusCode()).thenReturn(404);
        when(requestSpecification.post("/import")).thenReturn(rs);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobNotFoundResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        ImportException exception = Assertions.assertThrows(
                ImportException.class,
                () -> dataLoaderToDefinitionStore.importDefinition(file.toString())
        );

        Assertions.assertEquals(-1, exception.getHttpStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("after 2 poll attempts"));
        Assertions.assertTrue(exception.getMessage().contains("Last HTTP status: 404"));
        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(2)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_POLL_MAX_ATTEMPTS, value = "2")
    void testImportDefinitionPollTimeoutMessageIncludesLastImportJobStatus() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Response importJobInProgressResponse = mock(io.restassured.response.Response.class);
        JsonPath jsonPath = mock(JsonPath.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(500);
        when(importJobInProgressResponse.getStatusCode()).thenReturn(200);
        when(importJobInProgressResponse.jsonPath()).thenReturn(jsonPath);
        when(jsonPath.getString("status")).thenReturn("IN_PROGRESS");
        when(requestSpecification.post("/import")).thenReturn(rs);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobInProgressResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        ImportException exception = Assertions.assertThrows(
                ImportException.class,
                () -> dataLoaderToDefinitionStore.importDefinition(file.toString())
        );

        Assertions.assertEquals(-1, exception.getHttpStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("after 2 poll attempts"));
        Assertions.assertTrue(exception.getMessage().contains("Last HTTP status: 200"));
        Assertions.assertTrue(exception.getMessage().contains("Last import job status: IN_PROGRESS"));
        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(2)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_POLL_MAX_ATTEMPTS, value = "2")
    void testImportDefinitionPollTimeoutMessageUsesPollExceptionAsLastResult() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Response importJobUnavailableResponse = mock(io.restassured.response.Response.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(500);
        when(importJobUnavailableResponse.getStatusCode()).thenReturn(503);
        when(requestSpecification.post("/import")).thenReturn(rs);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobUnavailableResponse)
                .thenThrow(new RuntimeException(new SSLException("poll failed")));

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        ImportException exception = Assertions.assertThrows(
                ImportException.class,
                () -> dataLoaderToDefinitionStore.importDefinition(file.toString())
        );

        Assertions.assertEquals(-1, exception.getHttpStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("after 2 poll attempts"));
        Assertions.assertFalse(exception.getMessage().contains("Last HTTP status"));
        Assertions.assertTrue(exception.getMessage().contains("Last poll exception: javax.net.ssl.SSLException: poll failed"));
        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(2)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionDoesNotPollAfterNonRetryableClientErrorDuringSubmission() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(requestSpecification.post("/import")).thenThrow(new RuntimeException(badRequestFeignException()));

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        ImportException exception = Assertions.assertThrows(
                ImportException.class,
                () -> dataLoaderToDefinitionStore.importDefinition(file.toString())
        );

        Assertions.assertEquals(400, exception.getHttpStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("submission failed with HTTP 400"));
        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(0)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionDoesNotPollAfterRateLimitedClientErrorDuringSubmission() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(requestSpecification.post("/import"))
                .thenThrow(new RuntimeException(feignException(429, "Too Many Requests")));

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        ImportException exception = Assertions.assertThrows(
                ImportException.class,
                () -> dataLoaderToDefinitionStore.importDefinition(file.toString())
        );

        Assertions.assertEquals(429, exception.getHttpStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("submission failed with HTTP 429"));
        verify(requestSpecification).post("/import");
        verify(requestSpecification, times(0)).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    void testImportDefinitionStopsPollingAfterNonRetryableClientErrorDuringPoll() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response rs = mock(io.restassured.response.Response.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(rs.getStatusCode()).thenReturn(500);
        when(requestSpecification.post("/import")).thenReturn(rs);
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenThrow(new RuntimeException(badRequestFeignException()));

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        ImportException exception = Assertions.assertThrows(
                ImportException.class,
                () -> dataLoaderToDefinitionStore.importDefinition(file.toString())
        );

        Assertions.assertEquals(400, exception.getHttpStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("poll attempt 1"));
        verify(requestSpecification).post("/import");
        verify(requestSpecification).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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
    @SetEnvironmentVariable(key = DEFINITION_IMPORT_JOB_ID, value = DEFINITION_IMPORT_JOB_ID_VALUE)
    @SetEnvironmentVariable(key = "BEFTA_FORCE_IMPORT_RETRY", value = "false")
    void testImportDefinitionPollsImportJobAfterUnexpectedExceptionWhenForceImportRetryIsFalse() throws Exception {
        TestAutomationAdapter mockAdapter = mock(TestAutomationAdapter.class);
        RequestSpecification requestSpecification = mock(RequestSpecification.class);
        Response importJobResponse = mock(io.restassured.response.Response.class);
        JsonPath jsonPath = mock(JsonPath.class);
        Path file = Files.createTempFile("definition", ".xlsx");

        mockImportDefinitionApiCalls(requestSpecification);
        when(importJobResponse.getStatusCode()).thenReturn(200);
        when(importJobResponse.jsonPath()).thenReturn(jsonPath);
        when(jsonPath.getString("status")).thenReturn("COMPLETED");
        when(requestSpecification.post("/import"))
                .thenThrow(new RuntimeException("unexpected import failure"));
        when(requestSpecification.get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE))
                .thenReturn(importJobResponse);

        DataLoaderToDefinitionStore dataLoaderToDefinitionStore = new TestableDataLoaderToDefinitionStore(mockAdapter);

        dataLoaderToDefinitionStore.importDefinition(file.toString());

        verify(requestSpecification).post("/import");
        verify(requestSpecification).get("/import-jobs/{id}", DEFINITION_IMPORT_JOB_ID_VALUE);
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

    private void mockImportDefinitionApiCalls(RequestSpecification requestSpecification) {
        when(RestAssured.given(any())).thenReturn(requestSpecification);
        when(requestSpecification.header(any())).thenReturn(requestSpecification);
        when(requestSpecification.given()).thenReturn(requestSpecification);
        when(requestSpecification.multiPart(any(File.class))).thenReturn(requestSpecification);
        when(requestSpecification.when()).thenReturn(requestSpecification);
    }

    private void mockGetImportJobApiCalls(RequestSpecification requestSpecification) {
        when(RestAssured.given(any())).thenReturn(requestSpecification);
        when(requestSpecification.header(any())).thenReturn(requestSpecification);
        when(requestSpecification.given()).thenReturn(requestSpecification);
        when(requestSpecification.when()).thenReturn(requestSpecification);
    }

    private boolean isImportJobHeaderWithValidUuid(Header header) {
        if (!DEFINITION_IMPORT_JOB_ID_HEADER.equals(header.getName())) {
            return false;
        }
        try {
            UUID.fromString(header.getValue());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private FeignException badRequestFeignException() {
        return feignException(400, "Bad Request");
    }

    private FeignException feignException(int statusCode, String reason) {
        Request request = Request.create(
                Request.HttpMethod.POST,
                IDAM_URL_VALUE + "/oauth2/authorize",
                Collections.emptyMap(),
                new byte[0],
                StandardCharsets.UTF_8
        );
        return FeignException.errorStatus(
                "AuthApi#authenticateUser(String,String,String,String)",
                feign.Response.builder()
                        .status(statusCode)
                        .reason(reason)
                        .request(request)
                        .build()
        );
    }

    private static class TestableDataLoaderToDefinitionStore extends DataLoaderToDefinitionStore {

        TestableDataLoaderToDefinitionStore(TestAutomationAdapter adapter) {
            super(adapter);
        }

        @Override
        protected long getDefinitionImportJobPollDelayInMilliseconds() {
            return 0L;
        }
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
