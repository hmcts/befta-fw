/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import java.io.File;

import uk.gov.hmcts.befta.util.FileUtils;

/**
 * @author korneleehenry
 *
 */
class CcdBeftaUtilsTest {

    public static final String DEFAULT_DEFINITIONS_PATH_JSON = "src/test/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_CNP_27";
    public static final String DEFAULT_DEFINITIONS_PATH_EXCEL = "src/test/resources/uk/gov/hmcts/befta/dse/ccd/definitions/excel/CCD_CNP_27.xlsx";
    public static final String DEFAULT_DEFINITIONS_PATH_FOLDER = "src/test/resources/uk/gov/hmcts/befta/dse/ccd/definitions/excel/AUTOTEST1";
    File excelFile = null;
    File jsonFolder = null;
    File tempFolder = null;

    @BeforeEach
    void setup() {
        excelFile = new File(DEFAULT_DEFINITIONS_PATH_EXCEL);
        jsonFolder = new File(DEFAULT_DEFINITIONS_PATH_JSON);
        tempFolder = new File(DEFAULT_DEFINITIONS_PATH_FOLDER);
    }

    @AfterEach
    void cleanup() {
        excelFile = null;
        jsonFolder = null;
        tempFolder = null;
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.dse.ccd.CcdBeftaUtils#convertJsonDefinitionToExcel(java.lang.String)}.
     */
    @Test
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
    void testConvertJsonDefinitionToExcelString() {

        String expected = DEFAULT_DEFINITIONS_PATH_JSON + ".xlsx";
        File tempfile = new File(expected);
        File actual = CcdBeftaUtils.convertJsonDefinitionToExcel(DEFAULT_DEFINITIONS_PATH_JSON, CcdEnvironment.PREVIEW);
        assertEquals(tempfile, actual);
        assertTrue(tempfile.delete());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.dse.ccd.CcdBeftaUtils#convertJsonDefinitionToExcel(java.io.File)}.
     */
    @Test
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
    void testConvertJsonDefinitionToExcelFile() {
        String expected = DEFAULT_DEFINITIONS_PATH_JSON + ".xlsx";
        File tempfile = new File(expected);
        File actual = CcdBeftaUtils.convertJsonDefinitionToExcel(jsonFolder, CcdEnvironment.PREVIEW);
        assertEquals(tempfile, actual);
        assertTrue(tempfile.delete());
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.dse.ccd.CcdBeftaUtils#convertExcelFileToJson(java.lang.String)}.
     */
    @Test
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
    void testConvertExcelFileToJsonString() {
        File actual = CcdBeftaUtils.convertExcelFileToJson(DEFAULT_DEFINITIONS_PATH_EXCEL);
        assertEquals(tempFolder, actual);
        assertTrue(FileUtils.deleteDirectory(actual));
    }

    /**
     * Test method for
     * {@link uk.gov.hmcts.befta.dse.ccd.CcdBeftaUtils#convertExcelFileToJson(java.io.File)}.
     */
    @Test
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
    void testConvertExcelFileToJsonFile() {
        File actual = CcdBeftaUtils.convertExcelFileToJson(excelFile);
        assertEquals(tempFolder, actual);
        assertTrue(FileUtils.deleteDirectory(actual));
    }

}
