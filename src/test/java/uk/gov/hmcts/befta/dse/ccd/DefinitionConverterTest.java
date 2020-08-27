/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import uk.gov.hmcts.befta.dse.ccd.definition.converter.FileUtils;


/**
 * @author korneleehenry
 *
 */
class DefinitionConverterTest {
	public static final String DEFAULT_DEFINITIONS_PATH_JSON = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_CNP_27";
	public static final String DEFAULT_DEFINITIONS_PATH_EXCEL = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/excel/CCD_CNP_27.xlsx";
    public static final String TEMPORARY_DEFINITION_FOLDER_JSON = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definition_files/json";
    public static final String TEMPORARY_DEFINITION_FOLDER_EXCEL = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definition_files/excel";
    public static final String TEMPORARY_DEFINITION_FOLDER = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definition_files";

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainInsufficientArgs() {
		String[] args = {""};
		IllegalArgumentException aeThrown = Assertions.assertThrows(IllegalArgumentException.class, () ->
        	DefinitionConverter.main(args),
				"IllegalArgumentException is not thrown");
		assertTrue(aeThrown.getMessage().contains("At least 2 arguments expected: <to-json|to-excel> <input folder/file path> "));
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainIncorrectArgs() {
		String[] args = {"",""};
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
		String[] args = {"to-json","definition_files","true"};
		IllegalArgumentException aeThrown = Assertions.assertThrows(IllegalArgumentException.class, () ->
        	DefinitionConverter.main(args),
				"IllegalArgumentException is not thrown");
		assertTrue(aeThrown.getMessage().contains("Third arg should be a path not a boolean, if you wish to set Jurisdiction"));
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainIncorrectArgsBool() {
		String[] args = {"to-json","definition_files","null","tru1e"};
		IllegalArgumentException aeThrown = Assertions.assertThrows(IllegalArgumentException.class, () ->
        	DefinitionConverter.main(args),
				"IllegalArgumentException is not thrown");
		assertTrue(aeThrown.getMessage().contains("Forth arg should be a boolean but got: "));
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainArgsTojsonTrue() {
		String[] args = {"to-json",DEFAULT_DEFINITIONS_PATH_EXCEL,TEMPORARY_DEFINITION_FOLDER_JSON,"true"};
    	DefinitionConverter.main(args);
    	assertTrue(FileUtils.deleteDirectory(TEMPORARY_DEFINITION_FOLDER));
	}
	@Test
	void testMainArgsTojsonFalse() {
		String[] args = {"to-json",DEFAULT_DEFINITIONS_PATH_EXCEL,TEMPORARY_DEFINITION_FOLDER_JSON,"false"};
    	DefinitionConverter.main(args);
    	assertTrue(FileUtils.deleteDirectory(TEMPORARY_DEFINITION_FOLDER));
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainArgsTojson() {
		String tempJsonFolder = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/excel/AUTOTEST1";
		String[] args = {"to-json",DEFAULT_DEFINITIONS_PATH_EXCEL};
    	DefinitionConverter.main(args);
    	assertTrue(FileUtils.deleteDirectory(tempJsonFolder));
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainArgsToexcelTrue() {
		String[] args = {"to-excel",DEFAULT_DEFINITIONS_PATH_JSON,TEMPORARY_DEFINITION_FOLDER_EXCEL,"true"};
    	DefinitionConverter.main(args);
    	assertTrue(FileUtils.deleteDirectory(TEMPORARY_DEFINITION_FOLDER));
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainArgsToexcelfalse() {
		String[] args = {"to-excel",DEFAULT_DEFINITIONS_PATH_JSON,TEMPORARY_DEFINITION_FOLDER_EXCEL,"false"};
    	DefinitionConverter.main(args);
    	assertTrue(FileUtils.deleteDirectory(TEMPORARY_DEFINITION_FOLDER));
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.DefinitionConverter#main(java.lang.String[])}.
	 */
	@Test
	void testMainArgsToexcel() {
		String tempExcel = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_CNP_27.xlsx";
		File tempfile = new File(tempExcel);
		String[] args = {"to-excel",DEFAULT_DEFINITIONS_PATH_JSON};
    	DefinitionConverter.main(args);
    	assertTrue(tempfile.delete());
	}
}
