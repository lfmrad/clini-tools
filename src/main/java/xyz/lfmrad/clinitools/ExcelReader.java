package xyz.lfmrad.clinitools;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import xyz.lfmrad.clinitools.model.Appointment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExcelReader {
    public void readSheet() {
        String targetFilePath = "test-resources/partB.xlsx";


        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(new File(targetFilePath)))) {
            
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            Map<Integer, String> headerMap = new HashMap<>(); 
            for (Cell cell : headerRow) {
                headerMap.put(cell.getColumnIndex(),cell.toString());
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Map<String, String> rowData = new HashMap<>();
                for (Cell cell : row) {
                    if (!isCellEmpty(cell)) {
                        rowData.put(headerMap.get(cell.getColumnIndex()),cell.toString());
                        
                    }
                }
                printData(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isCellEmpty(Cell cell) {
        if (cell == null) {
            return true;
        }
        
        if (cell.getCellType() == CellType.BLANK) {
            return true;
        }
        
        if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty()) {
            return true;
        }
        
        return false;
    }

    private void printData(Map<String, String> rowData) {
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

        if (rowData.containsKey("Nombre")) {
            sb.append("Name: ").append(rowData.get("Nombre")).append(", ");
        }
        
        if (rowData.containsKey("Concepto")) {
            sb.append("Treatment: ").append(rowData.get("Concepto")).append(", ");
        }

        // Remove the trailing comma and space
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        System.out.println(sb.toString());
    }
}
