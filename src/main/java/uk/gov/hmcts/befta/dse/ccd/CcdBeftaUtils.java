package uk.gov.hmcts.befta.dse.ccd;

import java.io.File;

import uk.gov.hmcts.befta.dse.ccd.definition.converter.ExcelTransformer;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer;

public class CcdBeftaUtils {

    public static File convertJsonDefinitionToExcel(String jsonFolderPath, CcdEnvironment forEnvironment) {
        String path = new JsonTransformer(forEnvironment, jsonFolderPath).transformToExcel();
        return new File(path);
    }

    public static File convertJsonDefinitionToExcel(File jsonFolder, CcdEnvironment forEnvironment) {
        String path = new JsonTransformer(forEnvironment, jsonFolder.getPath()).transformToExcel();
        return new File(path);
    }

    public static File convertExcelFileToJson(String excelFilePath) {
        String path = new ExcelTransformer(excelFilePath).transformToJson();
        return new File(path);
    }

    public static File convertExcelFileToJson(File excelFile) {
        String path = new ExcelTransformer( excelFile.getPath()).transformToJson();
        return new File(path);
    }

}
