package uk.gov.hmcts.befta.dse.ccd;

import com.google.common.reflect.ClassPath;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.DefaultBeftaTestDataLoader;
import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.TestAutomationAdapter;
import uk.gov.hmcts.befta.auth.UserTokenProviderConfig;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer;
import uk.gov.hmcts.befta.exception.ImportException;
import uk.gov.hmcts.befta.util.BeftaUtils;
import uk.gov.hmcts.befta.util.EnvironmentVariableUtils;
import uk.gov.hmcts.befta.util.FileUtils;
import uk.gov.hmcts.befta.util.JsonUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import io.restassured.http.Header;

public class DataLoaderToDefinitionStore extends DefaultBeftaTestDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderToDefinitionStore.class);

    public static final String VALID_CCD_TEST_DEFINITIONS_PATH = "uk/gov/hmcts/ccd/test_definitions/valid";

    private static final String TEMPORARY_DEFINITION_FOLDER = "definition_files";

    private static final String[] RA_DATA_RESOURCE_PACKAGES = { "roleAssignments" };

    private static final String SECURITY_CLASSIFICATION_PUBLIC = "PUBLIC";
    private static final String SECURITY_CLASSIFICATION_PRIVATE = "PRIVATE";
    private static final String SECURITY_CLASSIFICATION_RESTRICTED = "RESTRICTED";

    // NB: by default BEFTA-FW will load CCD Roles from a json file: this constant is the fallback if that load fails.
    private static final CcdRoleConfig[] CCD_ROLES_NEEDED_FOR_TA = {
        new CcdRoleConfig("caseworker-autotest1", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-autotest1-private", SECURITY_CLASSIFICATION_PRIVATE),
        new CcdRoleConfig("caseworker-autotest1-senior", SECURITY_CLASSIFICATION_RESTRICTED),
        new CcdRoleConfig("caseworker-autotest1-solicitor", SECURITY_CLASSIFICATION_PRIVATE),
        new CcdRoleConfig("caseworker-autotest2", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-autotest2-private", SECURITY_CLASSIFICATION_PRIVATE),
        new CcdRoleConfig("caseworker-autotest2-senior", SECURITY_CLASSIFICATION_RESTRICTED),
        new CcdRoleConfig("caseworker-autotest2-solicitor", SECURITY_CLASSIFICATION_PRIVATE),
        new CcdRoleConfig("caseworker-befta_jurisdiction_1", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_jurisdiction_2", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_jurisdiction_2-solicitor_1", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_jurisdiction_2-solicitor_2", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_jurisdiction_2-solicitor_3", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("citizen", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_jurisdiction_3", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_jurisdiction_3-solicitor", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-autotest1-manager", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-autotest1-junior", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_master", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_master-solicitor", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_master-solicitor_1", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_master-solicitor_2", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_master-solicitor_3", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_master-junior", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-befta_master-manager", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-caa", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("caseworker-approver", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("next-hearing-date-admin", SECURITY_CLASSIFICATION_PUBLIC),
        new CcdRoleConfig("GS_profile", SECURITY_CLASSIFICATION_PUBLIC)
    };

    private final TestAutomationAdapter adapter;
    private final String definitionStoreUrl;
    private final String definitionsPath;

    public DataLoaderToDefinitionStore(String definitionsPath) {
        this(new DefaultTestAutomationAdapter(), definitionsPath, CcdEnvironment.AAT,
                BeftaMain.getConfig().getDefinitionStoreUrl());
    }

    public DataLoaderToDefinitionStore(CcdEnvironment dataSetupEnvironment) {
        this(new DefaultTestAutomationAdapter(), VALID_CCD_TEST_DEFINITIONS_PATH, dataSetupEnvironment,
                BeftaMain.getConfig().getDefinitionStoreUrl());
    }

    public DataLoaderToDefinitionStore(CcdEnvironment dataSetupEnvironment, String definitionsPath) {
        this(new DefaultTestAutomationAdapter(), definitionsPath, dataSetupEnvironment,
                BeftaMain.getConfig().getDefinitionStoreUrl());
    }

    public DataLoaderToDefinitionStore(TestAutomationAdapter adapter) {
        this(adapter, VALID_CCD_TEST_DEFINITIONS_PATH, CcdEnvironment.AAT,
                BeftaMain.getConfig().getDefinitionStoreUrl());
    }

    public DataLoaderToDefinitionStore(TestAutomationAdapter adapter, String definitionsPath) {
        this(adapter, definitionsPath, CcdEnvironment.AAT, BeftaMain.getConfig().getDefinitionStoreUrl());
    }

    public DataLoaderToDefinitionStore(TestAutomationAdapter adapter, String definitionsPath,
            CcdEnvironment dataSetupEnvironment) {
        this(adapter, definitionsPath, dataSetupEnvironment, BeftaMain.getConfig().getDefinitionStoreUrl());
    }

    public DataLoaderToDefinitionStore(TestAutomationAdapter adapter, String definitionsPath,
            CcdEnvironment dataSetupEnvironment, String definitionStoreUrl) {
        super(selectEnvironmentWith(dataSetupEnvironment, definitionStoreUrl));
        this.adapter = adapter;
        this.definitionsPath = definitionsPath;
        this.definitionStoreUrl = definitionStoreUrl;
    }

    private static CcdEnvironment selectEnvironmentWith(CcdEnvironment dataSetupEnvironment,
            String definitionStoreUrl) {
        if (dataSetupEnvironment == CcdEnvironment.PREVIEW) {
            if (definitionStoreUrl.contains("-aat.")) {
                return CcdEnvironment.AAT;
            }
        } else if (dataSetupEnvironment == CcdEnvironment.AAT) {
            if (definitionStoreUrl.contains("-preview.")) {
                return CcdEnvironment.PREVIEW;
            }
        }
        if (definitionStoreUrl.contains("localhost")) {
            return CcdEnvironment.LOCAL;
        }
        return dataSetupEnvironment;
    }

    public static void main(String[] args) throws Throwable {
        main(DataLoaderToDefinitionStore.class, args);
    }

    protected static void main(Class<? extends DataLoaderToDefinitionStore> klass, String[] args) throws Throwable {
        CcdEnvironment environment = null;
        if (args.length == 1) {
            try {
                environment = CcdEnvironment.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }
        if (environment == null) {
            throw new IllegalArgumentException(
                    "Must have 1 argument. Acceptable values: " + Arrays.asList(CcdEnvironment.values()) + ".");
        }
        execute(klass, environment);
    }

    protected static void execute(Class<? extends DataLoaderToDefinitionStore> klass, CcdEnvironment environment)
            throws Throwable {
        DataLoaderToDefinitionStore loader = null;
        try {
            loader = klass.getConstructor(CcdEnvironment.class).newInstance(environment);
            loader.loadDataIfNotLoadedVeryRecently();
        } catch (Throwable t) {
            logger.error("Failed to load data to {}: {}", environment, t.getMessage());
            logger.error("Thrown: ", t);
            if (loader == null || !loader.shouldTolerateDataSetupFailure()) {
                throw t;
            }
        }
    }

    protected boolean shouldTolerateDataSetupFailure() {
        return false;
    }

    @Override
    protected void doLoadTestData() {
        addCcdRoles();
        importDefinitions();
        createRoleAssignments();
    }

    public void createRoleAssignments() {
        getRoleAssignmentFiles(RA_DATA_RESOURCE_PACKAGES);
    }

    private void getRoleAssignmentFiles(String[] resourcePackages) {
        try {
            for (String resourcePackage : resourcePackages) {
                String prefix = resourcePackage + "/";
                Resource[] resources = new PathMatchingResourcePatternResolver()
                        .getResources("classpath*:" + prefix + "**/*.ras.json");
                for (Resource resource : resources) {
                    InputStream inputStream = resource.getInputStream();
                    String result = new BufferedReader(new InputStreamReader(inputStream))
                            .lines().collect(Collectors.joining("\n"));
                    createRoleAssignment(result, resource.getFilename());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read role assignment files", e);
        }
    }

    protected void createRoleAssignment(String resource, String filename) {
        try {
            JSONObject payLoadJSONObject = new JSONObject(resource);
            Header contentType = new Header("Content-type", "application/json");
            Response response = asRoleAssignmentUser().given()
                    .header(contentType)
                    .body(readObjectFromJsonFile(payLoadJSONObject).toString())
                    .when().post("/am/role-assignments");
            if (response.getStatusCode() / 100 != 2) {
                String message = "Calling Role Assignment service failed with response body: "
                        + response.body().prettyPrint();
                message += "\nand http code: " + response.statusCode();
                throw new RuntimeException(message);
            } else {
                logger.info("Role Assignment file {} loaded", filename);
            }
        } catch (Exception e) {
            String message = String.format("reading json from %s failed",filename);
            throw new RuntimeException(message, e);
        }

    }

    private  JSONObject readObjectFromJsonFile(JSONObject jsonObject) throws JSONException {
        JSONArray keys = jsonObject.names();
        for (int i = 0; i < keys.length(); i++) {
            String current_key = keys.get(i).toString();
            if (jsonObject.get(current_key).getClass().getName().equals("org.json.JSONObject")) {
                readObjectFromJsonFile((JSONObject) jsonObject.get(current_key));
            } else if (jsonObject.get(current_key).getClass().getName().equals("org.json.JSONArray")) {
                for (int j = 0; j < ((JSONArray) jsonObject.get(current_key)).length(); j++) {
                    if (((JSONArray) jsonObject.get(current_key)).get(j).getClass().getName().equals("org.json.JSONObject")) {
                        readObjectFromJsonFile((JSONObject) ((JSONArray) jsonObject.get(current_key)).get(j));
                    }
                }
            } else {
                String value = jsonObject.optString(current_key);
                if (value.startsWith("[[$")) {
                    UserData raUser = resolveUserData(value);
                    resolveUserIdamId(jsonObject, current_key, raUser);
                }
            }
        }
        return jsonObject;
    }

    private void resolveUserIdamId(JSONObject object, String current_key, UserData raUser) {
        try {
            adapter.authenticate(raUser, UserTokenProviderConfig.DEFAULT_INSTANCE.getClientId());
            object.put(current_key, raUser.getId());
        } catch (ExecutionException e) {
            String message = String.format("parsing json as %s failed", raUser.getUsername());
            throw new RuntimeException(message, e);
        }
    }

    private UserData resolveUserData(String value) {
        String resolvedUsername = EnvironmentVariableUtils.resolvePossibleVariable(value);
        String resolvedPassword = EnvironmentVariableUtils.getRequiredVariable(
                value.substring(3, value.length() - 2) + "_PWD");
        return getUserData(resolvedUsername, resolvedPassword);
    }

    private UserData getUserData(String resolvedUsername, String resolvedPassword) {
        return new UserData(resolvedUsername, resolvedPassword);
    }

    private RequestSpecification asRoleAssignmentUser() {
        UserData raUser = new UserData(BeftaMain.getConfig().getRoleAssignmentEmail(),
                BeftaMain.getConfig().getRoleAssignmentPassword());
        try {
            adapter.authenticate(raUser, UserTokenProviderConfig.DEFAULT_INSTANCE.getClientId());
            String s2sToken = adapter.getNewS2STokenWithEnvVars("ROLE_ASSIGNMENT_API_GATEWAY_S2S_CLIENT_ID",
                    "ROLE_ASSIGNMENT_API_GATEWAY_S2S_CLIENT_KEY");
            Header auth = new Header("Authorization", "Bearer " + raUser.getAccessToken());
            Header serviceAuth = new Header("ServiceAuthorization", s2sToken);
            return RestAssured.given(new RequestSpecBuilder().setBaseUri(BeftaMain.getConfig().getRoleAssignmentHost()).build())
                    .header(auth)
                    .header(serviceAuth);
        } catch (ExecutionException e) {
            String message = String.format("authenticating as %s failed ", raUser.getUsername());
            throw new RuntimeException(message, e);
        }
    }

    public void addCcdRoles() {
        CcdRoleConfig[] ccdRoleConfigs = getCcdRolesConfig();
        logger.info("{} roles will be added to '{}'.", ccdRoleConfigs.length, definitionStoreUrl);
        for (CcdRoleConfig roleConfig : ccdRoleConfigs) {
            try {
                logger.info("\n\nAdding CCD Role {}.", roleConfig);
                addCcdRole(roleConfig);
                logger.info("\n\nAdded CCD Role {}.", roleConfig);
            } catch (Exception e) {
                logger.error("\n\nCouldn't add CCD Role {} - Exception: {}.\n\n", roleConfig, e);
                if (!shouldTolerateDataSetupFailure()) {
                    throw e;
                }
            }
        }
    }

    public void importDefinitions() {
        importDefinitionsAt(definitionsPath);
    }

    protected void importDefinitionsAt(String definitionsPath) {
        List<String> definitionFileResources = getAllDefinitionFilesToLoadAt(definitionsPath);
        logger.info("{} definition files will be uploaded to '{}' on {}.", definitionFileResources.size(),
                definitionStoreUrl, getDataSetupEnvironment());
        String message = "Couldn't import {} - Exception: {}.\n\n";
        try {
            for (String fileName : definitionFileResources) {
                try {
                    logger.info("\n\nImporting {}...", fileName);
                    importDefinition(fileName);
                    logger.info("\nImported {}.\n\n", fileName);
                } catch (ImportException e) {
                    logger.error(message, fileName, e);
                    if (!shouldTolerateDataSetupFailure(e)) {
                        throw e;
                    }
                } catch (RuntimeException e) {
                    logger.error(message, fileName, e);
                    throw e;
                } catch (IOException e) {
                    logger.error(message, fileName, e);
                    throw new RuntimeException(e);
                }
            }
        } finally {
            FileUtils.deleteDirectory(TEMPORARY_DEFINITION_FOLDER);
        }
    }

    protected boolean shouldTolerateDataSetupFailure(Throwable e) {
        return shouldTolerateDataSetupFailure();
    }

    protected void addCcdRole(CcdRoleConfig roleConfig) {
        Map<String, String> ccdRoleInfo = new HashMap<>();
        ccdRoleInfo.put("role", roleConfig.getRole());
        ccdRoleInfo.put("security_classification", roleConfig.getSecurityClassification());
        Header contentType = new Header("Content-type", "application/json");
        Response response = asAutoTestImporter().given().header(contentType).body(
                ccdRoleInfo)
                .when().put("/api/user-role");
        if (response.getStatusCode() / 100 != 2) {
            String message = "Import failed with response body: " + response.body().prettyPrint();
            message += "\nand http code: " + response.getStatusCode();
            throw new RuntimeException(message);
        }
    }

    protected List<String> getAllDefinitionFilesToLoadAt(String definitionsPath) {
        return getAllDefinitionFilesToLoadAt(definitionsPath, TEMPORARY_DEFINITION_FOLDER);
    }

    public List<String> getAllDefinitionFilesToLoadAt(String definitionsPath, String generatedFileOutputPath) {
        try {
            boolean convertJsonFilesToExcel = false;
            Set<String> definitionJsonResourcesToTransform = new HashSet<>();
            List<String> definitionFileResources = new ArrayList<>();
            ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
            for (ClassPath.ResourceInfo info : cp.getResources()) {
                if (isAnExcelFileToImport(info.getResourceName(), definitionsPath)) {
                    definitionFileResources.add(info.getResourceName());
                } else if (isUnderAJsonDefinitionPackage(info.getResourceName(), definitionsPath)) {
                    convertJsonFilesToExcel = true;
                    File jsonFile = BeftaUtils.createJsonDefinitionFileFromClasspath(info.getResourceName());
                    String jsonDefinitionParentFolder = jsonFile.getParentFile().getParentFile().getPath();
                    definitionJsonResourcesToTransform.add(jsonDefinitionParentFolder);
                }
            }
            if (convertJsonFilesToExcel) {
                CcdEnvironment forEnvironment = getDataSetupEnvironment() == CcdEnvironment.PREVIEW ? CcdEnvironment.AAT
                        : (CcdEnvironment) getDataSetupEnvironment();
                definitionFileResources.addAll(definitionJsonResourcesToTransform.stream()
                        .map(folderPath -> new JsonTransformer(forEnvironment,
                                folderPath,
                                generatedFileOutputPath)
                                .transformToExcel())
                        .collect(Collectors.toList()));
            }
            return definitionFileResources;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void importDefinition(String fileResourcePath) throws IOException {
        File file = new File(fileResourcePath).exists() ? new File(fileResourcePath)
                : BeftaUtils.getClassPathResourceIntoTemporaryFile(fileResourcePath);
        try {
            Response response = asAutoTestImporter().given().multiPart(file).when().post("/import");
            if (response.getStatusCode() != 201) {
                String message = "Import failed with response body: " + response.body().prettyPrint();
                message += "\nand http code: " + response.statusCode();
                throw new ImportException(message, response.statusCode());
            }
        } finally {
            file.delete();
        }
    }

    protected RequestSpecification asAutoTestImporter() {
        UserData importingUser = new UserData(BeftaMain.getConfig().getImporterAutoTestEmail(),
                BeftaMain.getConfig().getImporterAutoTestPassword());
        try {
            adapter.authenticate(importingUser, UserTokenProviderConfig.DEFAULT_INSTANCE.getClientId());
            String s2sToken = adapter.getNewS2STokenWithEnvVars("CCD_API_GATEWAY_S2S_ID", "CCD_API_GATEWAY_S2S_KEY");
            Header auth = new Header("Authorization", "Bearer " + importingUser.getAccessToken());
            Header serviceAuth = new Header("ServiceAuthorization", s2sToken);
            return RestAssured.given(new RequestSpecBuilder().setBaseUri(definitionStoreUrl).build())
                .header(auth)
                .header(serviceAuth);
        } catch (ExecutionException e) {
            String message = String.format("authenticating as %s failed ", importingUser.getUsername());
            throw new RuntimeException(message, e);
        }
    }

    private boolean isAnExcelFileToImport(String resourceName, String definitionsPath) {
        return resourceName.startsWith(definitionsPath) && resourceName.toLowerCase().endsWith(
                ".xlsx")
                && !resourceName.startsWith("~$");
    }

    private boolean isUnderAJsonDefinitionPackage(String resourceName, String definitionsPath) {
        return resourceName.startsWith(definitionsPath) && resourceName.toLowerCase().endsWith(".json");
    }

    private CcdRoleConfig[] getCcdRolesConfig() {

        if (!StringUtils.isBlank(this.definitionsPath)) {

            String ccdRolesPath = Paths.get(this.definitionsPath).resolve("../ccd-roles.json").normalize().toString();
            try {
                Enumeration<URL> ccdRolesResource
                        = Thread.currentThread().getContextClassLoader().getResources(ccdRolesPath);
                if (ccdRolesResource != null && ccdRolesResource.hasMoreElements()) {
                    logger.info("Found CCD Roles JSON file: '{}'.", ccdRolesPath);
                    return JsonUtils.readObjectFromJsonResource(ccdRolesPath, CCD_ROLES_NEEDED_FOR_TA.getClass());
                } else {
                    logger.info("No CCD Roles JSON file found: '{}'.", ccdRolesPath);
                }
            } catch (IOException ex) {
                logger.warn("Error reading CCD Roles JSON file: '{}': ", ccdRolesPath, ex);
            }
        }

        logger.info("Defaulting to load standard CCD Roles from BEFTA library.");
        return CCD_ROLES_NEEDED_FOR_TA;
    }

}
