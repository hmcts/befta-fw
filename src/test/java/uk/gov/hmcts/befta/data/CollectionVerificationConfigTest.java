/**
 * 
 */
package uk.gov.hmcts.befta.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import uk.gov.hmcts.befta.data.CollectionVerificationConfig.Operator;
import uk.gov.hmcts.befta.data.CollectionVerificationConfig.Ordering;

/**
 * @author korneleehenry
 *
 */
class CollectionVerificationConfigTest {

	/**
	 * Test method for {@link uk.gov.hmcts.befta.data.CollectionVerificationConfig#equals(java.lang.Object)}.
	 */
	@Test
	void testEqualsObject() {
		CollectionVerificationConfig actual = new CollectionVerificationConfig();
		CollectionVerificationConfig other = new CollectionVerificationConfig(Operator.EQUIVALENT, Ordering.ORDERED, "Id2");
		assertEquals(actual.DEFAULT,actual);
		assertEquals(actual.DEFAULT.hashCode(),actual.hashCode());
		assertNotEquals(actual.hashCode(),other.hashCode());
		assertNotEquals(actual,other);
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.data.CollectionVerificationConfig#toString()}.
	 */
	@Test
	void testToString() {
		CollectionVerificationConfig actual = new CollectionVerificationConfig();
		CollectionVerificationConfig other = new CollectionVerificationConfig(Operator.EQUIVALENT, Ordering.ORDERED, "Id2");
		assertNotNull(actual.toString());
	}

}
