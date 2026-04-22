/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author korneleehenry
 *
 */
class SheetReaderTest {

    public static final String DEFAULT_DEFINITIONS_PATH_EXCEL = "src/test/resources/uk/gov/hmcts/befta/dse/ccd/definitions/excel/CCD_CNP_27.xlsx";
    private static final Map<String, Map<String, String>> SHEET_EXPECTATIONS = Map.of(
            "Jurisdiction", Map.of("ID", "AUTOTEST1"),
            "CaseType", Map.of("ID", "AAT"),
            "State", Map.of("ID", "TODO"),
            "CaseEvent", Map.of("ID", "CREATE"),
            "UserProfile", Map.of("UserIDAMId", "auto.test.cnp@gmail.com"));

	/**
	 * Test method for {@link uk.gov.hmcts.befta.dse.ccd.definition.converter.SheetReader#transformToJson(org.apache.poi.ss.usermodel.Sheet)}.
	 * @throws IOException 
	 * @throws EncryptedDocumentException 
	 */
	@Test
	void testTransformToJson() throws EncryptedDocumentException, IOException {
	       Workbook workbook = WorkbookFactory.create(new File(DEFAULT_DEFINITIONS_PATH_EXCEL));
	        Sheet sheet = workbook.getSheetAt(0);
	        int totalRows = sheet.getPhysicalNumberOfRows();
	        for (int i =4; i < totalRows; i++) {
	        	Row row = sheet.getRow(i);
		        sheet.removeRow(row);
	        }
	        SheetReader sheetTransformer = new SheetReader();
	        ArrayNode arrayNode = sheetTransformer.transformToJson(sheet);
	        assertNotNull(arrayNode);
	        assertTrue(arrayNode.size()==1);
	}

	@Test
	void testTransformSheetsToJson() throws EncryptedDocumentException, IOException {
		Workbook workbook = WorkbookFactory.create(new File(DEFAULT_DEFINITIONS_PATH_EXCEL));

		for (int i = 0; i < workbook.getNumberOfSheets(); i++)
		{
			Sheet sheet = workbook.getSheetAt(i);
			int totalRows = sheet.getPhysicalNumberOfRows();
			for (int j =4; j < totalRows; j++) {
				Row row = sheet.getRow(j);
				sheet.removeRow(row);
			}
			SheetReader sheetTransformer = new SheetReader();
			ArrayNode arrayNode = sheetTransformer.transformToJson(sheet);
			assertNotNull(arrayNode);
			assertTrue(arrayNode.size()==1);

            if (SHEET_EXPECTATIONS.containsKey(sheet.getSheetName())) {
                Map<String, String> expectations = SHEET_EXPECTATIONS.get(sheet.getSheetName());
                expectations.forEach((field, expectedValue) -> assertEquals(
                        expectedValue,
                        arrayNode.get(0).get(field).asText(),
                        "Unexpected value for " + sheet.getSheetName() + "." + field));
            } else {
                assertTrue(
                        arrayNode.get(0).fieldNames().hasNext(),
                        "Expected " + sheet.getSheetName() + " to contain at least one field");
            }
		}
	}
}
