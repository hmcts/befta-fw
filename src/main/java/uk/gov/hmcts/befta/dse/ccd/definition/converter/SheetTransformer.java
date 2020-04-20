package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static org.apache.poi.ss.usermodel.CellType.*;

public class SheetTransformer {

    private static final int HEADER_ROW = 2;
    private static final int DATA_START_ROW = 3;
    private ArrayList keys;
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Populate complete json for a ccd definition sheet/tab
     */
    public ArrayNode transformToJson(Sheet sheet){
        keys = getHeaders(sheet);
        ArrayNode sheetJsonArray = objectMapper.createArrayNode();

        //iterate over all rows after the header
        int totalRows = sheet.getPhysicalNumberOfRows();
        for (int i = DATA_START_ROW; i <  totalRows ; i++) {
            Row row = sheet.getRow(i);
            ObjectNode objectNodeForRow = generateJsonNodeForRow(row);
            if (objectNodeForRow != null){
                JsonNode jsonNode = convertToJsonNode(objectNodeForRow);
                sheetJsonArray.add(jsonNode);
            }
        }

        return sheetJsonArray;
    }


    /**
     * Get the Header values on the sheet
     * @param sheet
     * @return
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
     * for each row generate a json node with keys=header values=rowValues and add to main json
     */
    private ObjectNode generateJsonNodeForRow(Row row) {
        ObjectNode rowJsonObject = objectMapper.createObjectNode();

        int emptyCells = 0;

        int columns = keys.size();
        int first = row.getFirstCellNum();
        for (int j = first; j <columns ; j++) {
            String key = keys.get(j).toString();
            Cell cell = row.getCell(j);
            CellType type =  cell.getCellTypeEnum();

            //Check cell type and get value using appropriate method
            if (type == NUMERIC){
                //need to handle date field as it's stored as floating point number
                if (key.equals("LiveFrom") || key.equals("LiveTo")){
                    String stringDateValue = new DataFormatter().formatCellValue(cell);
                    rowJsonObject.put(keys.get(j).toString(), stringDateValue);
                } else {
                    rowJsonObject.put(key, cell.getNumericCellValue());
                }
            } else if (type == STRING){
                String val = cell.getStringCellValue();
                rowJsonObject.put(key, val);
            } else if (type == BLANK) {
                emptyCells++;
            } else {
                System.out.print("unsupported cell type value found: ");
                System.out.println(type);
            }

        }

        //Some rows have no values in, we want to return null so they can later be easily ignored rather than returning a json with empty values
        return emptyCells == columns ? null : rowJsonObject;
    }


}
