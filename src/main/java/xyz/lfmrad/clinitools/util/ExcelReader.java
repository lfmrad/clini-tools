package xyz.lfmrad.clinitools.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import xyz.lfmrad.clinitools.common.AppConstants;
import xyz.lfmrad.clinitools.model.Appointment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {
    public static List<Map<String, String>> readSheet(String filePath) {
        
        List<Map<String, String>> allRowsData = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(filePath)))) {
            int headerRowIndex = 3;
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(headerRowIndex);
            Map<Integer, String> headerMap = new HashMap<>(); 
            for (Cell cell : headerRow) {
                headerMap.put(cell.getColumnIndex(),cell.toString());
            }

            for (int rowIndex = headerRowIndex+1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Map<String, String> rowData = new HashMap<>();
                for (Cell cell : row) {
                    if (!isCellEmpty(cell)) {
                        rowData.put(headerMap.get(cell.getColumnIndex()),cell.toString());
                    }
                }
                // System.out.println("** ROW " + (rowIndex+1) + " - MAP: " + rowData.toString()); // TEST
                allRowsData.add(rowData);
            }
            return allRowsData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isCellEmpty(Cell cell) {
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
}
