/**
 * 
 */
package uk.gov.hmcts.befta.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
