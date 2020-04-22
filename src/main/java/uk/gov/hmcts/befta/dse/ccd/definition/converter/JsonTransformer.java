package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

/**
 * Read a JSON representation of a definition file
 */
public class JsonTransformer {

    private ObjectMapper objectMapper = new ObjectMapper();
    private HashMap<String, ArrayNode> defFileMap = new HashMap<>();
    private SheetWriter sheetReader = new SheetWriter();
    private static final String INPUT_FOLDER_PATH = "/Users/dev/code/ccd/befta-fw/definition_json/AUTOTEST1";
    private static final String OUTPUT_FOLDER_PATH = "DefinitionFileTransformed.xlsx";

    public static void main(String[] args) {
        JsonTransformer reader = new JsonTransformer();
        reader.parseDefinitionJson(INPUT_FOLDER_PATH);
        reader.createWorkbook(OUTPUT_FOLDER_PATH);


    }

    public void parseDefinitionJson(String path){
        File jurisdictionDir = new File(path);
        for (final File jsonFile : Objects.requireNonNull(jurisdictionDir.listFiles())) {
            try {
                ArrayNode finalSheetObject = objectMapper.createArrayNode();
                JsonNode rootSheetArray = objectMapper.readTree(jsonFile);
                for (JsonNode sheetRow : rootSheetArray){
                    finalSheetObject.add(sheetRow);
                }

                String sheetName = jsonFile.getName().replace(".json","");
                defFileMap.put(sheetName ,finalSheetObject);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createWorkbook(String outputFileName){
        final XSSFWorkbook workbook = new XSSFWorkbook();
        defFileMap.forEach((key, value) -> {
            sheetReader.addSheetToXlxs(workbook,key,value);
        });

        try {
            FileOutputStream outputStream = new FileOutputStream(outputFileName);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
