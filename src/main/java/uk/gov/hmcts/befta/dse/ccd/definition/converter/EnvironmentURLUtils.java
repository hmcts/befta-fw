package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.hmcts.befta.dse.ccd.CcdEnvironment;
import uk.gov.hmcts.befta.exception.InvalidTestDataException;
import uk.gov.hmcts.befta.util.BeftaUtils;

public class EnvironmentURLUtils {

    private static final String BASE_URL_PLACEHOLDER_REGEX = "\\$\\{(.*?)}";
    private static final List<String> SHEETS_FOR_URL_SUBSTITUTIONS = Arrays.asList("CaseType",
                                                                                   "CaseEvent",
                                                                                   "CaseEventToFields");

    public static JsonNode updateCallBackURLs(JsonNode rootSheetArray, String jsonFileName,
            CcdEnvironment forEnvironment)
            throws JsonProcessingException, MalformedURLException, InvalidTestDataException {
        if (SHEETS_FOR_URL_SUBSTITUTIONS.contains(jsonFileName)) {
            return new ObjectMapper().readTree(parseCallbackHostValues(rootSheetArray.toString(), forEnvironment));
        } else {
            return rootSheetArray;
        }
    }

    private static String parseCallbackHostValues(String sheet, CcdEnvironment forEnvironment)
            throws MalformedURLException, InvalidTestDataException {
        Matcher matcher = Pattern.compile(BASE_URL_PLACEHOLDER_REGEX).matcher(sheet);

        while (matcher.find()) {
            BaseUrlPlaceholder matchedResult = parseDefaultHostValue(matcher);

            String replacementUrl = getURLStringFromEnvironmentValue(matchedResult.getEnvironmentVariableName());
            if (replacementUrl == null) {
                replacementUrl = matchedResult.getUrlDefaultValue();
            }

            String finalUrl = replaceEnvironmentForTargetEnvironment(replacementUrl, forEnvironment);
            if (!finalUrl.equals(replacementUrl)) {
                BeftaUtils.defaultLog("Translated [" + replacementUrl + "] to [" + finalUrl + "].");
            }

            sheet = sheet.replace(matcher.group(0), finalUrl);
        }

        return sheet;
    }

    private static String replaceEnvironmentForTargetEnvironment(String url, CcdEnvironment forEnvironment) {
        if (forEnvironment == null || forEnvironment == CcdEnvironment.LOCAL)
            return url;
        for (CcdEnvironment environment : CcdEnvironment.values()) {
            if (!environment.equals(forEnvironment)) {
                String find = "-" + environment.name().toLowerCase() + "\\.";
                String replaceWith = "-" + forEnvironment.name().toLowerCase() + ".";
                url = url.replaceAll(find, replaceWith);
            }
        }
        return url;
    }

    private static String getURLStringFromEnvironmentValue(String environmentVariableName)
            throws MalformedURLException {
        String environmentValue = System.getenv(environmentVariableName);
        return environmentValue != null ? new URL(environmentValue).toString() : null;
    }

    private static BaseUrlPlaceholder parseDefaultHostValue(Matcher matcher) throws InvalidTestDataException {
        String[] split = matcher.group(1).split(":", 2);

        if (split.length != 2 || split[1].isEmpty()) {
            throw new InvalidTestDataException(
                    String.format("%s should be in the format ${ENVIRONMENT_VARIABLE:defaultValue}",
                    matcher.group(0)));
        }
        return new BaseUrlPlaceholder(split[0], split[1]);
    }

    @AllArgsConstructor
    @Data
    private static class BaseUrlPlaceholder {
        private final String environmentVariableName;
        private final String urlDefaultValue;
    }
}
