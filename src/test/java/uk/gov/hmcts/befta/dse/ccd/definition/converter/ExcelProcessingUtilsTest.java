package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExcelProcessingUtilsTest {

    @Test
    void testGetExcelDateCellStyle() {
        // Arrange
        Workbook workbook = new XSSFWorkbook();
        String expectedDateFormat = ExcelProcessingUtils.CCD_DATE_FORMAT;

        // Act
        CellStyle cellStyle = ExcelProcessingUtils.getExcelDateCellStyle(workbook);
        short actualDataFormat = cellStyle.getDataFormat();
        String actualDateFormat = workbook.getCreationHelper().createDataFormat().getFormat(actualDataFormat);

        // Assert
        assertEquals(expectedDateFormat, actualDateFormat, "The date format should match the expected format.");
    }

    @Test
    void testGetExcelStringDateCellStyle() {
        // Arrange
        Workbook workbook = new XSSFWorkbook();
        String expectedDateFormat = ExcelProcessingUtils.CCD_DATE_FORMAT;
        java.text.Format format = new java.text.SimpleDateFormat(expectedDateFormat);
        DataFormatter dataFormatter = new DataFormatter();
        dataFormatter.addFormat(expectedDateFormat, format);

        CellStyle cellStyle = ExcelProcessingUtils.getExcelDateCellStyle(workbook);
        Cell cell = workbook.createSheet().createRow(0).createCell(0);
        cell.setCellFormula("\"2023/1/1\"");

        // Act
        String dateStr = ExcelProcessingUtils.getStringDateFromCell(cell);
        short actualDataFormat = cellStyle.getDataFormat();
        String actualDateFormat = workbook.getCreationHelper().createDataFormat().getFormat(actualDataFormat);

        // Assert
        assertEquals(expectedDateFormat, actualDateFormat, "The date format should match the expected format.");
    }

    @Test
    void testGetStringDateFromCellUsesCanonicalFormat() {
        Workbook workbook = new XSSFWorkbook();
        CellStyle cellStyle = ExcelProcessingUtils.getExcelDateCellStyle(workbook);
        Cell cell = workbook.createSheet().createRow(0).createCell(0);
        cell.setCellValue(new java.util.Date(1483228800000L));
        cell.setCellStyle(cellStyle);

        String dateStr = ExcelProcessingUtils.getStringDateFromCell(cell);

        assertEquals("01/01/2017", dateStr);
    }
}
