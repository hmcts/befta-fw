package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearEnvironmentVariable;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import uk.gov.hmcts.befta.exception.InvalidTestDataException;

import java.io.IOException;
import java.net.MalformedURLException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvironmentURLUtilsTest {

    private static final String CASE_EVENT =  "CaseEvent";
    private static final String LOCALHOST_URL = "http://localhost:8080";

    private static final String MCA_API_BASE_URL = "MCA_API_BASE_URL";
    private static final String TEST_STUB_SERVICE_BASE_URL = "TEST_STUB_SERVICE_BASE_URL";

    ObjectMapper objectMapper = new ObjectMapper();

    JsonNode caseEventJson;

    private String CASE_EVENT_TO_FIELDS_TEMPLATE = "[ {\n" +
            "  \"CaseTypeID\" : \"FT_MultiplePages\",\n" +
            "  \"CaseEventID\" : \"createCase\",\n" +
            "  \"CaseFieldID\" : \"TextFieldFName\",\n" +
            "  \"PageID\" : \"MuliFormPage1\",\n" +
            "  \"PageLabel\" : \"Case Multiple Pages: Page 1/3\",\n" +
            "  \"CallBackURLMidEvent\" : \"%s%s\"\n" +
            "}, {\n" +
            " " +
            "  \"CaseTypeID\" : \"FT_MultiplePages\",\n" +
            "  \"CaseEventID\" : \"createCase\",\n" +
            "  \"CaseFieldID\" : \"TextFieldMName\",\n" +
            "  \"PageID\" : \"MuliFormPage2\",\n" +
            "  \"PageLabel\" : \"Case Multiple Pages: Page 2/3\",\n" +
            "  \"CallBackURLMidEvent\" : \"\"\n" +
            "} ]\n";

    private String CASE_EVENT_CONTENT_TEMPLATE = "[ {\n" +
            "  \"CaseTypeID\" : \"FT_NoCAutoApprovalCaseType\",\n" +
            "  \"ID\" : \"createCase\",\n" +
            "  \"Name\" : \"Create Case\",\n" +
            "  \"Description\" : \"\",\n" +
            "  \"CallBackURLAboutToStartEvent\" : \"\",\n" +
            "  \"RetriesTimeoutAboutToStartEvent\" : \"\",\n" +
            "  \"CallBackURLAboutToSubmitEvent\" : \"\",\n" +
            "  \"RetriesTimeoutURLAboutToSubmitEvent\" : \"\",\n" +
            "  \"CallBackURLSubmittedEvent\" : \"\",\n" +
            "  \"RetriesTimeoutURLSubmittedEvent\" : \"\",\n" +
            "  \"SecurityClassification\" : \"Public\"\n" +
            "}, {\n" +
            "  \"CaseTypeID\" : \"FT_NoCAutoApprovalCaseType\",\n" +
            "  \"ID\" : \"nocRequest_autoApproval\",\n" +
            "  \"Name\" : \"NoC Request\",\n" +
            "  \"Description\" : \"Notice of Change Request\",\n" +
            "  \"CallBackURLAboutToStartEvent\" : \"\",\n" +
            "  \"RetriesTimeoutAboutToStartEvent\" : \"\",\n" +
            "  \"CallBackURLAboutToSubmitEvent\" : \"\",\n" +
            "  \"RetriesTimeoutURLAboutToSubmitEvent\" : \"\",\n" +
            "  \"CallBackURLSubmittedEvent\" : \"%s%s\",\n" +
            "  \"RetriesTimeoutURLSubmittedEvent\" : \"\",\n" +
            "  \"SecurityClassification\" : \"Public\"\n" +
            "}, {\n" +
            "  \"CaseTypeID\" : \"FT_NoCAutoApprovalCaseType\",\n" +
            "  \"ID\" : \"ApplyNoCDecision\",\n" +
            "  \"Name\" : \"Apply NoC Decision\",\n" +
            "  \"Description\" : \"Apply Notice of Change Request\",\n" +
            "  \"CallBackURLAboutToStartEvent\" : \"%s%s\",\n" +
            "  \"RetriesTimeoutAboutToStartEvent\" : \"\",\n" +
            "  \"CallBackURLAboutToSubmitEvent\" : \"\",\n" +
            "  \"RetriesTimeoutURLAboutToSubmitEvent\" : \"\",\n" +
            "  \"CallBackURLSubmittedEvent\" : \"\",\n" +
            "  \"RetriesTimeoutURLSubmittedEvent\" : \"\",\n" +
            "  \"SecurityClassification\" : \"Public\"\n" +
            "} ]";

    private static final String NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_DEFAULT_HOST = "http://localhost:4454";
    private static final String NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_HOST =
            String.format("${MCA_API_BASE_URL:%s}", NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_DEFAULT_HOST);
    private static final String NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_PATH = "/noc/check-noc-approval";

    private static final String APPLY_NOC_DECISION_SUBMIT_EVENT_DEFAULT_HOST = "http://localhost:5555";
    private static final String APPLY_NOC_DECISION_SUBMIT_EVENT_HOST =
            String.format("${TEST_STUB_SERVICE_BASE_URL:%s}", APPLY_NOC_DECISION_SUBMIT_EVENT_DEFAULT_HOST);
    private static final String APPLY_NOC_DECISION_SUBMIT_EVENT_PATH =
            "/callback_returning_simulated_completed_noc_request";

    private static final String CREATE_CASE_CALLBACK_MID_EVENT_DEFAULT_HOST
            = "http://ccd-test-stubs-service:5555";
    private static final String CREATE_CASE_CALLBACK_MID_EVENT_HOST
            = String.format("${TEST_STUB_SERVICE_BASE_URL:%s}", CREATE_CASE_CALLBACK_MID_EVENT_DEFAULT_HOST);
    private static final String CREATE_CASE_CALLBACK_MID_EVENT_PATH
            = "/case_type/fe-functional-test/mid_event_dynamic_list";

    @BeforeEach
    void setup() throws IOException {
        caseEventJson = objectMapper.readTree(
                String.format(CASE_EVENT_CONTENT_TEMPLATE,
                        NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_HOST, NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_PATH,
                        APPLY_NOC_DECISION_SUBMIT_EVENT_HOST, APPLY_NOC_DECISION_SUBMIT_EVENT_PATH)
        );
    }

    @Test
    void unsupportedSheetNamesShouldNotModifyURLs() throws Exception{
        JsonNode nullJsonNode = objectMapper.nullNode();
        JsonNode modifiedJsonNode = EnvironmentURLUtils.updateCallBackURLs(nullJsonNode, "AFileNameThatIsNotCaseEvent");

        // check JSON has not been modified
        assertEquals(nullJsonNode, modifiedJsonNode);
    }

    @Test
    @SetEnvironmentVariable(key = TEST_STUB_SERVICE_BASE_URL, value = LOCALHOST_URL)
    void caseEventToFieldsSheetModifyTestStubURL() throws Exception {
        JsonNode caseEventToFieldsNode = objectMapper.readTree(String.format(CASE_EVENT_TO_FIELDS_TEMPLATE,
                CREATE_CASE_CALLBACK_MID_EVENT_HOST,
                CREATE_CASE_CALLBACK_MID_EVENT_PATH));

        JsonNode modifiedJsonNode = EnvironmentURLUtils.updateCallBackURLs(caseEventToFieldsNode, "CaseEventToFields");

        // check JSON has been modified
        assertNotEquals(caseEventToFieldsNode, modifiedJsonNode);

        // check placeholders are not still present
        assertFalse(modifiedJsonNode.toString().contains(CREATE_CASE_CALLBACK_MID_EVENT_HOST));

        // check URLs have been substituted with correct values
        assertTrue(modifiedJsonNode.toString().contains(LOCALHOST_URL+ CREATE_CASE_CALLBACK_MID_EVENT_PATH));
    }

    @Test
    @ClearEnvironmentVariable(key = TEST_STUB_SERVICE_BASE_URL)
    @ClearEnvironmentVariable(key = MCA_API_BASE_URL)
    void caseEventToFieldsUpdatedWithDefaultValuesWhenNoEnvironmentVariablesSet() throws Exception {
        JsonNode caseEventToFieldsNode = objectMapper.readTree(String.format(CASE_EVENT_TO_FIELDS_TEMPLATE,
                CREATE_CASE_CALLBACK_MID_EVENT_HOST,
                CREATE_CASE_CALLBACK_MID_EVENT_PATH));

        JsonNode modifiedJsonNode = EnvironmentURLUtils.updateCallBackURLs(caseEventToFieldsNode, "CaseEventToFields");

        // check JSON has been modified
        assertNotEquals(caseEventToFieldsNode, modifiedJsonNode);

        // check placeholders are not still present
        assertFalse(modifiedJsonNode.toString().contains(CREATE_CASE_CALLBACK_MID_EVENT_HOST));

        // check URLs have been substituted with correct values
        assertTrue(modifiedJsonNode.toString()
                .contains(CREATE_CASE_CALLBACK_MID_EVENT_DEFAULT_HOST + CREATE_CASE_CALLBACK_MID_EVENT_PATH));
    }

    @Test
    @ClearEnvironmentVariable(key = TEST_STUB_SERVICE_BASE_URL)
    @ClearEnvironmentVariable(key = MCA_API_BASE_URL)
    void caseEventUrlsUpdatedWithDefaultValuesWheNoEnvironmentVariablesSet() throws Exception {
        JsonNode modifiedJsonNode = EnvironmentURLUtils.updateCallBackURLs(caseEventJson, CASE_EVENT);

        // check JSON has been modified
        assertNotEquals(caseEventJson, modifiedJsonNode);

        // check placeholders are not still present
        assertFalse(modifiedJsonNode.toString().contains(NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_HOST));
        assertFalse(modifiedJsonNode.toString().contains(APPLY_NOC_DECISION_SUBMIT_EVENT_HOST));

        // check URLs have been substituted with correct values
        assertTrue(modifiedJsonNode.toString()
                .contains(NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_DEFAULT_HOST +
                          NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_PATH));
        assertTrue(modifiedJsonNode.toString()
                .contains(APPLY_NOC_DECISION_SUBMIT_EVENT_DEFAULT_HOST +
                          APPLY_NOC_DECISION_SUBMIT_EVENT_PATH));
    }

    @Test
    @SetEnvironmentVariable(key = MCA_API_BASE_URL, value = LOCALHOST_URL)
    @ClearEnvironmentVariable(key = TEST_STUB_SERVICE_BASE_URL)
    void caseEventMcaApiBaseUrlUpdated() throws Exception {
        JsonNode modifiedJsonNode = EnvironmentURLUtils.updateCallBackURLs(caseEventJson, CASE_EVENT);

        // check JSON has been modified
        assertNotEquals(caseEventJson, modifiedJsonNode);

        // check placeholders are not still present
        assertFalse(modifiedJsonNode.toString().contains(NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_HOST));
        assertFalse(modifiedJsonNode.toString().contains(APPLY_NOC_DECISION_SUBMIT_EVENT_HOST));

        // check URLs have been substituted with correct values
        assertTrue(modifiedJsonNode.toString().contains(LOCALHOST_URL + NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_PATH));
        assertFalse(modifiedJsonNode.toString().contains(LOCALHOST_URL + APPLY_NOC_DECISION_SUBMIT_EVENT_PATH));
        assertTrue(modifiedJsonNode.toString()
                .contains(APPLY_NOC_DECISION_SUBMIT_EVENT_DEFAULT_HOST + APPLY_NOC_DECISION_SUBMIT_EVENT_PATH));
    }

    @Test
    @SetEnvironmentVariable(key = TEST_STUB_SERVICE_BASE_URL, value = LOCALHOST_URL)
    void caseEventTestStubServiceBaseUrlUpdated() throws Exception {
        JsonNode modifiedJsonNode = EnvironmentURLUtils.updateCallBackURLs(caseEventJson, CASE_EVENT);

        // check JSON has been modified
        assertNotEquals(caseEventJson, modifiedJsonNode);

        // check placeholders are not still present
        assertFalse(modifiedJsonNode.toString().contains(NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_HOST));
        assertFalse(modifiedJsonNode.toString().contains(APPLY_NOC_DECISION_SUBMIT_EVENT_HOST));

        // check URLs have been substituted with correct values
        assertTrue(modifiedJsonNode.toString()
                .contains(NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_DEFAULT_HOST +
                          NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_PATH));
        assertFalse(modifiedJsonNode.toString().contains(LOCALHOST_URL + NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_PATH));
        assertTrue(modifiedJsonNode.toString().contains(LOCALHOST_URL + APPLY_NOC_DECISION_SUBMIT_EVENT_PATH));
    }

    @Test
    @SetEnvironmentVariable(key = MCA_API_BASE_URL, value = LOCALHOST_URL)
    @SetEnvironmentVariable(key = TEST_STUB_SERVICE_BASE_URL, value = LOCALHOST_URL)
    void caseEventAllBaseUrlsUpdated() throws Exception {
        JsonNode modifiedJsonNode = EnvironmentURLUtils.updateCallBackURLs(caseEventJson, CASE_EVENT);

        // check JSON has been modified
        assertNotEquals(caseEventJson, modifiedJsonNode);

        // check placeholders are not still present
        assertFalse(modifiedJsonNode.toString().contains(NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_HOST));
        assertFalse(modifiedJsonNode.toString().contains(APPLY_NOC_DECISION_SUBMIT_EVENT_HOST));

        // check URLs have been substituted with correct values
        assertTrue(modifiedJsonNode.toString().contains(LOCALHOST_URL + NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_PATH));
        assertTrue(modifiedJsonNode.toString().contains(LOCALHOST_URL + APPLY_NOC_DECISION_SUBMIT_EVENT_PATH));
    }

    @Test
    @SetEnvironmentVariable(key = MCA_API_BASE_URL, value = "httpsMalformed://" + LOCALHOST_URL)
    void urlNotConvertedOnPreviewEnvironmentTestUrlMalformed()  {
        Exception exception = assertThrows(MalformedURLException.class,
                () -> EnvironmentURLUtils.updateCallBackURLs(caseEventJson, CASE_EVENT));
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("unknown protocol"));
    }

    @Test
    void throwExceptionIfCallBackURLHasNoDefaultValue() throws Exception {
        final String invalidCallBackURLTemplate = "${HOST}";
        caseEventJson = objectMapper.readTree(
                String.format(CASE_EVENT_CONTENT_TEMPLATE,
                        invalidCallBackURLTemplate, NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_PATH,
                        APPLY_NOC_DECISION_SUBMIT_EVENT_HOST, APPLY_NOC_DECISION_SUBMIT_EVENT_PATH)
        );

        Exception exception = assertThrows(InvalidTestDataException.class,
                () -> EnvironmentURLUtils.updateCallBackURLs(caseEventJson, CASE_EVENT));

        assertNotNull(exception);
        assertEquals(exception.getMessage(),
                invalidCallBackURLTemplate + " should be in the format ${ENVIRONMENT_VARIABLE:defaultValue}");
    }

    @Test
    void throwExceptionIfCallBackURLHasBlankDefaultValue() throws Exception {
        final String invalidCallBackURLTemplate = "${HOST:}";
        caseEventJson = objectMapper.readTree(
                String.format(CASE_EVENT_CONTENT_TEMPLATE,
                        invalidCallBackURLTemplate, NOC_REQUEST_AUTO_APPROVAL_SUBMIT_EVENT_PATH,
                        APPLY_NOC_DECISION_SUBMIT_EVENT_HOST, APPLY_NOC_DECISION_SUBMIT_EVENT_PATH)
        );

        Exception exception = assertThrows(InvalidTestDataException.class,
                () -> EnvironmentURLUtils.updateCallBackURLs(caseEventJson, CASE_EVENT));

        assertNotNull(exception);
        assertEquals(exception.getMessage(),
                invalidCallBackURLTemplate + " should be in the format ${ENVIRONMENT_VARIABLE:defaultValue}");
    }
}
