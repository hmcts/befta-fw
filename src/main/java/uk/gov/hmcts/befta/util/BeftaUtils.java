package uk.gov.hmcts.befta.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import io.restassured.internal.util.IOUtils;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.FileUtils;
import uk.gov.hmcts.befta.exception.FunctionalTestException;

public class BeftaUtils {

    public static File getClassPathResourceIntoTemporaryFile(String resourcePath) {
        try {
            int nameStartsAt = resourcePath.lastIndexOf("/");
            String simpleName = resourcePath.substring(nameStartsAt + 1);
            URL resource = BeftaUtils.class.getClassLoader().getResource(resourcePath);
            if (resource == null) {
                throw new FunctionalTestException("Failed to load from filePath: " + resourcePath);
            }
            InputStream stream = resource.openStream();
            byte[] buffer = IOUtils.toByteArray(stream);
            File tempFile = new File("_temp_" + System.currentTimeMillis() + "_" + simpleName);
            tempFile.createNewFile();
            OutputStream outStream = new FileOutputStream(tempFile);
            outStream.write(buffer);
            outStream.close();
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO DRY
    public static File createJsonDefinitionFileFromClasspath(String resourceName) {
        String[] path = resourceName.split("/");
        String directoryStructure = path[path.length-3] + File.separator + path[path.length-2];
        FileUtils.createDirectoryHierarchy(directoryStructure);

        try {
            int nameStartsAt = resourceName.lastIndexOf("/");
            String simpleName = resourceName.substring(nameStartsAt + 1);
            URL resource = BeftaUtils.class.getClassLoader().getResource(resourceName);
            if (resource == null) {
                throw new FunctionalTestException("Failed to load from filePath: " + resourceName);
            }
            InputStream stream = resource.openStream();
            byte[] buffer = IOUtils.toByteArray(stream);
            File tempFile = new File(directoryStructure + File.separator + simpleName);
            tempFile.createNewFile();
            OutputStream outStream = new FileOutputStream(tempFile);
            outStream.write(buffer);
            outStream.close();
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
