package uk.gov.hmcts.befta.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class HttpTestDataTest {

    private HttpTestData testData;

    private static final String OPERATION = "OPERATION";
    private static final String PRODUCT = "PRODUCT";

    @Before
    public void setUp() {
        testData = new HttpTestData();
    }

    @Test
    public void shouldReturnTrueWhenSpecIsMet() {
        final String testSpec = "SPEC 2";
        final List<String> specs = Arrays.asList("SPEC 1", testSpec);
        testData.setSpecs(specs);

        boolean result = testData.meetsSpec(testSpec);

        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenSpecIsNotMet() {
        final List<String> specs = Arrays.asList("SPEC 1", "SPEC 2");
        testData.setSpecs(specs);

        boolean result = testData.meetsSpec("SPEC 3");

        assertFalse(result);
    }

    @Test
    public void shouldReturnTrueWhenOperationOfProductIsMet() {
        testData.setOperationName(OPERATION);
        testData.setProductName(PRODUCT);

        boolean result = testData.meetsOperationOfProduct(OPERATION, PRODUCT);

        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenOperationIsNotEqual() {
        testData.setOperationName(OPERATION);
        testData.setProductName(PRODUCT);

        boolean result = testData.meetsOperationOfProduct("OTHER OPERATION", PRODUCT);

        assertFalse(result);
    }

    @Test
    public void shouldReturnFalseWhenProductNameIsNotEqual() {
        testData.setOperationName(OPERATION);
        testData.setProductName(PRODUCT);

        boolean result = testData.meetsOperationOfProduct(OPERATION, "OTHER PRODUCT");

        assertFalse(result);
    }

    @Test
    public void shouldSetInvokingUserToUserSetWhenNotNull() {
        Map<String, UserData> users = new HashMap<>();
        final UserData user = new UserData("USERNAME", "PASSWORD");
        users.put("someUser", user);
        testData.setUserSet(user);

        testData.setUsers(users);

        assertEquals(user, testData.getInvokingUser());
    }
}
