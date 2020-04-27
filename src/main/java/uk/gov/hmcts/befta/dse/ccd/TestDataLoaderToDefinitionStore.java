package uk.gov.hmcts.befta.dse.ccd;

import io.restassured.internal.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.DefaultTestAutomationAdapter;
import uk.gov.hmcts.befta.TestAutomationAdapter;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.FileUtils;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.util.BeftaUtils;

//AC-2
public class TestDataLoaderToDefinitionStore {

    private static final Logger logger = LoggerFactory.getLogger(TestDataLoaderToDefinitionStore.class);

    public static final String DEFAULT_DEFINITIONS_PATH = "uk/gov/hmcts/befta/dse/ccd/definitions/valid";

    private static final CcdRoleConfig[] CCD_ROLES_NEEDED_FOR_TA = {
            new CcdRoleConfig("caseworker-autotest1", "PUBLIC"),
            new CcdRoleConfig("caseworker-autotest1-private", "PRIVATE"),
            new CcdRoleConfig("caseworker-autotest1-senior", "RESTRICTED"),
            new CcdRoleConfig("caseworker-autotest1-solicitor", "PRIVATE"),

            new CcdRoleConfig("caseworker-autotest2", "PUBLIC"),
            new CcdRoleConfig("caseworker-autotest2-private", "PRIVATE"),
            new CcdRoleConfig("caseworker-autotest2-senior", "RESTRICTED"),
            new CcdRoleConfig("caseworker-autotest2-solicitor", "PRIVATE"),

            new CcdRoleConfig("caseworker-befta_jurisdiction_1", "PUBLIC"),

            new CcdRoleConfig("caseworker-befta_jurisdiction_2", "PUBLIC"),
            new CcdRoleConfig("caseworker-befta_jurisdiction_2-solicitor_1", "PUBLIC"),
            new CcdRoleConfig("caseworker-befta_jurisdiction_2-solicitor_2", "PUBLIC"),
            new CcdRoleConfig("caseworker-befta_jurisdiction_2-solicitor_3", "PUBLIC"),
            new CcdRoleConfig("citizen", "PUBLIC"),

            new CcdRoleConfig("caseworker-befta_jurisdiction_3", "PUBLIC"),
            new CcdRoleConfig("caseworker-befta_jurisdiction_3-solicitor", "PUBLIC"),

            new CcdRoleConfig("caseworker-autotest1-manager", "PUBLIC"),
            new CcdRoleConfig("caseworker-autotest1-junior", "PUBLIC")
    };

    private TestAutomationAdapter adapter;
    private String definitionsPath;
    private String definitionStoreUrl;

    public TestDataLoaderToDefinitionStore(TestAutomationAdapter adapter) {
        this(adapter, DEFAULT_DEFINITIONS_PATH, BeftaMain.getConfig().getDefinitionStoreUrl());
    }

    public TestDataLoaderToDefinitionStore(TestAutomationAdapter adapter, String definitionsPath,
            String definitionStoreUrl) {
        super();
        this.adapter = adapter;
        this.definitionsPath = definitionsPath;
        this.definitionStoreUrl = definitionStoreUrl;
    }

    public void addCcdRoles() {
        logger.info("{} roles will be added to '{}'.", CCD_ROLES_NEEDED_FOR_TA.length, definitionStoreUrl);
        for (CcdRoleConfig roleConfig : CCD_ROLES_NEEDED_FOR_TA) {
            try {
                logger.info("\n\nAdding CCD Role {}.", roleConfig);
                addCcdRole(roleConfig);
                logger.info("\n\nAdded CCD Role {}.", roleConfig);
            } catch (Exception e) {
                logger.error("\n\nCouldn't adding CCD Role {} - Exception: {}.\\n\\n", roleConfig, e);
            }
        }
    }

