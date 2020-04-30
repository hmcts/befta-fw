package uk.gov.hmcts.befta.dse.ccd;

import uk.gov.hmcts.befta.dse.ccd.definition.converter.ExcelTransformer;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer;

//AC-1
public class DefinitionConverter {

    /**
     * Main method to convert between json and excel versions of a definition file
     * @param args
     *  arg1: to-json | to-excel : key word to convert from excel to json ot from json to excel
     *  arg2 input file path for excel document or parent jurisdiction folder for json version
     *  arg3 (Optional) output folder path for resulting json or excel file. By default will use parent folder from the input location
     *  arg4 (Optional) Boolean: true - use jurisdiction name to generate the parent folder name when converting from excel to JSON,
     *      false - use file name as the folder name
     */
    public static void main(String[] args) {
        if (args.length < 2){
            throw new RuntimeException("At least 2 arguments expected: <to-json|to-excel> <input folder/file path> <Optional: output path>");
        }

        String inputPath = args[1];
        String outPutPath = args.length > 2 ? args[2] : null;
        boolean userJurisdictionAsFolder = args.length <= 3 || Boolean.parseBoolean(args[3]);

        switch (args[0]){
            case "to-json" : new ExcelTransformer(inputPath,outPutPath, userJurisdictionAsFolder).transformToJson();
                break;
            case "to-excel" : new JsonTransformer(inputPath,outPutPath).transformToExcel();
                break;
            default: throw new RuntimeException("Invalid arg, first arg should be either 'to-json' or 'to-excel' but got " +args[0]);
        }


    }

}
