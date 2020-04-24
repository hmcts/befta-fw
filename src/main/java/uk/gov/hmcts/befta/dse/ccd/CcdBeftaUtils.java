package uk.gov.hmcts.befta.dse.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.reflect.ClassPath;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.ExcelTransformer;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer;

import java.io.File;

public class CcdBeftaUtils {

    //for testing
    public static void main(String[] args) {
        convertExcelFileToJson("/Users/dev/code/ccd/befta-fw/src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_BEFTA_JURISDICTION1.xlsx");
        convertExcelFileToJson("/Users/dev/code/ccd/befta-fw/src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_BEFTA_JURISDICTION2.xlsx");
        convertExcelFileToJson("/Users/dev/code/ccd/befta-fw/src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/valid/CCD_BEFTA_JURISDICTION3.xlsx");
    }

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
