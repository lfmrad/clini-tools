package xyz.lfmrad.clinitools.util;

import java.util.Map;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class DataWriter {
    private DataWriter() {
        throw new AssertionError("ExcelReader should not be instantiated.");
    }

    public static void printToConsole(List<Map<String, String>> data) { 
        int rowNumber = 1;
        for (Map<String, String> row : data) {
            System.out.println("ROW " + rowNumber  + " - DATA: " + row.toString());
            rowNumber++;
        }
    }

    public static String generateHTMLTable(List<Map<String, String>> data, List<String> headers) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html lang='en'>"
            + "<head>"
            + "    <meta charset='UTF-8'>"
            + "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>"
            + "    <title>Data</title>"
            + "    <link rel='stylesheet' href='table.css'>"
            + "</head>"
            + "<table>"
        );
        sb.append("<tr>");
        for (String header : headers) {
            sb.append("<th>").append(header).append("</th>");
        }
        sb.append("</tr>");
        for (Map<String, String> row : data) {
            sb.append("<tr>");
            for (String header : headers) {
                sb.append("<td>");
                if(row.containsKey(header)) {
                    sb.append(row.get(header));
                }
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>"
            + "</body>"
            + "</html>"
        );
        return sb.toString();
    }

    public static void writeToFile(String html, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(html);
            System.out.println("Created file:" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToExcel() {
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
}
