package uk.gov.hmcts.befta.util;

import org.apache.logging.log4j.util.Strings;
import org.junit.AssumptionViolatedException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.cucumber.java.Scenario;
import io.restassured.internal.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.exception.JsonStoreCreationException;
import uk.gov.hmcts.befta.featuretoggle.ScenarioFeatureToggleInfo;

import static uk.gov.hmcts.befta.util.Retryable.setRetryListener;

@Slf4j
public class BeftaUtils {

    public static File getSingleFileFromResource(String[] filelocation) {
        if(filelocation!=null&&filelocation.length==1) {
            return getFileFromResource(filelocation[0]);
        }
        else {
            throw new JsonStoreCreationException("Invalid parameter, for array with single entry a Signle directory or a file location.");
        }
    }
    public static File getFileFromResource(String location) {
        URL url = ClassLoader.getSystemResource(location);
        return new File(url.getFile());
    }

    public static File getClassPathResourceIntoTemporaryFile(String resourcePath) {
        return createTempFile(resourcePath,"");
    }

    public static File createJsonDefinitionFileFromClasspath(String resourcePath) {
        String[] path = resourcePath.split("/");
        String directoryStructure = "build" + File.separator + "tmp" + File.separator + path[path.length-3] + File.separator + path[path.length-2];
        FileUtils.createDirectoryHierarchy(directoryStructure);
        return createTempFile(resourcePath,directoryStructure);
    }

    private static File createTempFile(String resourcePath, String directoryPath){
        try {
            int nameStartsAt = resourcePath.lastIndexOf("/");
            String simpleName = resourcePath.substring(nameStartsAt + 1);
            URL resource = BeftaUtils.class.getClassLoader().getResource(resourcePath);
            if (resource == null) {
                throw new FunctionalTestException("Failed to load from filePath: " + resourcePath);
            }
            InputStream stream = resource.openStream();
            byte[] buffer = IOUtils.toByteArray(stream);
            String pathName;
            if (directoryPath.isEmpty()){
                pathName =  "_temp_" + System.currentTimeMillis() + "_" + simpleName;
            } else {
                pathName = directoryPath + File.separator + simpleName;
            }
            File tempFile = new File(pathName);
            tempFile.createNewFile();
            OutputStream outStream = new FileOutputStream(tempFile);
            outStream.write(buffer);
            outStream.close();
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void skipScenario(Scenario scenario, ScenarioFeatureToggleInfo toggleInfo) {
        String skipReason = String.format(
                "The scenario %s is being skipped because of feature(s) toggled off: %s",
                getScenarioTag(scenario), toggleInfo.getDisabledFeatureFlags());
        BeftaUtils.skipScenario(scenario, skipReason);
    }

    public static void skipScenario(Scenario scenario, String reason) {
        defaultLog(scenario, reason);
        throw new AssumptionViolatedException(reason);
    }

    public static String getScenarioTag(Scenario scenario) {
        return scenario.getSourceTagNames().stream().filter(tag -> tag.startsWith("@S-")).map(tag -> tag.substring(1))
                .collect(Collectors.joining(","));
    }

    public static Retryable getRetryableTag(Scenario scenario) {
        String retryInput = scenario.getSourceTagNames().stream()
                .filter(tag -> tag.startsWith("@Retryable"))
                .map(tag -> {
                    log.info("Retryable tag: {}", tag);
                    String regex = "\\(([^(){}@]+(?:\\{[^{}]*}|@[^()]*|[^(){}@])*)\\)";
                    Matcher matcher = Pattern.compile(regex).matcher(tag);
                    if (matcher.find()) {
                        return matcher.group(1).replaceAll("\\s+", "");
                    }
                    return "";
                })
                .collect(Collectors.joining());

        if (retryInput.isEmpty()) {
            return Retryable.RETRYABLE_FROM_CONFIG;
        }

        // default 1000ms
        int delay = getDelay(retryInput);
        // default 3
        int maxAttempts = getMaxAttempts(retryInput);
        // must be defined
        Set<Integer> statusCodes = getStatusCodes(retryInput);
        // default empty
        List<String> matches = getMatches(retryInput);

        return Retryable.builder()
                .delay(delay)
                .maxAttempts(maxAttempts)
                .statusCodes(statusCodes)
                .match(matches)
                .nonRetryableHttpMethods(Collections.emptySet())
                .retryListener(setRetryListener(true))
                .build();
    }

    private static int getDelay(String retryInput) {
        return Integer.parseInt(Optional.of(Pattern
                        .compile("delay=([^,]+|$)").matcher(retryInput))
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .orElse("1000"));
    }

    private static int getMaxAttempts(String retryInput) {
        return Integer.parseInt(Optional.of(Pattern
                        .compile("maxAttempts=([^,]+|$)").matcher(retryInput))
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .orElse("3"));
    }

    private static Set<Integer> getStatusCodes(String retryInput) {
        return Optional.of(Pattern
                        .compile("statusCodes=\\{([^}]+)}")
                        .matcher(retryInput))
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim)
                        .filter(Strings::isNotEmpty)
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet()))
                .orElseThrow(() -> new FunctionalTestException("Missing statusCode configuration in @Retryable"));
    }

    private static List<String> getMatches(String retryInput) {
        return Optional.of(Pattern
                        .compile("match\\s*=\\s*\\{([^}]*)}")
                        .matcher(retryInput))
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim)
                        .filter(Strings::isNotEmpty)
                        .map(match -> match.replaceAll("^\"|\"$", ""))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public static void defaultLog(Scenario scenario, String logString) {
        log.info(logString);
        scenario.log(logString);
    }

    public static void defaultLog(String logString) {
        log.info(logString);
    }

    public static void defaultLog(String logString, Exception e) {
        log.info(logString, e);
    }

    public static String getDateTimeFormatRequested(String key) {
        if (key.equals("today"))
            return "yyyy-MM-dd";
        else if (key.equals("now"))
            return "yyyy-MM-dd'T'HH:mm:ss.SSS";
        else if (key.startsWith("now("))
            return key.substring(4, key.length() - 1);
        return null;
    }
}
