package uk.gov.hmcts.jsonstore;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import uk.gov.hmcts.befta.data.HttpTestData;

import static org.junit.Assert.assertEquals;

public class JsonResourceStoreWithInheritanceTest {

    private JsonResourceStoreWithInheritance resourceStore;

    private static final String[] TEST_DATA_RESOURCES = {
            "framework-test-data/json-store-test-data/File-In-Root.td.json",
            "framework-test-data/json-store-test-data/test-directory/File-In-Subdirectory.td.json"
    };

    private static final String ID_KEY = "_guid_";
    private static final String FILE_IN_ROOT_ID = "File-In-Root";
    private static final String FILE_IN_SUBDIRECTORY_ID = "File-In-Subdirectory";

    @Test
    public void shouldBuildResourceStoreForAllResourcesSuccessfully() throws Exception {
        resourceStore = new JsonResourceStoreWithInheritance(TEST_DATA_RESOURCES);

        resourceStore.buildObjectStore();

        final JsonNode rootNode = resourceStore.getRootNode();
        assertEquals(2, rootNode.size());
        assertEquals(FILE_IN_ROOT_ID, rootNode.get(0).get(ID_KEY).textValue());
        assertEquals(FILE_IN_SUBDIRECTORY_ID, rootNode.get(1).get(ID_KEY).textValue());
    }

    @Test
    public void shouldGetObjectWithIdSuccessfully() throws Exception {
        resourceStore = new JsonResourceStoreWithInheritance(TEST_DATA_RESOURCES);

        final HttpTestData data = resourceStore.getObjectWithId(FILE_IN_ROOT_ID, HttpTestData.class);

        assertEquals(FILE_IN_ROOT_ID, data.get_guid_());
    }
}
