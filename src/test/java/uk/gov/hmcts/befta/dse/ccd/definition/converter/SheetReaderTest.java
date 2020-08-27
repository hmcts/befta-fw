/**
 * 
 */
package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author korneleehenry
 *
 */
class SheetReaderTest {
	public static final String DEFAULT_DEFINITIONS_PATH_EXCEL = "src/main/resources/uk/gov/hmcts/befta/dse/ccd/definitions/excel/CCD_CNP_27.xlsx";

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
	        for (int i =3; i < totalRows; i++) {
	        	Row row = sheet.getRow(i);
		        sheet.removeRow(row);
	        }
	        SheetReader sheetTransformer = new SheetReader();
	        ArrayNode arrayNode = sheetTransformer.transformToJson(sheet);
	        assertNotNull(arrayNode);
	        assertTrue(arrayNode.size()==1);
	}

}
