package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import uk.gov.hmcts.befta.dse.ccd.CcdEnvironment;
import uk.gov.hmcts.befta.exception.DefinitionTransformerException;
import uk.gov.hmcts.befta.exception.InvalidTestDataException;
import uk.gov.hmcts.befta.util.BeftaUtils;
import uk.gov.hmcts.befta.util.FileUtils;

/**
 * Read a JSON representation of a definition file
 */
public class JsonTransformer {

    private ObjectMapper objectMapper = new ObjectMapper();
    private SheetWriter sheetWriter = new SheetWriter();
    private String jurisdiction;
    private String inputFolderPath;
    private String outputPath;
    private CcdEnvironment forEnvironment;

    private static final List<String> SHEET_NAMES = Arrays.asList("CaseEvent", "AuthorisationCaseEvent",
            "AuthorisationCaseField", "AuthorisationCaseState", "AuthorisationCaseType", "AuthorisationComplexType",
            "CaseEventToFields", "CaseField", "CaseRoles", "CaseTypeTab" ,"SearchInputFields", "SearchResultFields", "State",
            "WorkBasketInputFields", "WorkBasketResultFields", "Category", "Banner", "CaseType", "ComplexTypes", "EventToComplexTypes",
            "FixedLists", "Jurisdiction", "UserProfile","SearchAlias", "SearchCasesResultFields",
            "NoticeOfChangeConfig", "ChallengeQuestion", "RoleToAccessProfiles");

    private Map<String, ArrayNode> defFileMap;

    public String transformToExcel(){
        parseDefinitionJson();
        return createWorkbook();
    }

    public JsonTransformer(CcdEnvironment forEnvironment, String inputFolderPath, String outputPath) {
        this.inputFolderPath = inputFolderPath;
        this.outputPath = outputPath !=null ? outputPath : setOutputPath(inputFolderPath);
        this.jurisdiction = getFolderName(inputFolderPath);
        this.forEnvironment = forEnvironment;
    }

    public JsonTransformer(CcdEnvironment dataSetupEnvironment, String inputFolderPath) {
        this(dataSetupEnvironment, inputFolderPath, null);
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
                    String jsonFileNameNoSuffix = jsonFile.getName().replace(".json", "");

                    rootSheetArray = EnvironmentURLUtils.updateCallBackURLs(rootSheetArray, jsonFileNameNoSuffix,
                            forEnvironment);

                    for (JsonNode sheetRow : rootSheetArray){
                        sheet = jsonFileNameNoSuffix;
                        defFileMap.get(sheet).add(sheetRow);
                    }
                } catch (InvalidTestDataException | IOException e) {
                    throw new DefinitionTransformerException("Unable to read or process json file:" + jsonFile.getPath(), e);
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
            BeftaUtils.defaultLog("Generated [" + path + "] for target environment " + forEnvironment);
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
