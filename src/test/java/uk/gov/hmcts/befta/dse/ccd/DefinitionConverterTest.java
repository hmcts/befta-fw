/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import java.io.File;

import uk.gov.hmcts.befta.util.FileUtils;


/**
 * @author korneleehenry
 *
 */
class DefinitionConverterTest {

    private static final String DEFAULT_DEFINITIONS_PATH_JSON = "src/test/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_CNP_27";
    private static final String DEFAULT_DEFINITIONS_PATH_EXCEL = "src/test/resources/uk/gov/hmcts/befta/dse/ccd/definitions/excel/CCD_CNP_27.xlsx";
    private static final String TEMPORARY_DEFINITION_FOLDER = "temp_dir/DefinitionConverterTest/"
            + System.currentTimeMillis()
            + "/definition_files";
    private static final String TEMPORARY_DEFINITION_FOLDER_JSON = TEMPORARY_DEFINITION_FOLDER + "/json";
    private static final String TEMPORARY_DEFINITION_FOLDER_EXCEL = TEMPORARY_DEFINITION_FOLDER + "/excel";

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainInsufficientArgs() {
		String[] args = {""};
		IllegalArgumentException aeThrown = Assertions.assertThrows(IllegalArgumentException.class, () ->
        	DefinitionConverter.main(args),
				"IllegalArgumentException is not thrown");
        assertEquals(aeThrown
                .getMessage(),
                "First arg should be either 'to-json' or 'to-excel' but got ''");
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainIncorrectArgs() {
        String[] args = { "", "", CcdEnvironment.PREVIEW.name() };
		IllegalArgumentException aeThrown = Assertions.assertThrows(IllegalArgumentException.class, () ->
        	DefinitionConverter.main(args),
				"IllegalArgumentException is not thrown");
		assertTrue(aeThrown.getMessage().contains("First arg should be either 'to-json' or 'to-excel'"));
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainIncorrectArgspos() {
        String[] args = { "to-json", "definition_files", "true" };
		IllegalArgumentException aeThrown = Assertions.assertThrows(IllegalArgumentException.class, () ->
        	DefinitionConverter.main(args),
				"IllegalArgumentException is not thrown");
        assertTrue(aeThrown.getMessage()
                .contains("Fourth arg should be a path not a boolean, if you wish to set Jurisdiction"));
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainIncorrectArgsBool() {
        String[] args = { "to-json", "definition_files", CcdEnvironment.PREVIEW.name(), "null", "tru1e" };
		IllegalArgumentException aeThrown = Assertions.assertThrows(IllegalArgumentException.class, () ->
        	DefinitionConverter.main(args),
				"IllegalArgumentException is not thrown");
        assertTrue(aeThrown.getMessage().contains("Fifth arg should be a boolean but got: "));
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainArgsTojsonTrue() {
        String[] args = { "to-json", DEFAULT_DEFINITIONS_PATH_EXCEL,
                TEMPORARY_DEFINITION_FOLDER_JSON, "true" };
    	DefinitionConverter.main(args);
    	assertTrue(FileUtils.deleteDirectory(TEMPORARY_DEFINITION_FOLDER));
	}

	@Test
	void testMainArgsTojsonFalse() {
        String[] args = { "to-json", DEFAULT_DEFINITIONS_PATH_EXCEL,
                TEMPORARY_DEFINITION_FOLDER_JSON, "false" };
    	DefinitionConverter.main(args);
    	assertTrue(FileUtils.deleteDirectory(TEMPORARY_DEFINITION_FOLDER));
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
	void testMainArgsTojson() {
        String tempJsonFolder = "src/test/resources/uk/gov/hmcts/befta/dse/ccd/definitions/excel/AUTOTEST1";
        String[] args = { "to-json", DEFAULT_DEFINITIONS_PATH_EXCEL };
    	DefinitionConverter.main(args);
    	assertTrue(FileUtils.deleteDirectory(tempJsonFolder));
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
	void testMainArgsToexcelTrue() {
        String[] args = { "to-excel", DEFAULT_DEFINITIONS_PATH_JSON, CcdEnvironment.PREVIEW.name(),
                TEMPORARY_DEFINITION_FOLDER_EXCEL, "true" };
    	DefinitionConverter.main(args);
    	assertTrue(FileUtils.deleteDirectory(TEMPORARY_DEFINITION_FOLDER));
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
	void testMainArgsToexcelfalse() {
        String[] args = { "to-excel", DEFAULT_DEFINITIONS_PATH_JSON, CcdEnvironment.PREVIEW.name(),
                TEMPORARY_DEFINITION_FOLDER_EXCEL, "false" };
    	DefinitionConverter.main(args);
        assertTrue(FileUtils.deleteDirectory(TEMPORARY_DEFINITION_FOLDER));
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
    @SetEnvironmentVariable(key = "TEST_URL", value = "http://localhost:8080/dummy-api")
	void testMainArgsToexcel() {
        String tempExcel = "src/test/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_CNP_27.xlsx";
		File tempfile = new File(tempExcel);
        String[] args = { "to-excel", DEFAULT_DEFINITIONS_PATH_JSON, CcdEnvironment.PREVIEW.name() };
    	DefinitionConverter.main(args);
    	assertTrue(tempfile.delete());
	}

}
