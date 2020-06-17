package uk.gov.hmcts.befta.util;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;

@RunWith(PowerMockRunner.class)
public class ReflectionUtilsTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void prepareTest() {
        PowerMockito.mockStatic(System.class);
        when(System.getenv("OAUTH2_CLIENT_ID")).thenReturn("ccd_gw");
    }

    @Test
    public void shouldDeepGetFieldInObject() throws Exception {
        HttpTestData testData = new HttpTestData();
        UserData user = new UserData("USERNAME", "PASSWORD");
        testData.setUserSet(user);

        final Object result = ReflectionUtils.deepGetFieldInObject(testData, "userSet.username");

        assertEquals("USERNAME", result);
    }

    @Test
    public void shouldErrorWithEmptyFieldPathForDeepGetFieldInObject() throws Exception {
        HttpTestData testData = new HttpTestData();

        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("Field path must be non-empty String.");

        ReflectionUtils.deepGetFieldInObject(testData, "");
    }

    @Test
    public void shouldRetrieveFieldInSimpleObject() throws Exception {
        HttpTestData testData = new HttpTestData();
        UserData user = new UserData();
        testData.setUserSet(user);

        final Object result = ReflectionUtils.retrieveFieldInObject(testData, "userSet");

        assertEquals(user, result);
    }

    @Test
    public void shouldRetrieveFieldInMapByKey() throws Exception {
        ResponseData responseData = new ResponseData();
        Map<String, ResponseData> testMap = new HashMap<String, ResponseData>() {
            private static final long serialVersionUID = 1L;
            {
                put("key", responseData);
            }
        };

        final Object result = ReflectionUtils.retrieveFieldInObject(testMap, "key");

        assertEquals(responseData, result);
    }

    @Test
    public void shouldRetrieveFieldFromMultiValueListAsList() throws Exception {
        List<UserData> testList = new ArrayList<UserData>() {
            private static final long serialVersionUID = 1L;
            {
                add(new UserData("USERNAME1", "PASSWORD1"));
                add(new UserData("USERNAME2", "PASSWORD2"));
            }
        };

        Map<String, List<UserData>> map = new HashMap<String, List<UserData>>();
        map.put("userList", testList);

        Object result = ReflectionUtils.retrieveFieldInObject(map, "userList[0]");
        assertEquals(testList.get(0), result);

        result = ReflectionUtils.retrieveFieldInObject(map, "userList[1]");
        assertEquals(testList.get(1), result);
    }

    @Test
    public void shouldRetrieveFieldFromAnObject() throws Exception {
        HttpTestData testData = new HttpTestData();
        UserData user = new UserData();
        testData.setUserSet(user);

        assertEquals(user, ReflectionUtils.retrieveFieldInObject(testData, "userSet"));
    }

    @Test
    public void shouldErrorWhenTryingToRetrieveNonExistingField() throws Exception {
        HttpTestData testData = new HttpTestData();
        testData.setS2sClientId("ccd_gw");

        exceptionRule.expect(NoSuchFieldException.class);
        exceptionRule.expectMessage(startsWith("nonExistingField not retrievable"));

        ReflectionUtils.retrieveFieldInObject(testData, "nonExistingField");
    }

    @Test
    public void shouldReturnNullWhenTryingToRetrieveNonExistingKeyInMap() throws Exception {
        Map<String, Object> testMap = new HashMap<>();

        final Object result = ReflectionUtils.retrieveFieldInObject(testMap, "NON EXISTING KEY");

        assertNull(result);
    }

    @Test
    public void shouldErrorWhenTryingToRetrieveNonExistingFieldInList() throws Exception {
        List<UserData> testList = new ArrayList<UserData>() {
            private static final long serialVersionUID = 1L;
            {
                add(new UserData("USERNAME1", "PASSWORD1"));
            }
        };

        exceptionRule.expect(NoSuchFieldException.class);
        exceptionRule.expectMessage(startsWith("nonExistingField not retrievable"));

        ReflectionUtils.retrieveFieldInObject(testList, "nonExistingField");
    }

    @Test
    public void shouldReturnTrueForMapContainsWhenTwoMapsAreEqual() {
        Map<String, String> big = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", "val1");
            }
        };
        Map<String, String> small = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", "val1");
            }
        };

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertTrue(result);
    }

    @Test
    public void shouldReturnTrueForMapContainsWhenBigMapExtendsSmallMap() {
        Map<String, String> big = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", "val1");
                put("key2", "val2");
            }
        };
        Map<String, String> small = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", "val1");
            }
        };

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseForMapContainsWhenSmallMapHasDifferentKey() {
        Map<String, String> big = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", "val1");
            }
        };
        Map<String, String> small = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put("OTHER KEY", "val1");
            }
        };

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertFalse(result);
    }

    @Test
    public void shouldReturnFalseForMapContainsWhenSmallMapHasDifferentValue() {
        Map<String, String> big = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", "val1");
            }
        };
        Map<String, String> small = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", "OTHER VAL");
            }
        };

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertFalse(result);
    }

    @Test
    public void shouldReturnFalseForMapContainsWhenSmallMapSizeIsGreaterThanBigMap() {
        Map<String, String> big = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", "val1");
            }
        };
        Map<String, String> small = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", "val1");
                put("key2", "val2");
            }
        };

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertFalse(result);
    }

    @Test
    public void shouldReturnTrueForMapContainsWithSameEmbeddedMap() {
        @SuppressWarnings("unchecked")
        Map<Object, Object> map = mock(Map.class);
        Map<String, Map<Object, Object>> small = new HashMap<String, Map<Object, Object>>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", map);
            }
        };
        Map<String, Map<Object, Object>> big = new HashMap<String, Map<Object, Object>>() {
            private static final long serialVersionUID = 1L;
            {
                put("key1", map);
            }
        };

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertTrue(result);
    }
}
