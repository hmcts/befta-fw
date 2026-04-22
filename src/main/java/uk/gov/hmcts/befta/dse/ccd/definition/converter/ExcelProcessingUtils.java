package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelProcessingUtils {

    public static final String CCD_DATE_FORMAT = "dd/MM/yyyy";

    private ExcelProcessingUtils() {
    }

    public static CellStyle getExcelDateCellStyle(Workbook wb) {
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(CCD_DATE_FORMAT));
        return cellStyle;
    }

    public static String getStringDateFromCell(Cell cell) {
        String stringDateValue = new DataFormatter().formatCellValue(cell);
        return stringDateValue.replace("\"", "");
    }

}
