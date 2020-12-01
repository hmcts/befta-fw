package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvironmentURLUtils {

    private static final String BASE_URL_PLACEHOLDER_REGEX = "\\$\\{(.*?)\\}";
    private static final List<String> SHEETS_FOR_URL_SUBSTITUTIONS = Arrays.asList("CaseEvent", "CaseEventToFields");

    protected static final String MCA_API_BASE_URL = "MCA_API_BASE_URL";
    protected static final String TEST_STUB_SERVICE_BASE_URL = "TEST_STUB_SERVICE_BASE_URL";

    public static JsonNode updateCallBackURLs(JsonNode rootSheetArray, String jsonFileName)
            throws JsonProcessingException, MalformedURLException {
        if (SHEETS_FOR_URL_SUBSTITUTIONS.contains(jsonFileName)) {
            return new ObjectMapper().readTree(parseCallbackHostValues(rootSheetArray.toString()));
        } else {
            return rootSheetArray;
        }
    }

    private static String parseCallbackHostValues(String sheet) throws MalformedURLException {
        Matcher matcher = Pattern.compile(BASE_URL_PLACEHOLDER_REGEX).matcher(sheet);

        while (matcher.find()) {
            BaseUrlPlaceholder matchedResult = parseDefaultHostValue(matcher.group(1));

            String replacementUrl = getURLStringFromEnvironmentValue(matchedResult.environmentVariable);
            if (replacementUrl == null) {
                replacementUrl = matchedResult.urlDefaultValue;
            }
            sheet = sheet.replaceAll(String.format("\\$\\{%s.*?\\}", matchedResult.environmentVariable),
                    replacementUrl);
        }

        return sheet;
    }

    private static String getURLStringFromEnvironmentValue(String environmentName) throws MalformedURLException {
        String environmentValue = System.getenv(environmentName);
        return environmentValue != null ? new URL(environmentValue).toString() : null;
    }

    private static BaseUrlPlaceholder parseDefaultHostValue(String value) {
        BaseUrlPlaceholder baseUrlPlaceholder = null;
        if (value != null) {
            String[] split = value.split(":", 2);
            baseUrlPlaceholder = new BaseUrlPlaceholder(split[0], split[1]);
        }

        return baseUrlPlaceholder;
    }

    @AllArgsConstructor
    @Data
    private static class BaseUrlPlaceholder {
        private String environmentVariable;
        private String urlDefaultValue;
    }
}
