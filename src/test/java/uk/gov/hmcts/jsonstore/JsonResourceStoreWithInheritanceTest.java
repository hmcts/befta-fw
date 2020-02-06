package uk.gov.hmcts.jsonstore;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import uk.gov.hmcts.befta.data.HttpTestData;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.common.CommonAssertions.*;

public class JsonResourceStoreWithInheritanceTest {

    private JsonResourceStoreWithInheritance resourceStore;

    private static final String[] DIRECTORIES_TEST_DATA_RESOURCES = {
            "framework-test-data/json-store-test-data/File-In-Root.td.json",
            "framework-test-data/json-store-test-data/test-directory/File-In-Subdirectory.td.json"
    };

    private static final String[] INHERITANCE_TEST_DATA_RESOURCES = {
            "framework-test-data/inheritance-test-data/Simple-Data-Without-Inheritance.td.json",
            "framework-test-data/inheritance-test-data/Simple-Data-With-Inheritance.td.json",
            "framework-test-data/inheritance-test-data/Simple-Data-With-Overrides.td.json",
    };

    private static final String ID_KEY = "_guid_";
    private static final String FILE_IN_ROOT_ID = "File-In-Root";
    private static final String FILE_IN_SUBDIRECTORY_ID = "File-In-Subdirectory";

    private static final String FILE_WITHOUT_INHERITANCE = "Simple-Data-Without-Inheritance";
    private static final String FILE_WITH_INHERITANCE = "Simple-Data-With-Inheritance";
    private static final String FILE_WITH_OVERRIDES = "Simple-Data-With-Overrides";

    @Test
    public void shouldBuildResourceStoreForAllResourcesSuccessfully() throws Exception {
        resourceStore = new JsonResourceStoreWithInheritance(DIRECTORIES_TEST_DATA_RESOURCES);

        resourceStore.buildObjectStore();

        final JsonNode rootNode = resourceStore.getRootNode();
        assertEquals(2, rootNode.size());
        assertEquals(FILE_IN_ROOT_ID, rootNode.get(0).get(ID_KEY).textValue());
        assertEquals(FILE_IN_SUBDIRECTORY_ID, rootNode.get(1).get(ID_KEY).textValue());
    }

    @Test
    public void shouldGetObjectWithIdForBasicDataSuccessfully() throws Exception {
        resourceStore = new JsonResourceStoreWithInheritance(INHERITANCE_TEST_DATA_RESOURCES);

        final HttpTestData data = resourceStore.getObjectWithId(FILE_WITHOUT_INHERITANCE, HttpTestData.class);

        applyCommonAssertionsOnBasicData(data);
    }

    @Test
    public void shouldGetObjectWithIdForInheritedDataSuccessfully() throws Exception {
        resourceStore = new JsonResourceStoreWithInheritance(INHERITANCE_TEST_DATA_RESOURCES);

        final HttpTestData data = resourceStore.getObjectWithId(FILE_WITH_INHERITANCE, HttpTestData.class);

        applyCommonAssertionsOnExtendedData(data);
    }

    @Test
    public void shouldGetObjectWithIdForOverriddenDataSuccessfully() throws Exception {
        resourceStore = new JsonResourceStoreWithInheritance(INHERITANCE_TEST_DATA_RESOURCES);

        final HttpTestData data = resourceStore.getObjectWithId(FILE_WITH_OVERRIDES, HttpTestData.class);

        applyCommonAssertionsOnOverriddenData(data);
    }
}
