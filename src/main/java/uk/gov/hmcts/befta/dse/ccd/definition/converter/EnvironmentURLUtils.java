package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.hmcts.befta.BeftaMain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class EnvironmentURLUtils {

    private static final List<String> SHEETS_FOR_URL_SUBSTITUTIONS = Arrays.asList("CaseEvent");
    private static final String LOCALHOST = "localhost";
    private static final String PREVIEW = "service.core-compute-preview.internal";
    private static final String DEFAULT_AAT_CALLBACK_HOST =
            "ccd-test-stubs-service-aat.service.core-compute-aat.internal";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode updateCallBackURLs(JsonNode rootSheetArray, String jsonFileName) throws JsonProcessingException {
        return objectMapper.readTree(substituteWithEnvironmentSpecificURL(rootSheetArray.toString(), jsonFileName));
    }

    private static String substituteWithEnvironmentSpecificURL(String rootSheetArrayString, String jsonFileName) {
        String testUrlString = BeftaMain.getConfig().getTestUrl();
        String returnValue = rootSheetArrayString;

        if (testUrlString != null && SHEETS_FOR_URL_SUBSTITUTIONS.contains(jsonFileName)) {
            if (testUrlString.contains(LOCALHOST)) {
                returnValue =
                        rootSheetArrayString.replaceAll(DEFAULT_AAT_CALLBACK_HOST, "ccd-test-stubs-service:5555");
            }

            if (testUrlString.endsWith(PREVIEW)) {
                try {
                    URL testURL = new URL(testUrlString);
                    returnValue = rootSheetArrayString.replaceAll(DEFAULT_AAT_CALLBACK_HOST, testURL.getHost());
                } catch (MalformedURLException e) {
                    returnValue = rootSheetArrayString;
                }
            }
        }
        return returnValue;
    }

}
