/**
 * 
 */
package uk.gov.hmcts.befta.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * @author korneleehenry
 *
 */
class DefinitionTransformerExceptionTest {

    public static final String MESSAGE = "Exception Message";

	/**
	 * Test method for {@link uk.gov.hmcts.befta.exception.DefinitionTransformerException#DefinitionTransformerException(java.lang.String)}.
	 */
	@Test
	void testDefinitionTransformerExceptionString() {
		DefinitionTransformerException ide = new DefinitionTransformerException(MESSAGE);
		String actual = ide.getMessage();
		assertEquals(MESSAGE, actual);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.exception.DefinitionTransformerException#DefinitionTransformerException(java.lang.String, java.lang.Throwable)}.
	 */
	@Test
	void testDefinitionTransformerExceptionStringThrowable() {
		DefinitionTransformerException ide = new DefinitionTransformerException(MESSAGE, new Exception());
		String actual = ide.getMessage();
		assertEquals(MESSAGE, actual);
		assertNotNull(ide.getCause());
	}

}
