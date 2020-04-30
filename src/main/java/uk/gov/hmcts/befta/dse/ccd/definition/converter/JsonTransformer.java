package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Read a JSON representation of a definition file
 */
public class JsonTransformer {

    private ObjectMapper objectMapper = new ObjectMapper();
    private SheetWriter sheetWriter = new SheetWriter();
    private String jurisdiction;
    private String inputFolderPath;
    private String outputPath;

    private static final List<String> SHEET_NAMES = Arrays.asList("CaseEvent", "AuthorisationCaseEvent",
            "AuthorisationCaseField", "AuthorisationCaseState", "AuthorisationCaseType", "AuthorisationComplexType",
            "CaseEventToFields", "CaseField", "CaseRoles", "CaseTypeTab" ,"SearchInputFields", "SearchResultFields", "State",
            "WorkBasketInputFields", "WorkBasketResultFields", "Category", "Banner", "CaseType", "ComplexTypes", "EventToComplexTypes",
            "FixedLists", "Jurisdiction", "UserProfile","SearchAlias");

    private Map<String, ArrayNode> defFileMap;

    public String transformToExcel(){
        parseDefinitionJson();
        return createWorkbook();
    }

    public JsonTransformer(String inputFolderPath, String outputPath) {
        this.inputFolderPath = inputFolderPath;
        this.outputPath = outputPath !=null ? outputPath : setOutputPath(inputFolderPath);
        this.jurisdiction = getFolderName(inputFolderPath);
    }

    public JsonTransformer(String inputFolderPath) {
        this.inputFolderPath = inputFolderPath;
        this.outputPath = setOutputPath(inputFolderPath);
        this.jurisdiction = getFolderName(inputFolderPath);
    }

    private String setOutputPath(String inputPath){
        return new File(inputPath).getParentFile().getPath();
    }


    private String getFolderName(String path){
        String delimiter = File.separator;
        String[] dirStructure = path.split(delimiter);
        return  dirStructure[dirStructure.length-1];
    }


    public Map<String, ArrayNode> parseDefinitionJson(String path){

        File jurisdictionDir = new File(path);
        defFileMap = SHEET_NAMES.stream().collect(Collectors.toMap(Function.identity(), sheetName -> objectMapper.createArrayNode()));
        for (final File subFolder : Objects.requireNonNull(jurisdictionDir.listFiles())) {
            for (final File jsonFile : Objects.requireNonNull(subFolder.listFiles())){
                try {
                    JsonNode rootSheetArray = objectMapper.readTree(jsonFile);
                    for (JsonNode sheetRow : rootSheetArray){
                        String sheet = jsonFile.getName().replace(".json","");
                        defFileMap.get(sheet).add(sheetRow);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return defFileMap;
    }

    private void parseDefinitionJson() {
        parseDefinitionJson(this.inputFolderPath);
    }

    private String createWorkbook() {
        return createWorkbook(this.outputPath);
    }

    public String createWorkbook(String outputPath){
        FileUtils.createDirectoryHierarchy(outputPath);
        final XSSFWorkbook workbook = new XSSFWorkbook();
        defFileMap.forEach((key, value) -> {
            sheetWriter.addSheetToXlxs(workbook,key,value);
        });

        try {
            outputPath = outputPath + File.separator + jurisdiction + ".xlsx";
            FileOutputStream outputStream = new FileOutputStream(outputPath);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputPath;
    }

}
