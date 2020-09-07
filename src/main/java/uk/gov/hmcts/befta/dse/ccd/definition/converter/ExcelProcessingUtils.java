package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelProcessingUtils {

    private ExcelProcessingUtils() {
    }

    public static CellStyle getExcelDateCellStyle(Workbook wb) {
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("DD/mm/YYYY"));
        return cellStyle;
    }

    public static String getStringDateFromCell(Cell cell) {
        String stringDateValue = new DataFormatter().formatCellValue(cell);
        return stringDateValue.replace("\"", "");
    }

}
