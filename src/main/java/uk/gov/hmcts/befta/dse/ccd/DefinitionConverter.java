package uk.gov.hmcts.befta.dse.ccd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.hmcts.befta.dse.ccd.definition.converter.ExcelTransformer;
import uk.gov.hmcts.befta.dse.ccd.definition.converter.JsonTransformer;

public class DefinitionConverter {

    private static Logger logger = LoggerFactory.getLogger(DefinitionConverter.class);

    public static final String DEFAULT_DEFINITIONS_PATH = "uk/gov/hmcts/befta/dse/ccd/definitions/valid";


    /**
     * Main method to convert between json and excel versions of a definition file
     * 
     * @param args
     * 
     * arg1: to-json | to-excel : key word to convert from excel to json or from json to excel
     * arg2 input file path for excel document or parent jurisdiction folder for json version
     * arg3 (Optional) target environment for which an Excel file is to be generated (with to-excel)
     * arg4 (Optional) output folder path for resulting json or excel file. By default will use parent folder from the input location 
     * arg5 (Optional) Boolean: true - use jurisdiction  name to generate the parent folder name when converting from excel to JSON, false - use file name as the folder name
     */
    public static void main(String[] args) {
        validateArgs(args);

        String transformationKeyword = args[0];
        String inputPath = args[1];
        String forEnvironment = args[2];
        String outputPath = args.length > 3 ? args[3] : null;
        boolean useJurisdictionAsFolder = args.length <= 4 || Boolean.parseBoolean(args[4]);
        try {
            switch (transformationKeyword) {
            case "to-json":
                new ExcelTransformer(inputPath, outputPath, useJurisdictionAsFolder).transformToJson();
                break;
            case "to-excel":
                new JsonTransformer(CcdEnvironment.valueOf(forEnvironment.toUpperCase()), inputPath, outputPath)
                        .transformToExcel();
                break;
            }
            System.out.println("Definition conversion completed successfully.");

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
    }

    private static void validateArgs(String[] args) throws IllegalArgumentException {
        String instructions = "Arguments expected as follows: <to-json|to-excel> <input folder/file path> <Optional: environment (with to-excel)> <Optional: output path> "
                + "<Optional boolean: use jurisdiction as as folder name for json to excel transformation>";

        if (args.length == 0) {
            logger.info(instructions);
            throw new IllegalArgumentException(instructions);
        }

        String transformerKeyword = args[0];

        if (!transformerKeyword.equals("to-json") && !transformerKeyword.equals("to-excel")) {
            logger.info(instructions);
            throw new IllegalArgumentException(
                    "First arg should be either 'to-json' or 'to-excel' but got '" + transformerKeyword + "'");
        }

        int minArgCount = transformerKeyword.equals("to-json") ? 2 : 3;
        
        if (args.length < minArgCount) {
            logger.info(instructions);
            throw new IllegalArgumentException(
                    "At least " + minArgCount
                            + " arguments expected: <to-json|to-excel> <input folder/file path> <Optional: environment (with to-excel)> "
                            + "<Optional: output path> <Optional boolean: use jurisdiction as as folder name for json to excel transformation>");
        }

        if (args.length > 3) {
            String outputPath = args[3];
            if (outputPath.equals("true") || outputPath.equals("false")) {
                logger.info(instructions);
                throw new IllegalArgumentException(
                        "Fourth arg should be a path not a boolean, if you wish to set Jurisdiction "
                                + "name boolean and want the output path to be the same as the input path you can put null for this arg");
            }
        }

        if (args.length > 4) {
            String jurisdictionAsFolderBool = args[4];
            logger.info(instructions);
            if (!jurisdictionAsFolderBool.equals("true") && !jurisdictionAsFolderBool.equals("false")) {
                throw new IllegalArgumentException(
                        "Fifth arg should be a boolean but got: " + jurisdictionAsFolderBool);
            }
        }

    }

}
