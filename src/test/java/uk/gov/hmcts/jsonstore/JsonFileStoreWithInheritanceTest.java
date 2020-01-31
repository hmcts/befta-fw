package uk.gov.hmcts.jsonstore;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import uk.gov.hmcts.befta.data.HttpTestData;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;


public class JsonFileStoreWithInheritanceTest {

    private JsonFileStoreWithInheritance fileStore;

    private static final String TEST_DATA_RESOURCE_FOLDER = "framework-test-data/json-store-test-data";

    private static final String ID_KEY = "_guid_";
    private static final String FILE_IN_ROOT_ID = "File-In-Root";
    private static final String FILE_IN_SUBDIRECTORY_ID = "File-In-Subdirectory";

    @Test
    public void shouldBuildFileStoreForAllSubdirectoriesSuccessfully() throws Exception {
        fileStore = new JsonFileStoreWithInheritance(getFileFromResource(TEST_DATA_RESOURCE_FOLDER));

        fileStore.buildObjectStore();

        final JsonNode rootNode = fileStore.getRootNode();
        assertEquals(2, rootNode.size());
        assertEquals(FILE_IN_ROOT_ID, rootNode.get(0).get(ID_KEY).textValue());
        assertEquals(FILE_IN_SUBDIRECTORY_ID, rootNode.get(1).get(ID_KEY).textValue());
    }

    @Test
    public void shouldGetObjectWithIdSuccessfully() throws Exception {
        fileStore = new JsonFileStoreWithInheritance(getFileFromResource(TEST_DATA_RESOURCE_FOLDER));

        final HttpTestData data = fileStore.getObjectWithId(FILE_IN_ROOT_ID, HttpTestData.class);

        assertEquals(FILE_IN_ROOT_ID, data.get_guid_());
    }

    private File getFileFromResource(String location) {
        URL url = ClassLoader.getSystemResource(location);
        return new File(url.getFile());
    }
}
