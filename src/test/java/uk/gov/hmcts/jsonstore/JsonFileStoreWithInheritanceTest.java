package uk.gov.hmcts.jsonstore;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.common.CommonAssertions.applyCommonAssertionsOnBasicData;
import static uk.gov.hmcts.common.CommonAssertions.applyCommonAssertionsOnExtendedData;
import static uk.gov.hmcts.common.CommonAssertions.applyCommonAssertionsOnOverriddenData;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.net.URL;

import uk.gov.hmcts.befta.data.HttpTestData;


public class JsonFileStoreWithInheritanceTest {

    private JsonFileStoreWithInheritance fileStore;

    private static final String DIRECTORIES_TEST_DATA_RESOURCE_FOLDER = "framework-test-data/json-store-test-data";
    private static final String INHERITANCE_TEST_DATA_RESOURCE_FOLDER = "framework-test-data/inheritance-test-data";

    private static final String ID_KEY = "_guid_";
    private static final String FILE_IN_ROOT_ID = "File-In-Root";
    private static final String FILE_IN_SUBDIRECTORY_ID = "File-In-Subdirectory";

    private static final String FILE_WITHOUT_INHERITANCE = "Simple-Data-Without-Inheritance";
    private static final String FILE_WITH_INHERITANCE = "Simple-Data-With-Inheritance";
    private static final String FILE_WITH_OVERRIDES = "Simple-Data-With-Overrides";

    @Test
    public void shouldBuildFileStoreForAllSubdirectoriesSuccessfully() throws Exception {
        fileStore = new JsonFileStoreWithInheritance(getFileFromResource(DIRECTORIES_TEST_DATA_RESOURCE_FOLDER));

        fileStore.buildObjectStore();

        final JsonNode rootNode = fileStore.getRootNode();
        assertEquals(2, rootNode.size());
        assertEquals(FILE_IN_ROOT_ID, rootNode.get(0).get(ID_KEY).textValue());
        assertEquals(FILE_IN_SUBDIRECTORY_ID, rootNode.get(1).get(ID_KEY).textValue());
    }

    @Test
    public void shouldGetObjectWithIdForBasicDataSuccessfully() throws Exception {
        fileStore = new JsonFileStoreWithInheritance(getFileFromResource(INHERITANCE_TEST_DATA_RESOURCE_FOLDER));

        final HttpTestData data = fileStore.getObjectWithId(FILE_WITHOUT_INHERITANCE, HttpTestData.class);

        applyCommonAssertionsOnBasicData(data);
    }

    @Test
    public void shouldGetObjectWithIdForInheritedDataSuccessfully() throws Exception {
        fileStore = new JsonFileStoreWithInheritance(getFileFromResource(INHERITANCE_TEST_DATA_RESOURCE_FOLDER));

        final HttpTestData data = fileStore.getObjectWithId(FILE_WITH_INHERITANCE, HttpTestData.class);

        applyCommonAssertionsOnExtendedData(data);
    }

    @Test
    public void shouldGetObjectWithIdForOverriddenDataSuccessfully() throws Exception {
        fileStore = new JsonFileStoreWithInheritance(getFileFromResource(INHERITANCE_TEST_DATA_RESOURCE_FOLDER));

        final HttpTestData data = fileStore.getObjectWithId(FILE_WITH_OVERRIDES, HttpTestData.class);

        applyCommonAssertionsOnOverriddenData(data);
    }

    private File getFileFromResource(String location) {
        URL url = ClassLoader.getSystemResource(location);
        return new File(url.getFile());
    }
}
