package uk.gov.hmcts.befta.dse.ccd.definition.converter;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExcelReader {

    private String filePath;

    public ExcelReader(String filePath) {
        this.filePath = filePath;
    }


    /**
     * Read Excel file and return a Workbook object
     */
    public Workbook parseXLXS(){
        FileInputStream fInputStream = null;
        Workbook excelWookBook = null;

        try {
            fInputStream = new FileInputStream(filePath.trim());

            /* Create the workbook object. */
            excelWookBook = WorkbookFactory.create(fInputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException ignored){

        }


        return excelWookBook;

    }

}
