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
class JsonStoreCreationExceptionTest {
	public static final String MESSAGE = "Exception Message";

	/**
	 * Test method for {@link uk.gov.hmcts.befta.exception.JsonStoreCreationException#JsonStoreCreationException(java.lang.String, java.lang.Throwable)}.
	 */
	@Test
	void testJsonStoreCreationExceptionStringThrowable() {
		JsonStoreCreationException ide = new JsonStoreCreationException(MESSAGE, new Exception());
		String actual = ide.getMessage();
		assertEquals(MESSAGE, actual);
		assertNotNull(ide.getCause());
	}

}
