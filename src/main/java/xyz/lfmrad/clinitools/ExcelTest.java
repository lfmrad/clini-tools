package xyz.lfmrad.clinitools;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTest {
    public void test() {
        String fileName = "testExcelFile.xlsx";

        // Create a new workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestSheet");

        // Create a row and write some data
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Hello, Apache POI!");

        // Write the workbook to a file
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read data back from the workbook
        Sheet readSheet = workbook.getSheetAt(0);
        Row readRow = readSheet.getRow(0);
        Cell readCell = readRow.getCell(0);
        System.out.println("Data from Excel: " + readCell.getStringCellValue());

        // Close the workbook
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
