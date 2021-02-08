package uk.gov.hmcts.befta.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.befta.exception.InvalidTestDataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ElementIdFinderTest {
    private static final String CASE_ID =
            "${}${[scenarioContext][siblingContexts][F-105_Case_Data_Create _C1][testData][actualResponse][body][id]}";
    private static final String USER_ID = "${[scenarioContext][parentContext][testData][users][userOlawale][id]}";
    private static final String ELEMENT_IDS = "case_id,user_id,case_role";
    private static final String CASE_ID_KEY = "case_id";
    private static final String CASE_ROLE_KEY = "case_role";
    private static final String USER_ID_KEY = "user_id";

    Collection<Map<String, String>> caseUsers = new ArrayList<>();

    ElementIdFinder elementIdFinder;

    Map<String, String> metaData = new HashMap<>();

    @BeforeEach
    public void setup() {
        metaData = new HashMap<>();
        metaData.put("__ordering__","UNORDERED");
        metaData.put("__elementId__","case_id,user_id,case_role");

        Map<String, String> caseRole1 = new HashMap<>();
        caseRole1.put(CASE_ID_KEY,
                CASE_ID);
        caseRole1.put(USER_ID_KEY,
                USER_ID);
        caseRole1.put(CASE_ROLE_KEY, "[CR-1]");

        Map<String, String> caseRole2 = new HashMap<>();
        caseRole2.put(CASE_ID_KEY,
                CASE_ID);
        caseRole2.put(USER_ID_KEY,
                USER_ID);
        caseRole2.put(CASE_ROLE_KEY, "[CR-2]");

        caseUsers.add(metaData);
        caseUsers.add(caseRole1);
        caseUsers.add(caseRole2);

        elementIdFinder = new ElementIdFinder();
    }

    @Test
    void testValueIsCalculatedAtRuntime() {
        assertTrue(elementIdFinder.isCalculatedAtRuntime(CASE_ID));
    }

    @Test
    void testValueIsCalculatedAtRuntimeWithoutStringCast() {
        assertTrue(elementIdFinder.isCalculatedAtRuntime("${[scenarioContext][siblingContexts][F-105_Case_Data_Create _C1][testData][actualResponse][body][id]}"));
    }

    @Test
    void testValueIsNotCalculatedAtRuntime() {
        assertFalse(elementIdFinder.isCalculatedAtRuntime(USER_ID));
    }

    @Test
    void testCaseRoleKeyIsPresentInAllMapsWithUniqueValues() {
        assertEquals(new HashSet<>(Arrays.asList(CASE_ROLE_KEY)), elementIdFinder.findCommonMapEntries(caseUsers));
    }

    @Test
    void testUserIdKeyIsPresentInAllMapsWithUniqueValues() {
        caseUsers = new ArrayList<>();

        Map<String, String> caseRole1 = new HashMap<>();
        caseRole1.put(CASE_ID_KEY, CASE_ID);
        caseRole1.put(USER_ID_KEY, USER_ID + "1");
        caseRole1.put(CASE_ROLE_KEY, "[CR-1]");

        Map<String, String> caseRole2 = new HashMap<>();
        caseRole2.put(CASE_ID_KEY, CASE_ID);
        caseRole2.put(USER_ID_KEY, USER_ID + "2" );
        caseRole2.put(CASE_ROLE_KEY, "[CR-1]");

        Map<String, String> caseRole3 = new HashMap<>();
        caseRole3.put(CASE_ID_KEY, CASE_ID);
        caseRole3.put(USER_ID_KEY, USER_ID + "3");
        caseRole3.put(CASE_ROLE_KEY, "[CR-1]");

        Map<String, String> caseRole4 = new HashMap<>();
        caseRole4.put(CASE_ID_KEY, CASE_ID);
        caseRole4.put(USER_ID_KEY, USER_ID + "4");
        caseRole4.put(CASE_ROLE_KEY, "[CR-1]");

        caseUsers.add(metaData);
        caseUsers.add(caseRole1);
        caseUsers.add(caseRole2);
        caseUsers.add(caseRole3);
        caseUsers.add(caseRole4);

        assertEquals(new HashSet<>(Arrays.asList(USER_ID_KEY)), elementIdFinder.findCommonMapEntries(caseUsers));
    }

    @Test
    void testCaseIdKeyCalculatedAtRuntimeIsIgnoreThrowsException() {
        caseUsers = new ArrayList<>();

        Map<String, String> caseRole1 = new HashMap<>();
        caseRole1.put(CASE_ID_KEY, "${}${[scenarioContext][siblingContexts][F-105_Case_Data_Create _C1][testData][actualResponse][body][id]}");

        Map<String, String> caseRole2 = new HashMap<>();
        caseRole2.put(CASE_ID_KEY, "${}${[scenarioContext][siblingContexts][F-105_Case_Data_Create _C2][testData][actualResponse][body][id]}");

        Map<String, String> caseRole3 = new HashMap<>();
        caseRole3.put(CASE_ID_KEY, "${}${[scenarioContext][siblingContexts][F-105_Case_Data_Create _C3][testData][actualResponse][body][id]}");

        Map<String, String> caseRole4 = new HashMap<>();
        caseRole4.put(CASE_ID_KEY, "${}${[scenarioContext][siblingContexts][F-105_Case_Data_Create _C4][testData][actualResponse][body][id]}");

        caseUsers.add(metaData);
        caseUsers.add(caseRole1);
        caseUsers.add(caseRole2);
        caseUsers.add(caseRole3);
        caseUsers.add(caseRole4);

        assertThrows(InvalidTestDataException.class, () -> elementIdFinder.findCommonMapEntries(caseUsers));
    }

    @Test
    void testCaseIdKeyCalculatedAtRuntimeIsIgnoredReturnsCaseRole() {
        caseUsers = new ArrayList<>();

        Map<String, String> caseRole1 = new HashMap<>();
        caseRole1.put(CASE_ID_KEY, "${}${[scenarioContext][siblingContexts][F-105_Case_Data_Create _C1][testData][actualResponse][body][id]}");
        caseRole1.put(CASE_ROLE_KEY, ",[CR-1]");

        Map<String, String> caseRole2 = new HashMap<>();
        caseRole2.put(CASE_ID_KEY, "${}${[scenarioContext][siblingContexts][F-105_Case_Data_Create _C2][testData][actualResponse][body][id]}");
        caseRole2.put(CASE_ROLE_KEY, ",[CR-2]");

        Map<String, String> caseRole3 = new HashMap<>();
        caseRole3.put(CASE_ID_KEY, "${}${[scenarioContext][siblingContexts][F-105_Case_Data_Create _C3][testData][actualResponse][body][id]}");
        caseRole3.put(CASE_ROLE_KEY, ",[CR-3]");

        Map<String, String> caseRole4 = new HashMap<>();
        caseRole4.put(CASE_ID_KEY, "${}${[scenarioContext][siblingContexts][F-105_Case_Data_Create _C4][testData][actualResponse][body][id]}");
        caseRole4.put(CASE_ROLE_KEY, ",[CR-4]");

        caseUsers.add(metaData);
        caseUsers.add(caseRole1);
        caseUsers.add(caseRole2);
        caseUsers.add(caseRole3);
        caseUsers.add(caseRole4);

        assertEquals(Collections.singleton(CASE_ROLE_KEY), elementIdFinder.findCommonMapEntries(caseUsers));
    }

    @Test
    void testReturnEmptyResultsWhenNoUniqueResultsAcrossMaps() {
        Map<String, String> caseRole = new HashMap<>();
        caseRole.put(CASE_ID_KEY,
                CASE_ID);
        caseRole.put("case_role", "[CR-2]");
        caseUsers.add(caseRole);

        assertThrows(InvalidTestDataException.class, () -> elementIdFinder.findCommonMapEntries(caseUsers));
    }
}