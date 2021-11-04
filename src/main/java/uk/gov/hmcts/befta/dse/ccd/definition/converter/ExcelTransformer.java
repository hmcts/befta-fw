package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.gov.hmcts.befta.util.FileUtils;

public class ExcelTransformer {
    private static final List<String> PER_CASE_TYPE_SHEET_NAMES = Arrays.asList("CaseEvent", "AuthorisationCaseEvent",
            "AuthorisationCaseField", "AuthorisationCaseState", "AuthorisationCaseType", "AuthorisationComplexType",
            "CaseEventToFields", "CaseField", "CaseRoles", "CaseTypeTab", "SearchInputFields", "SearchResultFields",
            "State", "WorkBasketInputFields", "WorkBasketResultFields", "Category", "SearchAlias");
    private ObjectWriter writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
    private SheetReader sheetTransformer = new SheetReader();
    private HashMap<String, ArrayNode> defFileMap = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private String inputExcelFilePath;
    private String outputFolderPath;
    private String fileName;
    private boolean useJurisdictionAsFolderName;

    public String transformToJson() {
        parseExcelFile();
        return writeToJson(this.outputFolderPath);
    }

    public ExcelTransformer(String inputExcelFilePath, String outputFolderPath, boolean useJurisdictionAsFolderName) {
        this.inputExcelFilePath = inputExcelFilePath;
        this.useJurisdictionAsFolderName = useJurisdictionAsFolderName;
        this.outputFolderPath = outputFolderPath != null ? outputFolderPath : setOutputPath(inputExcelFilePath);
    }

    public ExcelTransformer(String inputExcelFilePath) {
        this(inputExcelFilePath, null, true);
    }

    private String setOutputPath(String inputExcelFile) {
        return new File(inputExcelFile).getParentFile().getPath();
    }

    /**
     * Read Excel file and return a Workbook object
     */
    private Workbook getExcelFile(String filePath) {
        FileInputStream fInputStream = null;
        Workbook excelWorkBook = null;
        try {
            fInputStream = new FileInputStream(filePath.trim());
            String fileName = new File(filePath.trim()).getName().replace(".xlsx", "");
            setFileName(fileName);
            /* Create the workbook object. */
            excelWorkBook = WorkbookFactory.create(fInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException ignored) {
        }
        return excelWorkBook;
    }

    /**
     * Parse an xlxs document into a map of excel sheets k=sheet name v=object
     * representation of that sheet. Saving into a Map object will allow reuse of
     * this object in the future
     * 
     * @param xlxsPath
     * @return HashMap<String,ArrayNode>
     */
    private HashMap<String, ArrayNode> parseExcelFile(String xlxsPath) {
        Workbook workbook = getExcelFile(xlxsPath);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = workbook.getSheetAt(i).getSheetName();
            ArrayNode sheetObject = sheetTransformer.transformToJson(sheet);
            defFileMap.put(sheetName, sheetObject);
        }
        return defFileMap;
    }

    private HashMap<String, ArrayNode> parseExcelFile() {
        return parseExcelFile(this.inputExcelFilePath);
    }

    /**
     * use HashMap<String,ArrayNode> defFileMap to write out json files, a file for
     * each def file sheet. List output folder, jurisdiction subfolder is
     * automatically created
     * 
     * @param outputFilePath
     */
    private String writeToJson(String outputFilePath) {
        String jurisdiction = defFileMap.get("Jurisdiction").get(0).get("ID").asText();
        String folderName = useJurisdictionAsFolderName ? jurisdiction : getFileName();
        String outputFolderPath = outputFilePath + File.separator + folderName;
        FileUtils.deleteDirectory(outputFolderPath);

        defFileMap.forEach((key, value) -> {
            try {
                if (isSheetPerCaseType(key)) {
                    Map<String, ArrayNode> caseTypeArrayNodes = splitIntoCaseTypes(value);
                    for (String caseTypeId : caseTypeArrayNodes.keySet()) {
                        writeNodeToFile(caseTypeArrayNodes.get(caseTypeId), new File(
                                outputFolderPath + File.separator + caseTypeId + File.separator + key + ".json"));
                    }
                } else {
                    writeNodeToFile(value,
                            new File(outputFolderPath + File.separator + "common" + File.separator + key + ".json"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return outputFolderPath;
    }

    private void writeNodeToFile(ArrayNode value, File file) throws Exception {
        FileUtils.createDirectoryHierarchy(file.getParentFile());
        writer.writeValue(file, value);
        BufferedWriter bf = new BufferedWriter(new FileWriter(file, true));
        bf.append("\n");
        bf.close();
    }

    private Map<String, ArrayNode> splitIntoCaseTypes(ArrayNode combinedArrayNode) {
        Map<String, ArrayNode> caseTypeArrayNodes = getInitialEmptyMapContainingAnArrayNodeForEachCaseType();
        for (JsonNode node : combinedArrayNode) {
            String caseTypeId = node.get("CaseTypeID").asText();

            // This means empty tab
            if (caseTypeId.isEmpty()) {
                caseTypeArrayNodes.keySet().forEach(key -> caseTypeArrayNodes.get(key).add(node));
            } else {
                caseTypeArrayNodes.get(caseTypeId).add(node);
            }
        }

        return caseTypeArrayNodes;
    }

    private Map<String, ArrayNode> getInitialEmptyMapContainingAnArrayNodeForEachCaseType() {
        Map<String, ArrayNode> emptyNodes = new HashMap<>();
        for (JsonNode caseTypeNode : defFileMap.get("CaseType")) {
            emptyNodes.put(caseTypeNode.get("ID").asText(), objectMapper.createArrayNode());
        }
        return emptyNodes;
    }

    private boolean isSheetPerCaseType(String key) {
        return PER_CASE_TYPE_SHEET_NAMES.contains(key);
    }

    private void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String getFileName() {
        return this.fileName;
    }

}
