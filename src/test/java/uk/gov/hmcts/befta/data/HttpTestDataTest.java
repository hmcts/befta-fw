package uk.gov.hmcts.befta.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

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

        boolean result = testData.meetsOperationOfProduct(PRODUCT, OPERATION);

        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenOperationIsNotEqual() {
        testData.setOperationName(OPERATION);
        testData.setProductName(PRODUCT);

        boolean result = testData.meetsOperationOfProduct(PRODUCT, "OTHER OPERATION");

        assertFalse(result);
    }

    @Test
    public void shouldReturnFalseWhenProductNameIsNotEqual() {
        testData.setOperationName(OPERATION);
        testData.setProductName(PRODUCT);

        boolean result = testData.meetsOperationOfProduct("OTHER PRODUCT", OPERATION);

        assertFalse(result);
    }

    @Test
    public void shouldSetInvokingUserToUserSetWhenNotNull() {
        LinkedHashMap<String, UserData> users = new LinkedHashMap<>();
        final UserData user = new UserData("USERNAME", "PASSWORD");
        users.put("someUser", user);
        testData.setInvokingUser(user);

        testData.setUsers(users);

        assertEquals(user, testData.getInvokingUser());
    }
}

