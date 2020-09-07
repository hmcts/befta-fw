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
class ResponseDataTest {

	/**
	 * Test method for {@link uk.gov.hmcts.befta.data.ResponseData#hashCode()}.
	 */
	@Test
	void testHashCode() {
		ResponseData actual = new ResponseData();
		actual.setResponseMessage("responseMessage");
		actual.setResponseCode(200);
		ResponseData other = new ResponseData(actual);
		assertEquals(other.hashCode(),actual.hashCode());
		
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.data.ResponseData#equals(java.lang.Object)}.
	 */
	@Test
	void testEqualsObject() {
		ResponseData actual = new ResponseData();
		actual.setResponseMessage("responseMessage");
		actual.setResponseCode(200);
		ResponseData other = new ResponseData(actual);
		assertEquals(other,actual);
		assertEquals(other.toString(),actual.toString());
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.data.ResponseData#toString()}.
	 */
	@Test
	void testToString() {
		ResponseData actual = new ResponseData();
		ResponseData other = new ResponseData(actual);
		actual.setResponseMessage("responseMessage1");
		actual.setResponseCode(400);
		assertNotEquals(other,actual);
		assertNotEquals(other.toString(),actual.toString());
		assertNotEquals(other,actual);
		assertNotEquals(other.hashCode(),actual.hashCode());
	}

}
