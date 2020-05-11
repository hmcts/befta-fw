package uk.gov.hmcts.befta.dse.ccd;

import uk.gov.hmcts.befta.dse.ccd.definition.converter.ExcelTransformer;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer;

import java.io.File;

public class CcdBeftaUtils {

    public static File convertJsonDefinitionToExcel(String jsonFolderPath) {
        String path = new JsonTransformer(jsonFolderPath).transformToExcel();
        return new File(path);
    }

    public static File convertJsonDefinitionToExcel(File jsonFolder) {
        String path = new JsonTransformer(jsonFolder.getPath()).transformToExcel();
        return new File(path);
    }

    public static File convertExcelFileToJson(String excelFilePath) {
        String path = new ExcelTransformer(excelFilePath).transformToJson();
        return new File(path);
    }

    public static File convertExcelFileToJson(File excelFile) {
        String path = new ExcelTransformer(excelFile.getPath()).transformToJson();
        return new File(path);
    }
    
}
