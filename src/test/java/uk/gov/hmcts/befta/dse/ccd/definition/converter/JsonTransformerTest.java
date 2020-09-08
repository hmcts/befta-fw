/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author korneleehenry
 *
 */
@Ignore
class JsonTransformerTest {

    public static final String DEFAULT_DEFINITIONS_PATH_JSON_EMPTY_1_FILE = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/invalid/test/1/empty.json";
	public static final String DEFAULT_DEFINITIONS_PATH_JSON_EMPTY_1 = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/invalid/test/1";
	public static final String DEFAULT_DEFINITIONS_PATH_JSON_EMPTY = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/invalid/test";
	public static final String DEFAULT_DEFINITIONS_PATH_JSON = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_CNP_27";
	public static final String DEFAULT_DEFINITIONS_PATH_EXCEL = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/excel/CCD_CNP_27.xlsx";
    public static final String TEMPORARY_DEFINITION_FOLDER_JSON = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definition_files/json";
    public static final String TEMPORARY_DEFINITION_FOLDER_EXCEL = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definition_files/excel";
    public static final String TEMPORARY_DEFINITION_FOLDER = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definition_files";
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
	void testParseDefinitionJson() throws IOException {
		JsonTransformer jsonTransformer = new JsonTransformer(DEFAULT_DEFINITIONS_PATH_JSON_EMPTY);
    	jsonTransformer.parseDefinitionJson(DEFAULT_DEFINITIONS_PATH_JSON_EMPTY);;
		assertNotNull(jsonTransformer);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer#createWorkbook(java.lang.String)}.
	 */
	@Test
	void testCreateWorkbook() {
		JsonTransformer jsonTransformer = new JsonTransformer(DEFAULT_DEFINITIONS_PATH_JSON,TEMPORARY_DEFINITION_FOLDER_EXCEL);
		String expected = TEMPORARY_DEFINITION_FOLDER_EXCEL+"/CCD_CNP_27.xlsx";
		File tempfile = new File(expected);
		String actual = jsonTransformer.transformToExcel();
		assertEquals(expected,actual);
    	assertTrue(tempfile.delete());
	}

}
