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
class UserDataTest {

	public static final String ID = "uniqueid";

	public static final String USER_NAME = "geek";

	public static final String PASSWORD = "secret";

	public static final String ACCESS_TOKEN = "complicatedtext";

    /**
	 * Test method for {@link uk.gov.hmcts.befta.data.UserData#UserData()}.
	 */
	@Test
	void testUserData() {
		UserData actual = new UserData();
		assertNotNull(actual);
		assertNull(actual.getUsername());
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.data.UserData#UserData(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testUserDataStringString() {
		UserData actual = new UserData(USER_NAME,PASSWORD);
		assertNotNull(actual);
		assertEquals(USER_NAME, actual.getUsername());
		assertEquals(PASSWORD, actual.getPassword());
	}

	/**
	 * Test method for {@link uk.gov.hmcts.befta.data.UserData#UserData(uk.gov.hmcts.befta.data.UserData)}.
	 */
	@Test
	void testUserDataUserData() {
		UserData otherData = new UserData();
		otherData.setId(ID);
		otherData.setAccessToken(ACCESS_TOKEN);
		otherData.setUsername(USER_NAME);
		otherData.setPassword(PASSWORD);
		UserData actual = new UserData(otherData);
		assertNotNull(actual);
		assertEquals(USER_NAME, actual.getUsername());
		assertEquals(PASSWORD, actual.getPassword());
		assertEquals(ID, actual.getId());
		assertEquals(ACCESS_TOKEN, actual.getAccessToken());
	}
	@Test
	void testUserDataUserDataEquals() {
		UserData otherData = new UserData();
		otherData.setId(ID);
		otherData.setAccessToken(ACCESS_TOKEN);
		otherData.setUsername(USER_NAME);
		otherData.setPassword(PASSWORD);
		UserData actual = new UserData(otherData);
		assertNotNull(actual.toString());
		assertEquals(otherData.hashCode(), actual.hashCode());
		assertEquals(otherData, actual);
	}

}
