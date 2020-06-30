package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class SheetWriter {

    private ArrayList<String> keys;
    private CellStyle cellDateStyle;

    public XSSFWorkbook addSheetToXlxs(XSSFWorkbook workbook, String sheetName, ArrayNode sheetArrayNode) {
        setCellDateStyle(ExcelProcessingUtils.getExcelDateCellStyle(workbook));
        if (sheetArrayNode.size() > 0) {
            Sheet sheet = workbook.createSheet(sheetName);
            sheet.createRow(0).createCell(0).setCellValue(sheetName);
            sheet.createRow(1); // blank row required - normally contains description
            createHeaderRow(sheet, sheetArrayNode);

            Iterator<JsonNode> iterator = sheetArrayNode.iterator();
            while (iterator.hasNext()) {
                JsonNode row = iterator.next();
                writeRowToSheet(sheet, row);
            }

        }

        return workbook;

    }

    /**
     * Create a row consisting of the column headers for the particular ccd
     * definition sheet/tab
     * 
     * @param sheet
     * @param sheetArrayNode
     */
    private void createHeaderRow(Sheet sheet, ArrayNode sheetArrayNode) {
        Iterator<String> headers = sheetArrayNode.get(0).fieldNames();
        ArrayList<String> keys = new ArrayList<>();

        int nextRow = sheet.getPhysicalNumberOfRows();
        Row row = sheet.createRow(nextRow);
        int columnIndex = 0;
        while (headers.hasNext()) {
            Cell cell = row.createCell(columnIndex);
            String headerName = headers.next();
            cell.setCellValue(headerName);
            keys.add(headerName);
            columnIndex++;
        }

        setKeys(keys);
    }

    /**
     * Generate a row on the excel sheet/tab from the json node
     * 
     * @param sheet
     * @param jsonNodeRow
     */
    private void writeRowToSheet(Sheet sheet, JsonNode jsonNodeRow) {
        int nextRow = sheet.getPhysicalNumberOfRows();
        Row row = sheet.createRow(nextRow);
        Iterator<JsonNode> jsonNodeCellIterator = jsonNodeRow.elements();
        int columnIndex = 0;
        while (jsonNodeCellIterator.hasNext()) {
            String column = getKeys().get(columnIndex);
            Cell cell = row.createCell(columnIndex);
            JsonNode jsonCellObject = jsonNodeCellIterator.next();

            switch (jsonCellObject.getNodeType()) {
            // todo if we change the read operations to read blank cells as null we will
            // need to handle the corresponding write filtering here
            case STRING:
                String value = jsonCellObject.asText();
                if ((column.equals("LiveFrom") || column.equals("LiveTo")) && value.length() > 0) {
                    try {
                        Date dt = new SimpleDateFormat("dd/MM/yy").parse(value);
                        cell.setCellValue(dt);
                        cell.setCellStyle(getCellDateStyle());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                } else if (value.length() > 0) {
                    cell.setCellValue(jsonCellObject.asText());
                }
                break;
            case NUMBER:
                cell.setCellValue(jsonCellObject.asInt());
                break;
            default:
                throw new RuntimeException("not sure what data type this json node is");
            }
            columnIndex++;
        }
    }

    private ArrayList<String> getKeys() {
        return keys;
    }

    private void setKeys(ArrayList<String> keys) {
        this.keys = keys;
    }

    public CellStyle getCellDateStyle() {
        return cellDateStyle;
    }

    public void setCellDateStyle(CellStyle cellDateStyle) {
        this.cellDateStyle = cellDateStyle;
    }

}
