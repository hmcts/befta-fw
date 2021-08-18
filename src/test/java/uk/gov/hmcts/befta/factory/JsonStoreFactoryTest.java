package uk.gov.hmcts.befta.factory;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import uk.gov.hmcts.befta.exception.JsonStoreCreationException;
import uk.gov.hmcts.jsonstore.JsonFileStoreWithInheritance;
import uk.gov.hmcts.jsonstore.JsonResourceStoreWithInheritance;
import uk.gov.hmcts.jsonstore.JsonStoreWithInheritance;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link uk.gov.hmcts.befta.factory.JsonStoreFactory}.
 *
 * @author korneleehenry
 *
 */
@SuppressWarnings("UnnecessaryLocalVariable")
class JsonStoreFactoryTest {

    private static final String FILE_STR = "FILE";
    private static final String FILE_WITH_INHERITANCE = "framework-test-data/json-store-test-data";
    private static final String GUID = "_guid_";
    private static final String EXTENDS = "_extends_";

    @Test
    void testCreateJsonStoreFile() {
        String jsonStoreOption = FILE_STR;
        String[] resourcePaths = { FILE_WITH_INHERITANCE };
        JsonStoreWithInheritance actual = JsonStoreFactory.createJsonStoreWithInheritance(jsonStoreOption,
                resourcePaths);
        MatcherAssert.assertThat(actual, IsInstanceOf.instanceOf(JsonFileStoreWithInheritance.class));
    }

    @Test
    void testCreateJsonStoreFileInValid() {
        String jsonStoreOption = FILE_STR;
        String[] resourcePaths = { FILE_WITH_INHERITANCE, "InValid" };
        JsonStoreCreationException aeThrown = Assertions.assertThrows(JsonStoreCreationException.class,
            () -> JsonStoreFactory.createJsonStoreWithInheritance(jsonStoreOption,
                resourcePaths),
            "JsonStoreCreationException is not thrown");
        assertTrue(aeThrown.getMessage()
                .contains("Invalid parameter, for array with single entry a single directory or a file location."));
    }

    @Test
    void testCreateJsonStoreFileParam() {
        String jsonStoreOption = FILE_STR;
        String[] resourcePaths = { FILE_WITH_INHERITANCE };
        JsonStoreWithInheritance actual =
            JsonStoreFactory.createJsonStoreWithInheritance(jsonStoreOption, resourcePaths, GUID, EXTENDS);
        MatcherAssert.assertThat(actual, IsInstanceOf.instanceOf(JsonFileStoreWithInheritance.class));
    }

    @Test
    void testCreateJsonStoreFileInValidParam() {
        String jsonStoreOption = FILE_STR;
        String[] resourcePaths = { FILE_WITH_INHERITANCE, "InValid" };
        JsonStoreCreationException aeThrown = Assertions.assertThrows(JsonStoreCreationException.class,
            () -> JsonStoreFactory.createJsonStoreWithInheritance(jsonStoreOption, resourcePaths),
            "JsonStoreCreationException is not thrown");
        assertTrue(aeThrown.getMessage()
                .contains("Invalid parameter, for array with single entry a single directory or a file location."));
    }

    @Test
    void testCreateJsonStoreResouceNull() {
        String jsonStoreOption = "default";
        String[] resourcePaths = null;
        JsonStoreWithInheritance actual = JsonStoreFactory.createJsonStoreWithInheritance(jsonStoreOption,
                resourcePaths);
        MatcherAssert.assertThat(actual, IsInstanceOf.instanceOf(JsonResourceStoreWithInheritance.class));
    }

    @Test
    void testCreateJsonStoreResource() {
        String jsonStoreOption = "default";
        String[] resourcePaths = { FILE_WITH_INHERITANCE };
        JsonStoreWithInheritance actual = JsonStoreFactory.createJsonStoreWithInheritance(jsonStoreOption,
                resourcePaths);
        MatcherAssert.assertThat(actual, IsInstanceOf.instanceOf(JsonResourceStoreWithInheritance.class));
    }

    @Test
    void testCreateJsonStoreResouceNullWithParam() {
        String jsonStoreOption = "default";
        String[] resourcePaths = null;
        JsonStoreWithInheritance actual =
            JsonStoreFactory.createJsonStoreWithInheritance(jsonStoreOption, resourcePaths, GUID, EXTENDS);
        MatcherAssert.assertThat(actual, IsInstanceOf.instanceOf(JsonResourceStoreWithInheritance.class));
    }

    @Test
    void testCreateJsonStoreResourceWithParam() {
        String jsonStoreOption = "default";
        String[] resourcePaths = { FILE_WITH_INHERITANCE };
        JsonStoreWithInheritance actual =
            JsonStoreFactory.createJsonStoreWithInheritance(jsonStoreOption, resourcePaths, GUID, EXTENDS);
        MatcherAssert.assertThat(actual, IsInstanceOf.instanceOf(JsonResourceStoreWithInheritance.class));
    }
}
