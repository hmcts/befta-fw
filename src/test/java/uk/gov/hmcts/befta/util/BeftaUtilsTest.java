/**
 * 
 */
package uk.gov.hmcts.befta.util;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.exception.JsonStoreCreationException;

/**
 * @author korneleehenry
 *
 */
class BeftaUtilsTest {
	private static final String[] MULT_STRS = {"tempdirectory","tempfilepath"};
    private static final String TEMPORARY_DEFINITION_FOLDER = "definition_files";
    private static final String TEMPORARY_FILE_NAME = "framework-test-data/json-store-test-data";
    private static final String TEMPORARY_SIMPLE_FOLDER = "framework-test-data/json-store-test-data/test-directory";
	private static final String[] SINGLE_STR = {TEMPORARY_FILE_NAME};
	/**
	 * Test method for {@link uk.gov.hmcts.befta.util.BeftaUtils#getSingleFileFromResource(java.lang.String[])}.
	 */
	@Test
	void testGetSingleFileFromResourceMulty() {
		JsonStoreCreationException aeThrown = Assertions.assertThrows(JsonStoreCreationException.class,
				() -> BeftaUtils.getSingleFileFromResource(MULT_STRS),
				"JsonStoreCreationException is not thrown");
		assertTrue(aeThrown.getMessage().contains("Invalid parameter, for array with single entry a Signle directory or a file location."));
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.util.BeftaUtils#getSingleFileFromResource(java.lang.String[])}.
	 */
	@Test
	void testGetSingleFileFromResource() {
		File actual = BeftaUtils.getSingleFileFromResource(SINGLE_STR);
		String expected = new File(ClassLoader.getSystemResource(SINGLE_STR[0]).getFile()).getAbsolutePath();
		assertNotNull(actual);
		assertEquals(expected,actual.getAbsolutePath());
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.util.BeftaUtils#getFileFromResource(java.lang.String)}.
	 */
	@Test
	void testGetFileFromResource() {
		File actual = BeftaUtils.getFileFromResource(SINGLE_STR[0]);
		String expected = new File(ClassLoader.getSystemResource(SINGLE_STR[0]).getFile()).getAbsolutePath();
		assertNotNull(actual);
		assertEquals(expected,actual.getAbsolutePath());
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.util.BeftaUtils#getClassPathResourceIntoTemporaryFile(java.lang.String)}.
	 */
	@Test
	void testGetClassPathResourceIntoTemporaryFile() {
		File actual = BeftaUtils.getClassPathResourceIntoTemporaryFile(TEMPORARY_FILE_NAME);
		assertNotNull(actual);
		assertTrue(actual.delete());
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.util.BeftaUtils#getClassPathResourceIntoTemporaryFile(java.lang.String)}.
	 */
	@Test
	void testGetClassPathResourceIntoTemporaryFileNeg() {
		FunctionalTestException aeThrown = Assertions.assertThrows(FunctionalTestException.class,
				() -> BeftaUtils.getClassPathResourceIntoTemporaryFile(TEMPORARY_DEFINITION_FOLDER),
				"FunctionalTestException is not thrown");
		assertTrue(aeThrown.getMessage().contains("Failed to load from filePath: " + TEMPORARY_DEFINITION_FOLDER));

	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.util.BeftaUtils#createJsonDefinitionFileFromClasspath(java.lang.String)}.
	 */
	@Test
	void testCreateJsonDefinitionFileFromClasspath() {
		File actual = BeftaUtils.createJsonDefinitionFileFromClasspath(TEMPORARY_SIMPLE_FOLDER);
		assertNotNull(actual);
		assertTrue(actual.delete());
	}

}
