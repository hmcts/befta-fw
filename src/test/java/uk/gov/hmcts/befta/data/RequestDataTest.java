/**
 * 
 */
package uk.gov.hmcts.befta.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.HashMap;


/**
 * @author korneleehenry
 *
 */
class RequestDataTest {

	/**
	 * Test method for {@link uk.gov.hmcts.befta.data.RequestData#isMultipart()}.
	 */
	@Test
	void testIsMultipart() {
		RequestData actual = new RequestData();
		assertFalse(actual.isMultipart());
	}

	@Test
	void testIsMultipartTrue() {
		RequestData actual = new RequestData();
		actual.setHeaders(new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("Content-Type", "multipart");
                put("header2", "header value 2");
            }
        });
		assertTrue(actual.isMultipart());
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.data.RequestData#equals(java.lang.Object)}.
	 */
	@Test
	void testEqualsObject() {
		RequestData actual = new RequestData();
		RequestData other = new RequestData(actual);
		assertEquals(other.hashCode(),actual.hashCode());
		assertEquals(other,actual);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.data.RequestData#toString()}.
	 */
	@Test
	void testToString() {
		RequestData actual = new RequestData();
		RequestData other = new RequestData(actual);
		assertEquals(other.toString(),actual.toString());
	}

}
