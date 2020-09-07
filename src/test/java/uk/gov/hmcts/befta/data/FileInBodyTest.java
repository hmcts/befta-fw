/**
 * 
 */
package uk.gov.hmcts.befta.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * @author korneleehenry
 *
 */
class FileInBodyTest {

    public static final String DEFAULT_DEFINITIONS_PATH_JSON = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_CNP_27";

	/**
	 * Test method for {@link uk.gov.hmcts.befta.data.FileInBody#FileInBody(java.lang.String)}.
	 */
	@Test
	void testFileInBody() {
		FileInBody actual = new FileInBody(DEFAULT_DEFINITIONS_PATH_JSON);
		assertEquals(DEFAULT_DEFINITIONS_PATH_JSON,actual.getFullPath());
	}

	@Test
	void testFileInBodyEquals() {
		FileInBody actual = new FileInBody(DEFAULT_DEFINITIONS_PATH_JSON);
		FileInBody other = new FileInBody(DEFAULT_DEFINITIONS_PATH_JSON);
		assertEquals(other,actual);
		assertEquals(other.hashCode(),actual.hashCode());
	}

	@Test
	void testFileInBodyNotEquals() {
		FileInBody actual = new FileInBody(DEFAULT_DEFINITIONS_PATH_JSON);
		FileInBody other = new FileInBody("");
		other.setContentHash("contentHash");
		other.setFullPath(DEFAULT_DEFINITIONS_PATH_JSON);
		other.setSize("size");
		assertNotEquals(null,actual);
		assertNotEquals(other,actual);
		assertNotEquals(other.hashCode(),actual.hashCode());
		assertNotEquals(other.toString(),actual.toString());
	}

}
