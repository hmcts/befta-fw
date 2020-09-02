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
class ParentNotFoundExceptionTest {
	public static final String MESSAGE = "Exception Message";

	/**
	 * Test method for {@link uk.gov.hmcts.befta.exception.ParentNotFoundException#ParentNotFoundException(java.lang.String)}.
	 */
	@Test
	void testParentNotFoundExceptionString() {
		ParentNotFoundException ide = new ParentNotFoundException(MESSAGE);
		String actual = ide.getMessage();
		assertEquals(MESSAGE, actual);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.exception.ParentNotFoundException#ParentNotFoundException(java.lang.String, java.lang.Throwable)}.
	 */
	@Test
	void testParentNotFoundExceptionStringThrowable() {
		ParentNotFoundException ide = new ParentNotFoundException(MESSAGE, new Exception());
		String actual = ide.getMessage();
		assertEquals(MESSAGE, actual);
		assertNotNull(ide.getCause());
	}

}
