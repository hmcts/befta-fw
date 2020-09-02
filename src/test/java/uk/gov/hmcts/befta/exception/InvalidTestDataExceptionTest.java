/**
 * 
 */
package uk.gov.hmcts.befta.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author korneleehenry
 *
 */
class InvalidTestDataExceptionTest {
	public static final String MESSAGE = "Exception Message";
	/**
	 * Test method for {@link uk.gov.hmcts.befta.exception.InvalidTestDataException#InvalidTestDataException(java.lang.String)}.
	 */
	@Test
	void testInvalidTestDataExceptionString() {
		InvalidTestDataException ide = new InvalidTestDataException(MESSAGE);
		String actual = ide.getMessage();
		assertEquals(MESSAGE, actual);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.exception.InvalidTestDataException#InvalidTestDataException(java.lang.String, java.lang.Throwable)}.
	 */
	@Test
	void testInvalidTestDataExceptionStringThrowable() {
		InvalidTestDataException ide = new InvalidTestDataException(MESSAGE, new Exception());
		String actual = ide.getMessage();
		assertEquals(MESSAGE, actual);
		assertNotNull(ide.getCause());
	}

}
