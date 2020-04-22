package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class SheetWriter {

    private ArrayList<String> keys;
    private static CellStyle cellDateStyle;

    public XSSFWorkbook addSheetToXlxs(XSSFWorkbook workbook, String sheetName, ArrayNode sheetArrayNode) {
        cellDateStyle = ExcelDateUtils.getExcelDateCellStyle(workbook);
        if (sheetArrayNode.size() > 0){
            Sheet sheet = workbook.createSheet(sheetName);
            sheet.createRow(0).createCell(0).setCellValue(sheetName);
            sheet.createRow(1); //blank row required - normally contains description
            createHeaderRow(sheet, sheetArrayNode);

            Iterator iterator = sheetArrayNode.iterator();
            while (iterator.hasNext()) {
                JsonNode row = (JsonNode) iterator.next();
                writeRowToSheet(sheet, row);
            }

        }

        return workbook;

    }

    /**
     * Create a row consisting of the column headers for the particular ccd definition sheet/tab
     * @param sheet
     * @param sheetArrayNode
     */
    private void createHeaderRow(Sheet sheet, ArrayNode sheetArrayNode) {
        Iterator<String> headers = sheetArrayNode.get(0).fieldNames();
        ArrayList<String> keys = new ArrayList<>();

        int nextRow = sheet.getPhysicalNumberOfRows();
        Row row = sheet.createRow(nextRow);
        int columnIndex = 0;
        while (headers.hasNext()){
            Cell cell = row.createCell(columnIndex);
            String headerName = headers.next();
            cell.setCellValue(headerName);
            keys.add(headerName);
            columnIndex++;
        }

        this.keys = keys;

    }

    /**
     * Generate a row on the excel sheet/tab from the json node
     * @param sheet
     * @param jsonNodeRow
     */
    private void writeRowToSheet(Sheet sheet, JsonNode jsonNodeRow){
        int nextRow = sheet.getPhysicalNumberOfRows();
        Row row = sheet.createRow(nextRow);
        Iterator jsonNodeCellIterator = jsonNodeRow.elements();
        int columnIndex = 0;
        while (jsonNodeCellIterator.hasNext()){
            String column = keys.get(columnIndex);
            Cell cell = row.createCell(columnIndex);
            JsonNode jsonCellObject = (JsonNode) jsonNodeCellIterator.next();

            switch (jsonCellObject.getNodeType()){
                //todo if we change the read operations to read blank cells as null we will need to handle the corresponding write filtering here
                case STRING:
                    String value = jsonCellObject.asText();
                    if ( (column.equals("LiveFrom") || column.equals("LiveTo")) && value.length() > 0) {
                        Date dt = new Date(value);
                        cell.setCellValue(dt);
                        cell.setCellStyle(cellDateStyle);
                    } else if (value.length() > 0){
                        cell.setCellValue(jsonCellObject.asText());
                    }
                    break;
                case NUMBER: cell.setCellValue(jsonCellObject.asInt());
                    break;
                default: throw new RuntimeException("not sure what data type this json node is");
            }
            columnIndex++;
        }
    }

}
