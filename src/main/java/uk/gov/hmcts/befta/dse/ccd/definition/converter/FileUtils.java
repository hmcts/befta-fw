package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import java.io.File;

public class FileUtils {

    private FileUtils() {
    }

    public static void createDirectoryHierarchy(File dir) {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Could not create directory for " + dir);
            }
        }

    }

    public static void createDirectoryHierarchy(String path) {
        File file = new File(path);
        createDirectoryHierarchy(file);
    }

    public static boolean deleteDirectory(String path) {
        return deleteDirectory(new File(path));
    }

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        if (directoryToBeDeleted.exists()) {
            File[] allContents = directoryToBeDeleted.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    deleteDirectory(file);
                }
            }
        }
        System.err.println("Deleting: " + directoryToBeDeleted);
        return directoryToBeDeleted.delete();
    }
}
