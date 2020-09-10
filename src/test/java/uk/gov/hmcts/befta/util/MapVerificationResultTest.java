/**
 * 
 */
package uk.gov.hmcts.befta.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author korneleehenry
 *
 */
class MapVerificationResultTest {

	/**
	 * Test method for {@link uk.gov.hmcts.befta.util.MapVerificationResult#minimalUnverifiedResult(java.lang.String, int, int)}.
	 */
	@Test
	void testMinimalUnverifiedResult() {
		String field = "field";
		int currentDepth = 1;
		int maxMessageDepth = 2;
		MapVerificationResult actual = MapVerificationResult.minimalUnverifiedResult(field, currentDepth, maxMessageDepth);
		assertNotNull(actual);
		assertFalse(actual.isVerified());
	}

	/**
	 * Test method for {@link java.lang.Object#equals(java.lang.Object)}.
	 */
	@Test
	void testEqualsObject() {
		String field = "field";
		int currentDepth = 1;
		int maxMessageDepth = 2;
		MapVerificationResult actual = new MapVerificationResult(field, false, "Summary",currentDepth, maxMessageDepth);
		MapVerificationResult expected = new MapVerificationResult(field, false, "Summary",currentDepth, maxMessageDepth);
		assertEquals(expected,actual);
		assertEquals(expected.hashCode(), actual.hashCode());
	}

	/**
	 * Test method for {@link java.lang.Object#toString()}.
	 */
	@Test
	void testToString() {
		String field = "field";
		int currentDepth = 1;
		int maxMessageDepth = 2;
		String summary = "summary";
		MapVerificationResult actual = new MapVerificationResult(field, false, "Summary",currentDepth, maxMessageDepth);
		MapVerificationResult expected = new MapVerificationResult(field, false, "",currentDepth, maxMessageDepth);
		expected.setCurrentDepth(currentDepth);
		expected.setVerified(true);
		expected.setMaxMessageDepth(maxMessageDepth);
		expected.setSummary(summary);
		expected.setField(field);
		assertNotEquals(expected,actual);
		assertNotNull(actual.toString());
		
	}

}
