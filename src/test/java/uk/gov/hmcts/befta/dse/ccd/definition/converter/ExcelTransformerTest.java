/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author korneleehenry
 *
 */
class ExcelTransformerTest {
	public static final String DEFAULT_DEFINITIONS_PATH_JSON = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_CNP_27";
	public static final String DEFAULT_DEFINITIONS_PATH_EXCEL = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/excel/CCD_CNP_27.xlsx";
    public static final String TEMPORARY_DEFINITION_FOLDER_JSON = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definition_files/json";
    public static final String TEMPORARY_DEFINITION_FOLDER_EXCEL = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definition_files/excel";
    public static final String TEMPORARY_DEFINITION_FOLDER = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definition_files";

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.definition.converter.ExcelTransformer#transformToJson()}.
	 */
	@Test
	void testTransformToJson() {
		ExcelTransformer excelTransformer = new ExcelTransformer(DEFAULT_DEFINITIONS_PATH_EXCEL,TEMPORARY_DEFINITION_FOLDER,true);
		excelTransformer.transformToJson();
    	assertTrue(FileUtils.deleteDirectory(TEMPORARY_DEFINITION_FOLDER));
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.definition.converter.ExcelTransformer#ExcelTransformer(java.lang.String, java.lang.String, boolean)}.
	 */
	@Test
	void testExcelTransformerStringStringBoolean() {
		ExcelTransformer excelTransformer = new ExcelTransformer(DEFAULT_DEFINITIONS_PATH_EXCEL,TEMPORARY_DEFINITION_FOLDER,true);
		assertNotNull(excelTransformer);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.definition.converter.ExcelTransformer#ExcelTransformer(java.lang.String)}.
	 */
	@Test
	void testExcelTransformerString() {
		ExcelTransformer excelTransformer = new ExcelTransformer(DEFAULT_DEFINITIONS_PATH_EXCEL);
		assertNotNull(excelTransformer);
	}

}
