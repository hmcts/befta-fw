package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang.ObjectUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.gov.hmcts.befta.exception.DefinitionTransformerException;

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
            "FixedLists", "Jurisdiction", "UserProfile","SearchAlias", "SearchCasesResultFields");

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
        this(inputFolderPath, null);
    }

    private String setOutputPath(String inputPath){
        return new File(inputPath).getParentFile().getPath();
    }


    private String getFolderName(String path){
        String delimiter = File.separator;
        String[] dirStructure = path.split(delimiter);
        return  dirStructure[dirStructure.length-1];
    }


    public void parseDefinitionJson(String path) {

        File jurisdictionDir = new File(path);
        Map<String, ArrayNode> defFileMap = SHEET_NAMES.stream().collect(Collectors.toMap(Function.identity(), sheetName -> objectMapper.createArrayNode()));
        for (final File subFolder : Objects.requireNonNull(jurisdictionDir.listFiles())) {
            for (final File jsonFile : Objects.requireNonNull(subFolder.listFiles())){
                String sheet = null;
                try {
                    JsonNode rootSheetArray = objectMapper.readTree(jsonFile);
                    for (JsonNode sheetRow : rootSheetArray){
                        sheet = jsonFile.getName().replace(".json","");
                        defFileMap.get(sheet).add(sheetRow);
                    }
                } catch (IOException e) {
                    throw new DefinitionTransformerException("Unable to read json file:" + jsonFile.getPath(), e);
                } catch (NullPointerException e){
                    throw new DefinitionTransformerException("May be a problem generating sheet: " + sheet, e);
                }
            }

        }
        setDefFileMap(defFileMap);
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
        getDefFileMap().forEach((key, value) -> sheetWriter.addSheetToXlxs(workbook,key,value));

        String path = outputPath + File.separator + jurisdiction + ".xlsx";;
        try {
            FileOutputStream outputStream = new FileOutputStream(path);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public Map<String, ArrayNode> getDefFileMap() {
        return defFileMap;
    }

    public void setDefFileMap(Map<String, ArrayNode> defFileMap) {
        this.defFileMap = defFileMap;
    }

}
