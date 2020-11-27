package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvironmentUrlUtilsTest {

    private static final String CASE_EVENT =  "CaseEvent";
    private static final String TEST_CASE_EVENT_FILE_LOCATION = "framework-test-data/environment-url-utils-test-data/CaseEvent.json";
    private static final String PR_HOST = "aac-manage-case-assignment-pr-164.service.core-compute-preview.internal";
    private static final String LOCALHOST_URL = "http://localhost:8080/dummy-api";
    private static final String TEST_URL_ENV = "TEST_URL";

    ObjectMapper objectMapper = new ObjectMapper();

    private File caseEventFile;
    JsonNode caseEventJson;

    @BeforeEach
    void setup() throws IOException {
        caseEventFile = new File(ClassLoader.getSystemResource(TEST_CASE_EVENT_FILE_LOCATION).getFile());
        caseEventJson = objectMapper.readTree(caseEventFile);
    }

    @Test
    @SetEnvironmentVariable(key = TEST_URL_ENV, value = LOCALHOST_URL)
    void onlyModifyCaseEventFiles() throws JsonProcessingException {
        JsonNode modifiedJsonNode = EnvironmentUrlUtils.updateCallBackURLs(caseEventJson, "AFileNameThatIsNotCaseEvent");
        assertEquals(caseEventJson, modifiedJsonNode);
    }

    @Test
    @SetEnvironmentVariable(key = TEST_URL_ENV, value = "http://localhost:8080/dummy-api")
    void urlsAreConvertedToLocalHostOnLocalEnvironment() throws IOException {
        JsonNode modifiedJsonNode = EnvironmentUrlUtils.updateCallBackURLs(caseEventJson, CASE_EVENT);
        assertNotEquals(caseEventJson, modifiedJsonNode);
        assertFalse(modifiedJsonNode.toString().contains("aat"));
        assertTrue(modifiedJsonNode.toString().contains("ccd-test-stubs-service:5555"));
    }

    @Test
    @SetEnvironmentVariable(key = TEST_URL_ENV, value = "http://my.aat.environment:8080/dummy-api")
    void urlsAreNotModifiedWhenRunningOnAatEnvironment() throws IOException {
        JsonNode modifiedJsonNode = EnvironmentUrlUtils.updateCallBackURLs(caseEventJson, CASE_EVENT);
        assertEquals(caseEventJson, modifiedJsonNode);
        assertTrue(modifiedJsonNode.toString().contains("aat"));
        assertFalse(modifiedJsonNode.toString().contains("ccd-test-stubs-service:5555"));
    }

    @Test
    @SetEnvironmentVariable(key = TEST_URL_ENV, value = "https://" + PR_HOST)
    void urlsConvertedToPRHostOnPreviewEnvironment() throws IOException {
        JsonNode modifiedJsonNode = EnvironmentUrlUtils.updateCallBackURLs(caseEventJson, CASE_EVENT);
        assertNotEquals(caseEventJson, modifiedJsonNode);
        assertFalse(modifiedJsonNode.toString().contains("aat"));
        assertTrue(modifiedJsonNode.toString().contains("https://" + PR_HOST));
        assertTrue(modifiedJsonNode.toString().contains("http://" + PR_HOST));
    }

    @Test
    @SetEnvironmentVariable(key = TEST_URL_ENV, value = "httpsMalformed://" + PR_HOST)
    void urlNotConvertedOnPreviewEnvironmentTestUrlMalformed() throws IOException {
        JsonNode modifiedJsonNode = EnvironmentUrlUtils.updateCallBackURLs(caseEventJson, CASE_EVENT);
        assertEquals(caseEventJson, modifiedJsonNode);
    }
}