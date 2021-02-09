package uk.gov.hmcts.befta.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.befta.exception.InvalidTestDataException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ElementIdFinderTest {
    private static final String CASE_ID =
            "${}${[scenarioContext][siblingContexts][F-105_Case_Data_Create _C1][testData][actualResponse][body][id]}";
    private static final String USER_ID = "${[scenarioContext][parentContext][testData][users][userOlawale][id]}";
    private static final String CASE_ID_KEY = "case_id";
    private static final String CASE_ROLE_KEY = "case_role";
    private static final String USER_ID_KEY = "user_id";

    Collection<Map<String, String>> caseUsers = new ArrayList<>();

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
    }

    @Test
    void testCaseRoleKeyIsPresentInAllMapsWithUniqueValues() {
        assertEquals(CASE_ROLE_KEY, ElementIdFinder.findElementIds(caseUsers));
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

        assertEquals(USER_ID_KEY, ElementIdFinder.findElementIds(caseUsers));
    }

    @Test
    void testUserIdAndCaseRoleKeyIsPresentInAllMapsWithUniqueValues() {
        caseUsers = new ArrayList<>();

        Map<String, String> caseRole1 = new HashMap<>();
        caseRole1.put(CASE_ID_KEY, CASE_ID);
        caseRole1.put(USER_ID_KEY, USER_ID + "1");
        caseRole1.put(CASE_ROLE_KEY, "[CR-1]");

        Map<String, String> caseRole2 = new HashMap<>();
        caseRole2.put(CASE_ID_KEY, CASE_ID);
        caseRole2.put(USER_ID_KEY, USER_ID + "2" );
        caseRole2.put(CASE_ROLE_KEY, "[CR-2]");

        Map<String, String> caseRole3 = new HashMap<>();
        caseRole3.put(CASE_ID_KEY, CASE_ID);
        caseRole3.put(USER_ID_KEY, USER_ID + "3");
        caseRole3.put(CASE_ROLE_KEY, "[CR-3]");

        Map<String, String> caseRole4 = new HashMap<>();
        caseRole4.put(CASE_ID_KEY, CASE_ID);
        caseRole4.put(USER_ID_KEY, USER_ID + "4");
        caseRole4.put(CASE_ROLE_KEY, "[CR-4]");

        caseUsers.add(metaData);
        caseUsers.add(caseRole1);
        caseUsers.add(caseRole2);
        caseUsers.add(caseRole3);
        caseUsers.add(caseRole4);

        String expectedValues = CASE_ROLE_KEY + "," + USER_ID_KEY;
        assertEquals(expectedValues, ElementIdFinder.findElementIds(caseUsers));
    }

    @Test
    void testCaseIdKeyCalculatedAtRuntimeIsIgnoredThrowsException() {
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

        assertThrows(InvalidTestDataException.class, () -> ElementIdFinder.findElementIds(caseUsers));
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

        assertEquals(CASE_ROLE_KEY, ElementIdFinder.findElementIds(caseUsers));
    }

    @Test
    void testThrowExceptionWhenWhenNoUniqueResultsAcrossMaps() {
        Map<String, String> caseRole = new HashMap<>();
        caseRole.put(CASE_ID_KEY,
                CASE_ID);
        caseRole.put("case_role", "[CR-2]");
        caseUsers.add(caseRole);

        assertThrows(InvalidTestDataException.class, () -> ElementIdFinder  .findElementIds(caseUsers));
    }
}