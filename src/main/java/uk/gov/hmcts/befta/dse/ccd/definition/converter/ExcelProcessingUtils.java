package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import org.apache.poi.ss.usermodel.*;

public class ExcelProcessingUtils {

    public static CellStyle getExcelDateCellStyle(Workbook wb){
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(
                createHelper.createDataFormat().getFormat("DD/mm/YYYY"));
        return cellStyle;
    }

    public static String getStringDateFromCell(Cell cell){
        String stringDateValue = new DataFormatter().formatCellValue(cell);
        return stringDateValue.replace("\"","");
    }

}
