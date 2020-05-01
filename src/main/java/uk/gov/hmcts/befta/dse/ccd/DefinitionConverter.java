package uk.gov.hmcts.befta.dse.ccd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.ExcelTransformer;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer;

public class DefinitionConverter {

    private static Logger logger = LoggerFactory.getLogger(DefinitionConverter.class);

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
        validateArgs(args);

        String transformationKeyword  = args[0];
        String inputPath = args[1];
        String outputPath = args.length > 2 ? args[2] : null;
        boolean useJurisdictionAsFolder = args.length <= 3 || Boolean.parseBoolean(args[3]);

        switch (transformationKeyword){
            case "to-json" : new ExcelTransformer(inputPath,outputPath, useJurisdictionAsFolder).transformToJson();
                break;
            case "to-excel" : new JsonTransformer(inputPath,outputPath).transformToExcel();
                break;
        }


    }

    private static void validateArgs(String[] args) throws IllegalArgumentException {
        String instructions = "Arguments expected as follows: <to-json|to-excel> <input folder/file path> <Optional: output path> " +
                "<Optional boolean: use jurisdiction as as folder name for json to excel transformation>";


        if (args.length < 2){
            logger.info(instructions);
            throw new IllegalArgumentException("At least 2 arguments expected: <to-json|to-excel> <input folder/file path> " +
                    "<Optional: output path> <Optional boolean: use jurisdiction as as folder name for json to excel transformation>");
        }

        String transformerKeyword = args[0];
        if (!transformerKeyword.equals("to-json") && !transformerKeyword.equals("to-excel")){
            logger.info(instructions);
            throw new IllegalArgumentException("First arg should be either 'to-json' or 'to-excel' but got "+ transformerKeyword);
        }


        if (args.length > 2){
            String outputPath = args[2];
            if (outputPath.equals("true") || outputPath.equals("false")){
                logger.info(instructions);
                throw new IllegalArgumentException("Third arg should be a path not a boolean, if you wish to set Jurisdiction " +
                        "name boolean and want the output path to be the same as the input path you can put null for this arg");
            }
        }

        if (args.length > 3){
            String jurisdictionAsFolderBool = args[3];
            logger.info(instructions);
            if (!jurisdictionAsFolderBool.equals("true") && !jurisdictionAsFolderBool.equals("false")){
                throw new IllegalArgumentException("Forth arg should be a boolean but got: " + jurisdictionAsFolderBool);
            }
        }

    }

}
