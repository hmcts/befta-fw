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
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.CcdRoleConfig#CcdRoleConfig(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testCcdRoleConfigEqual() {
		String role="caseworker-autotest2";
		String securityClassification="PUBLIC1";
		CcdRoleConfig ccdRoleConfig = new CcdRoleConfig("caseworker-autotest1", "PUBLIC");
		CcdRoleConfig ccdRoleConfigOther = new CcdRoleConfig("caseworker-autotest1", "PUBLIC");
		ccdRoleConfigOther.setRole(role);
		ccdRoleConfigOther.setSecurityClassification(securityClassification);
		CcdRoleConfig ccdRoleConfigSame = new CcdRoleConfig("caseworker-autotest1", "PUBLIC");
		assertNotEquals(ccdRoleConfigSame,null);
		assertNotEquals(ccdRoleConfigSame,ccdRoleConfigOther);
		assertEquals(ccdRoleConfigSame,ccdRoleConfig);
		assertEquals(ccdRoleConfigSame.hashCode(),ccdRoleConfig.hashCode());
		assertNotNull(ccdRoleConfigOther.toString());
	}
}
