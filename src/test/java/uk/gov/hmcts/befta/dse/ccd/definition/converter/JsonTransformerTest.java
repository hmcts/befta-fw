/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import java.io.File;
import java.io.IOException;

import uk.gov.hmcts.befta.util.FileUtils;

/**
 * @author korneleehenry
 *
 */
class JsonTransformerTest {

    private static final String DEFAULT_DEFINITIONS_PATH_JSON_EMPTY_1_FILE = "src/test/resources/uk/gov/hmcts/befta/dse/ccd/definitions/invalid/test/1/empty.json";
    private static final String DEFAULT_DEFINITIONS_PATH_JSON_EMPTY = "src/test/resources/uk/gov/hmcts/befta/dse/ccd/definitions/invalid/test";
    private static final String DEFAULT_DEFINITIONS_PATH_JSON = "src/test/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_CNP_27";

    private static final String TEMPORARY_DEFINITION_FOLDER = "temp_dir/JsonTransformerTest/"
            + System.currentTimeMillis()
    + "/definition_files";
    private static final String TEMPORARY_DEFINITION_FOLDER_EXCEL = TEMPORARY_DEFINITION_FOLDER + "/excel";

    @BeforeEach
    void setup() {
		File tempemptyFile = new File(DEFAULT_DEFINITIONS_PATH_JSON_EMPTY_1_FILE);
		File tempDir = tempemptyFile.getParentFile();
		FileUtils.createDirectoryHierarchy(tempDir);
		try {
			tempemptyFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
    @AfterEach
    void cleanup() {
    	File tempemptyFile = new File(DEFAULT_DEFINITIONS_PATH_JSON_EMPTY_1_FILE);
    	tempemptyFile.delete();
        FileUtils.deleteDirectory(DEFAULT_DEFINITIONS_PATH_JSON_EMPTY);
		FileUtils.deleteDirectory(TEMPORARY_DEFINITION_FOLDER);
    }
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer#transformToExcel()}.
	 */
	@Test
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
	void testTransformToExcel() {
		JsonTransformer jsonTransformer = new JsonTransformer(DEFAULT_DEFINITIONS_PATH_JSON,null);
		String expected = DEFAULT_DEFINITIONS_PATH_JSON+".xlsx";
		File tempfile = new File(expected);
		String actual = jsonTransformer.transformToExcel();
		assertEquals(expected,actual);
    	assertTrue(tempfile.delete());
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer#JsonTransformer(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testJsonTransformerStringString() {
		JsonTransformer jsonTransformer = new JsonTransformer(DEFAULT_DEFINITIONS_PATH_JSON,TEMPORARY_DEFINITION_FOLDER_EXCEL);
		assertNotNull(jsonTransformer);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer#JsonTransformer(java.lang.String)}.
	 */
	@Test
	void testJsonTransformerString() {
		JsonTransformer jsonTransformer = new JsonTransformer(DEFAULT_DEFINITIONS_PATH_JSON);
		assertNotNull(jsonTransformer);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer#parseDefinitionJson(java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
	void testParseDefinitionJson() throws IOException {
		JsonTransformer jsonTransformer = new JsonTransformer(DEFAULT_DEFINITIONS_PATH_JSON_EMPTY);
    	jsonTransformer.parseDefinitionJson(DEFAULT_DEFINITIONS_PATH_JSON_EMPTY);;
		assertNotNull(jsonTransformer);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer#createWorkbook(java.lang.String)}.
	 */
	@Test
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
	void testCreateWorkbook() {
		JsonTransformer jsonTransformer = new JsonTransformer(DEFAULT_DEFINITIONS_PATH_JSON,TEMPORARY_DEFINITION_FOLDER_EXCEL);
		String expected = TEMPORARY_DEFINITION_FOLDER_EXCEL+"/CCD_CNP_27.xlsx";
		File tempfile = new File(expected);
		String actual = jsonTransformer.transformToExcel();
		assertEquals(expected,actual);
    	assertTrue(tempfile.delete());
	}

}
