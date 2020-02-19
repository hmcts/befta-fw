package uk.gov.hmcts.befta.dse.ccd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.TestAutomationAdapter;
import uk.gov.hmcts.befta.data.UserData;
import uk.gov.hmcts.befta.exception.FunctionalTestException;

public class TestDataLoaderToDefinitionStore {

    private static final Logger logger = LoggerFactory.getLogger(TestDataLoaderToDefinitionStore.class);

    private static final File TEMP_FILE = new File("___temp___.temp");

    public static final String DEFAULT_DEFINITIONS_PATH = "uk/gov/hmcts/befta/dse/ccd/definitions/";

    private static final String[][] CCD_ROLES_NEEDED_FOR_TA = {
            { "caseworker-autotest1", "PUBLIC" },
            { "caseworker-autotest1-private", "PRIVATE" },
            { "caseworker-autotest1-senior", "RESTRICTED" },
            { "caseworker-autotest1-solicitor", "PRIVATE" },

            { "caseworker-autotest2", "PUBLIC" },
            { "caseworker-autotest2-private", "PRIVATE" },
            { "caseworker-autotest2-senior", "RESTRICTED" },
            { "caseworker-autotest2-solicitor", "PRIVATE" },

            { "caseworker-befta_jurisdiction_1", "PUBLIC" },

            { "caseworker-befta_jurisdiction_2", "PUBLIC" },
            { "caseworker-befta_jurisdiction_2-solicitor_1", "PUBLIC" },
            { "caseworker-befta_jurisdiction_2-solicitor_2", "PUBLIC" },
            { "caseworker-befta_jurisdiction_2-solicitor_3", "PUBLIC" },
            { "citizen", "PUBLIC" },

            { "caseworker-befta_jurisdiction_3", "PUBLIC" },
            { "caseworker-befta_jurisdiction_3-solicitor", "PUBLIC" }
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
        for (String[] roleInfo : CCD_ROLES_NEEDED_FOR_TA) {
            try {
                logger.info("\n\nAdding CCD Role {}, {}...", roleInfo[0], roleInfo[1]);
                addCcdRole(roleInfo[0], roleInfo[1]);
                logger.info("\n\nAdded CCD Role {}, {}...", roleInfo[0], roleInfo[1]);
            } catch (Exception e) {
                logger.error("\n\nCouldn't adding CCD Role {}, {} - Exception: {}.\\n\\n", roleInfo[0], roleInfo[1], e);
            }
        }
    }

    private void addCcdRole(String role, String classification) {
        Map<String, String> ccdRoleInfo = new HashMap<>();
        ccdRoleInfo.put("role", role);
        ccdRoleInfo.put("security_classification", classification);
        Response response = asAutoTestImporter().given().header("Content-type", "application/json").body(ccdRoleInfo)
                .when().put("/api/user-role");
        if (response.getStatusCode() / 100 != 2) {
            String message = "Import failed with response body: " + response.body().prettyPrint();
            message += "\nand http code: " + response.statusCode();
            throw new FunctionalTestException(message);
        }
    }

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
            } finally {
                TEMP_FILE.delete();
            }
        }
    }

    private List<String> getAllDefinitionFilesToLoad() {
        try {
            List<String> definitionFileResources = new ArrayList<String>();
            ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
            for (ClassPath.ResourceInfo info : cp.getResources()) {
                if (info.getResourceName().startsWith(definitionsPath)
                        && info.getResourceName().toLowerCase().endsWith(".xlsx")) {
                    definitionFileResources.add(info.getResourceName());
                }
            }
            return definitionFileResources;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void importDefinition(String fileName) throws IOException {
        InputStream stream = getClass().getClassLoader().getResource(fileName).openStream();
        copyInputStreamToFile(stream, TEMP_FILE);
        Response response = asAutoTestImporter().given().multiPart(TEMP_FILE).when().post("/import");

        if (response.getStatusCode() != 201) {
            String message = "Import failed with response body: " + response.body().prettyPrint();
            message += "\nand http code: " + response.statusCode();
            throw new FunctionalTestException(message);
        }
    }

    private void copyInputStreamToFile(InputStream inStream, File file) throws IOException {
        byte[] buffer = new byte[inStream.available()];
        inStream.read(buffer);
        OutputStream outStream = new FileOutputStream(file);
        outStream.write(buffer);
        outStream.close();
        inStream.close();
    }

    private RequestSpecification asAutoTestImporter() {
        UserData caseworker = new UserData(BeftaMain.getConfig().getImporterAutoTestEmail(),
                BeftaMain.getConfig().getImporterAutoTestPassword());
        adapter.authenticate(caseworker);

        String s2sToken = adapter.getNewS2SToken();
        return RestAssured
                .given(new RequestSpecBuilder().setBaseUri(definitionStoreUrl).build())
                .header("Authorization", "Bearer " + caseworker.getAccessToken())
                .header("ServiceAuthorization", s2sToken);
    }

}
