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

    private static final String TEMPORARY_DEFINITION_FOLDER = "definition_files";

    public static File getClassPathResourceIntoTemporaryFile(String resourcePath) {
        return createTempFile(resourcePath,"");
    }

    public static File createJsonDefinitionFileFromClasspath(String resourcePath) {
        String[] path = resourcePath.split("/");
        String directoryStructure = TEMPORARY_DEFINITION_FOLDER + File.separator + path[path.length-3] + File.separator + path[path.length-2];
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


}
