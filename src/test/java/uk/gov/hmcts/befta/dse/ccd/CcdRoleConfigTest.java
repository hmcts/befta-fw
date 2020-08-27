/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author korneleehenry
 *
 */
class CcdRoleConfigTest {

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.CcdRoleConfig#CcdRoleConfig(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testCcdRoleConfig() {
		String role="caseworker-autotest1";
		String securityClassification="PUBLIC";
		CcdRoleConfig ccdRoleConfig = new CcdRoleConfig("caseworker-autotest1", "PUBLIC");
		assertEquals(role,ccdRoleConfig.getRole());
		assertEquals(securityClassification,ccdRoleConfig.getSecurityClassification());
	}

}
