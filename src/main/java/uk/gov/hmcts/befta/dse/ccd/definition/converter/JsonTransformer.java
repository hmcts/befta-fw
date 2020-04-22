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
    private String jurisdiction;
    private static final String INPUT_FOLDER_PATH = "/Users/dev/code/ccd/befta-fw/definition_json/TEST";
    private static final String OUTPUT_FOLDER_PATH = "/Users/dev/code/ccd/befta-fw/definition_excel";
    private String inputFolderPath;
    private String outputPath;

    public static void main(String[] args) {
        JsonTransformer jsonTransformer = new JsonTransformer(INPUT_FOLDER_PATH,OUTPUT_FOLDER_PATH);
        jsonTransformer.transformToExcel();
    }

    public void transformToExcel(){
        parseDefinitionJson();
        createWorkbook();
    }

    public JsonTransformer(String inputFolderPath, String outputPath) {
        this.inputFolderPath = inputFolderPath;
        this.outputPath = outputPath;
        this.jurisdiction = getFolderName(inputFolderPath);
    }

    private String getFolderName(String path){
        String delimiter = File.separator;
        String[] dirStructure = path.split(delimiter);
        return  dirStructure[dirStructure.length-1];
    }

    private void parseDefinitionJson() {
        parseDefinitionJson(this.inputFolderPath);
    }

    public HashMap<String, ArrayNode> parseDefinitionJson(String path){
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
        return defFileMap;
    }

    private void createWorkbook() {
        createWorkbook(this.outputPath);
    }

    public void createWorkbook(String outputPath){
        createDirectoryHierarchy(outputPath);

        final XSSFWorkbook workbook = new XSSFWorkbook();
        defFileMap.forEach((key, value) -> {
            sheetReader.addSheetToXlxs(workbook,key,value);
        });

        try {
            FileOutputStream outputStream = new FileOutputStream(outputPath + File.separator + jurisdiction + ".xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDirectoryHierarchy(String path){
        File dir = new File(path);

        if (!dir.exists()){
            if (!dir.mkdirs()){
                throw new RuntimeException("Could not create directory for " + path);
            }
        }
    }

}