    //todo refactor import definition file method
    public void importDefinitions() {
        List<String> definitionFileResources = getAllDefinitionFilesToLoad();
        logger.info("{} definition files will be uploaded to '{}'.", definitionFileResources.size(),
                definitionStoreUrl);
        for (String fileName : definitionFileResources) {
            try {
                logger.info("\n\nImporting {}...", fileName);
                importDefinition(fileName);
                logger.info("\nImported {}.\n\n", fileName);
            } catch (Exception e) {
                logger.error("Couldn't import {} - Exception: {}.\n\n", fileName, e);
            }
        }
    }

    protected void addCcdRole(CcdRoleConfig roleConfig) {
        Map<String, String> ccdRoleInfo = new HashMap<>();
        ccdRoleInfo.put("role", roleConfig.getRole());
        ccdRoleInfo.put("security_classification", roleConfig.getSecurityClassification());
        Response response = asAutoTestImporter().given().header("Content-type", "application/json").body(ccdRoleInfo)
                .when().put("/api/user-role");
        if (response.getStatusCode() / 100 != 2) {
            String message = "Import failed with response body: " + response.body().prettyPrint();
            message += "\nand http code: " + response.statusCode();
            throw new FunctionalTestException(message);
        }
    }

    //todo refactor import definition file method
    protected List<String> getAllDefinitionFilesToLoad() {
        try {
            boolean convertJsonFilesToExcel = false;
            Set<String> definitionJsonResourcesToTransform = new HashSet<>();
            List<String> definitionFileResources = new ArrayList<String>();
            ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
            for (ClassPath.ResourceInfo info : cp.getResources()) {
                if (info.getResourceName().startsWith(definitionsPath)
                        && info.getResourceName().toLowerCase().endsWith(".xlsx")
                        && !info.getResourceName().startsWith("~$")) {
                    definitionFileResources.add(info.getResourceName());
                } else if (info.getResourceName().startsWith(definitionsPath)
                        && info.getResourceName().toLowerCase().endsWith(".json")){
                    convertJsonFilesToExcel = true;
                    File jsonFile = BeftaUtils.createJsonDefinitionFileFromClasspath(info.getResourceName());
                    String jsonDefinitionParentFolder = jsonFile.getParentFile().getParentFile().getPath();
                    definitionJsonResourcesToTransform.add(jsonDefinitionParentFolder);
                }
            }

            if (convertJsonFilesToExcel) {
                definitionFileResources.addAll(
                        definitionJsonResourcesToTransform.stream()
                        .map(folderPath -> new JsonTransformer(folderPath,"build/tmp").transformToExcel())
                        .collect(Collectors.toList()));
            }

            return definitionFileResources;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void importDefinition(String fileResourcePath) throws IOException {
        File file =  new File(fileResourcePath).exists()
                ?
                new File(fileResourcePath)
                :
                BeftaUtils.getClassPathResourceIntoTemporaryFile(fileResourcePath);

        try {
            Response response = asAutoTestImporter().given().multiPart(file).when().post("/import");
            if (response.getStatusCode() != 201) {
                String message = "Import failed with response body: " + response.body().prettyPrint();
                message += "\nand http code: " + response.statusCode();
                throw new FunctionalTestException(message);
            }

        } finally {

            //todo delete parent directory for json structure
            file.delete();
        }
    }

    protected RequestSpecification asAutoTestImporter() {
        UserData caseworker = new UserData(BeftaMain.getConfig().getImporterAutoTestEmail(),
                BeftaMain.getConfig().getImporterAutoTestPassword());
        adapter.authenticate(caseworker);

        String s2sToken = adapter.getNewS2SToken();
        return RestAssured.given(new RequestSpecBuilder().setBaseUri(definitionStoreUrl).build())
                .header("Authorization", "Bearer " + caseworker.getAccessToken())
                .header("ServiceAuthorization", s2sToken);
    }

    //for testing
    public static void main(String[] args) {
        new TestDataLoaderToDefinitionStore(new DefaultTestAutomationAdapter()).importDefinitions();
    }

}
