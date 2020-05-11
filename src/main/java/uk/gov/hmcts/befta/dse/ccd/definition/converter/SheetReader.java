package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.*;
import uk.gov.hmcts.befta.exception.DefinitionTransformerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import static org.apache.poi.ss.usermodel.CellType.*;

public class SheetReader {

    private static final int HEADER_ROW = 2;
    private static final int DATA_START_ROW = 3;
    private ArrayList<String> keys;
    private ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Populate complete JSON Object for a ccd definition sheet/tab
     * @param sheet - single definition sheet(tab) object
     * @return ArrayNode object containing array of json objects, each item in array is a row from the definition sheet
     */
    public ArrayNode transformToJson(Sheet sheet){
        keys = getHeaders(sheet);
        ArrayNode sheetJsonArray = objectMapper.createArrayNode();

        //iterate over all rows after the header
        int totalRows = sheet.getPhysicalNumberOfRows();
        for (int i = DATA_START_ROW; i <  totalRows + 1; i++) {
            Row row = sheet.getRow(i);
            ObjectNode objectNodeForRow = generateJsonNodeForRow(row);
            if (!rowEmpty(objectNodeForRow)){
                JsonNode jsonNode = convertToJsonNode(objectNodeForRow);
                sheetJsonArray.add(jsonNode);
            }
        }

        if (sheetJsonArray.size() ==0){
            sheetJsonArray.add(generateEmptyRow());
        }

        return sheetJsonArray;
    }

    private boolean rowEmpty(ObjectNode objectNodeForRow) {

        if (objectNodeForRow == null){
            return true;
        }

        Iterator it = objectNodeForRow.fieldNames();
        boolean empty = true;
        while (it.hasNext()){
            String key = (String) it.next();
            if (!objectNodeForRow.get(key).asText().isEmpty()){
                empty = false;
                break;
            }
        }

        return empty;
    }


    /**
     * If a sheet has no valid rows we insert a single empty row equivalent in the json structure so we can keep
     * a record of the headers for purposes of a json -> xlsx transformation
     * @return
     */
    private JsonNode generateEmptyRow(){
        ObjectNode rowJsonObject = objectMapper.createObjectNode();
        for(String column : keys){
            rowJsonObject.put(column,"");
        }
        return convertToJsonNode(rowJsonObject);
    }

    /**
     * Get the CCD Header values on the sheet
     * @param sheet
     * @return Array of the headers the columns from a ccd definition sheet/tab
     */
    private ArrayList getHeaders(Sheet sheet){
        ArrayList<String> header = new ArrayList<String>();
        Iterator cells = sheet.getRow(HEADER_ROW).cellIterator();
        while (cells.hasNext()){
            Cell cell = (Cell)cells.next();
            String value = cell.getStringCellValue();
            if (!value.isEmpty()){
                header.add(value);
            }
        }

        return header;
    }


    private JsonNode convertToJsonNode(ObjectNode objectNode) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(objectNode.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonNode;
    }


    /**
     * for a spreadsheet Row, generate a json node with keys=header values=rowValues
     * @param row - Row object from a Sheet
     * @return ObjectNode representation for single excel row in definition file,
     * if empty row is detected as all cells are empty, return null instead
     */
    private ObjectNode generateJsonNodeForRow(Row row) {

        if (Objects.isNull(row) || row.getPhysicalNumberOfCells() == 0){
            return null;
        }

        ObjectNode rowJsonObject = objectMapper.createObjectNode();
        int columns = keys.size();
        int first = row.getFirstCellNum();
        for (int j = first; j <columns ; j++) {
            String key = keys.get(j);
            Cell cell = row.getCell(j);
            CellType type =  getCellType(cell);

            //Check cell type and get value using appropriate method
            if (key.equals("LiveFrom") || key.equals("LiveTo")){
                if (type != BLANK){
                    rowJsonObject.put(key, ExcelProcessingUtils.getStringDateFromCell(cell));
                } else {
                    rowJsonObject.put(key,"");
                }
            } else if (type == NUMERIC) {
                rowJsonObject.put(key, (int) cell.getNumericCellValue());
            } else if (type == STRING){
                String val = cell.getStringCellValue();
                rowJsonObject.put(key, val);
            } else if (type == BLANK) {
                rowJsonObject.put(key,""); //todo should this be null?
            } else {
                throw new DefinitionTransformerException("unsupported excel cell type value found:" + type);
            }

        }

        return rowJsonObject;
    }

    /**
     * Get celltype, ccd Custom date field throws a null pointer exception as poi library cannot detect 'Custom' cell type
     * so we catch and model as blank and handle later by column/header name
     * @param cell
     * @return CellType
     */
    private CellType getCellType(Cell cell) {
        CellType cellType;
        try {
            cellType = cell.getCellType();
        } catch (NullPointerException e) {
            cellType = BLANK;
        }

        return cellType;

    }

}
