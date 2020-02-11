package uk.gov.hmcts.befta.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.hmcts.befta.data.HttpTestData;
import uk.gov.hmcts.befta.data.ResponseData;
import uk.gov.hmcts.befta.data.UserData;

import java.util.*;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


@RunWith(PowerMockRunner.class)
public class ReflectionUtilsTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

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
        Map<String, ResponseData> testMap = new HashMap<String, ResponseData>() {{
            put("key", responseData);
        }};

        final Object result = ReflectionUtils.retrieveFieldInObject(testMap, "key");

        assertEquals(responseData, result);
    }

    @Test
    public void shouldRetrieveFieldFromMultiValueListAsList() throws Exception {
        List<UserData> testList = new ArrayList<UserData>() {{
            add(new UserData("USERNAME1", "PASSWORD1"));
            add(new UserData("USERNAME2", "PASSWORD2"));
        }};

        final Object result = ReflectionUtils.retrieveFieldInObject(testList, "username");

        final List<String> resultAsList = (ArrayList) result;
        assertEquals(2, resultAsList.size());
        assertEquals("USERNAME1", resultAsList.get(0));
        assertEquals("USERNAME2", resultAsList.get(1));
    }

    @Test
    public void shouldRetrieveFieldFromSingleValueListAsSingleObject() throws Exception {
        HttpTestData testData = new HttpTestData();
        UserData user = new UserData();
        testData.setUserSet(user);
        List<HttpTestData> testList = Collections.singletonList(testData);

        final Object result = ReflectionUtils.retrieveFieldInObject(testList, "userSet");

        assertEquals(user, result);
    }

    @Test
    public void shouldRetrieveFieldFromSetAsSet() throws Exception {
        Set<UserData> testSet = new HashSet<UserData>() {{
            add(new UserData("USERNAME1", "PASSWORD1"));
            add(new UserData("USERNAME2", "PASSWORD2"));
        }};

        final Object result = ReflectionUtils.retrieveFieldInObject(testSet, "username");

        final Set<String> resultAsSet = (LinkedHashSet) result;
        assertEquals(2, resultAsSet.size());
        assertThat(resultAsSet, hasItem("USERNAME1"));
        assertThat(resultAsSet, hasItem("USERNAME2"));
    }

    @Test
    public void shouldRetrieveFieldFromSingleValueSetAsSingleObject() throws Exception {
        HttpTestData testData = new HttpTestData();
        UserData user = new UserData();
        testData.setUserSet(user);
        Set<HttpTestData> testList = new HashSet<HttpTestData>() {{
            add(testData);
        }};

        final Object result = ReflectionUtils.retrieveFieldInObject(testList, "userSet");

        assertEquals(user, result);
    }

    @Test
    public void shouldReturnDummyPlaceHolderWhenTryingToRetrieveFieldWithNullValueFromList() throws Exception {
        HttpTestData testData = new HttpTestData();
        List<HttpTestData> testList = Collections.singletonList(testData);

        final Object result = ReflectionUtils.retrieveFieldInObject(testList, "userSet");

        assertEquals(ReflectionUtils.DummyPlaceHolder.class, result.getClass());
    }

    @Test
    public void shouldErrorWhenTryingToRetrieveNonExistingField() throws Exception {
        HttpTestData testData = new HttpTestData();

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
        List<UserData> testList = new ArrayList<UserData>() {{
            add(new UserData("USERNAME1", "PASSWORD1"));
        }};

        exceptionRule.expect(NoSuchFieldException.class);
        exceptionRule.expectMessage(startsWith("nonExistingField not retrievable"));

        ReflectionUtils.retrieveFieldInObject(testList, "nonExistingField");
    }

    @Test
    public void shouldReturnTrueForMapContainsWhenTwoMapsAreEqual() {
        Map<String, String> big = new HashMap<String, String>() {{
            put("key1", "val1");
        }};
        Map<String, String> small = new HashMap<String, String>() {{
            put("key1", "val1");
        }};

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertTrue(result);
    }

    @Test
    public void shouldReturnTrueForMapContainsWhenBigMapExtendsSmallMap() {
        Map<String, String> big = new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
        }};
        Map<String, String> small = new HashMap<String, String>() {{
            put("key1", "val1");
        }};

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseForMapContainsWhenSmallMapHasDifferentKey() {
        Map<String, String> big = new HashMap<String, String>() {{
            put("key1", "val1");
        }};
        Map<String, String> small = new HashMap<String, String>() {{
            put("OTHER KEY", "val1");
        }};

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertFalse(result);
    }

    @Test
    public void shouldReturnFalseForMapContainsWhenSmallMapHasDifferentValue() {
        Map<String, String> big = new HashMap<String, String>() {{
            put("key1", "val1");
        }};
        Map<String, String> small = new HashMap<String, String>() {{
            put("key1", "OTHER VAL");
        }};

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertFalse(result);
    }

    @Test
    public void shouldReturnFalseForMapContainsWhenSmallMapSizeIsGreaterThanBigMap() {
        Map<String, String> big = new HashMap<String, String>() {{
            put("key1", "val1");
        }};
        Map<String, String> small = new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
        }};

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertFalse(result);
    }

    @Test
    public void shouldReturnTrueForMapContainsWithSameEmbeddedMap() {
        Map<Object, Object> map = mock(Map.class);
        Map<String, Map<Object, Object>> small = new HashMap<String, Map<Object, Object>>() {{
            put("key1", map);
        }};
        Map<String, Map<Object, Object>> big = new HashMap<String, Map<Object, Object>>() {{
            put("key1", map);
        }};

        final boolean result = ReflectionUtils.mapContains(big, small);

        assertTrue(result);
    }
}
