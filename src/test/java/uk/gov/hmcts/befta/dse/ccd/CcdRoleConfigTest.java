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
	private static final String PUBLIC = "PUBLIC";
	private static final String CASEWORKER_ROLE = "caseworker-autotest1";;
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.CcdRoleConfig#CcdRoleConfig(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testCcdRoleConfig() {
		CcdRoleConfig ccdRoleConfig = new CcdRoleConfig(CASEWORKER_ROLE, PUBLIC);
		assertEquals(CASEWORKER_ROLE,ccdRoleConfig.getRole());
		assertEquals(PUBLIC,ccdRoleConfig.getSecurityClassification());
	}
	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.CcdRoleConfig#CcdRoleConfig(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testCcdRoleConfigEqual() {
		String role="caseworker-autotest2";
		String securityClassification="PUBLIC1";
		CcdRoleConfig ccdRoleConfig = new CcdRoleConfig(CASEWORKER_ROLE, PUBLIC);
		CcdRoleConfig ccdRoleConfigOther = new CcdRoleConfig(CASEWORKER_ROLE, PUBLIC);
		ccdRoleConfigOther.setRole(role);
		ccdRoleConfigOther.setSecurityClassification(securityClassification);
		CcdRoleConfig ccdRoleConfigSame = new CcdRoleConfig(CASEWORKER_ROLE, PUBLIC);
		assertNotEquals(ccdRoleConfigSame,null);
		assertNotEquals(ccdRoleConfigSame,ccdRoleConfigOther);
		assertEquals(ccdRoleConfigSame,ccdRoleConfig);
		assertEquals(ccdRoleConfigSame.hashCode(),ccdRoleConfig.hashCode());
		assertNotNull(ccdRoleConfigOther.toString());
	}
}
