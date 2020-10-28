package uk.gov.hmcts.befta.util;

import org.junit.AssumptionViolatedException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.stream.Collectors;

import io.cucumber.java.Scenario;
import io.restassured.internal.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.befta.exception.FunctionalTestException;
import uk.gov.hmcts.befta.exception.JsonStoreCreationException;
import uk.gov.hmcts.befta.featureToggle.FeatureToggleInfo;

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

    public static void skipScenario(Scenario scenario, FeatureToggleInfo toggleInfo) {
        String skipReason = String.format(
                "The scenario %s is being skipped because of feature(s) toggled off: %s",
                getScenarioTag(scenario), toggleInfo.getDisabledFeatureFlags());
        BeftaUtils.skipScenario(scenario, skipReason);
    }

    public static void skipScenario(Scenario scenario, String reason) {
        log(scenario, reason);
        throw new AssumptionViolatedException(reason);
    }

    public static String getScenarioTag(Scenario scenario) {
        return scenario.getSourceTagNames().stream().filter(tag -> tag.startsWith("@S-")).map(tag -> tag.substring(1))
                .collect(Collectors.joining(","));
    }

    public static void log(Scenario scenario, String logString) {
        log.info(logString);
        scenario.log(logString);
    }
}
