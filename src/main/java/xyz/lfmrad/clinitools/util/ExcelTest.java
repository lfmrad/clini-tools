package xyz.lfmrad.clinitools.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List; 
import java.util.Map;

public class ExcelTest {
        public static void testDataPrinter (List<Map<String, String>> data) { 
            int rowNumber = 1;
            for (Map<String, String> row : data) {
                System.out.println("ROW " + rowNumber  + " - DATA: " + row.toString());
                rowNumber++;
            }
        }

        public static void testRowPrinter(Map<String, String> rowData) {
        /*
        * Using StringBuilder for efficient string concatenation in loops.
        * Strings are immutable; each concatenation creates a new object.
        * StringBuilder is mutable, so it's more memory-efficient for repetitive string operations.
        */
        StringBuilder sb = new StringBuilder();

        // print all
        // for (Map.Entry<String, String> entry : rowData.entrySet()) {
        //     sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        // }

        // if (rowData.containsKey("Nombre")) {
        //     sb.append("Name: ").append(rowData.get("Nombre")).append(", ");
        // }
        
        // if (rowData.containsKey("Concepto")) {
        //     sb.append("Treatment: ").append(rowData.get("Concepto")).append(", ");
        // }

        // Remove the trailing comma and space
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        System.out.println(sb.toString());
    }


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
