package xyz.lfmrad.clinitools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import xyz.lfmrad.clinitools.Configuration;

public final class ExcelTools {
    private ExcelTools() {
        throw new AssertionError("ExcelReader should not be instantiated.");
    }

    public static List<Map<String, String>> readXlxsSheet(String filePath, int numberOfHeaderRows) {
        List<Map<String, String>> allRowsData = new ArrayList<>();

        if (Configuration.isDebugEnabled()) {
            System.out.println("FILEPATH FOR READING: " + filePath);
            File file = new File(filePath);
            System.out.println("File exists: " + file.exists());
            System.out.println("Absolute path: " + file.getAbsolutePath());
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)))) {
            int headerRowIndex = numberOfHeaderRows - 1;
            Sheet sheet = workbook.getSheetAt(0);
            boolean is1904DateSystem = workbook.isDate1904();
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
                        String dataToParse;
                        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                            dataToParse = DateTools.formatDateTime(cell, is1904DateSystem);
                        } else {
                            dataToParse = cell.toString();
                        }
                        rowData.put(headerMap.get(cell.getColumnIndex()), dataToParse);
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

    public static int getFirstEmptyRow(Sheet sheet) {
        for (Row row : sheet) {
            boolean allEmpty = true;
            for (Cell cell : row) {
                if (!isCellEmpty(cell)) {
                    allEmpty = false;
                }
            }
            if (allEmpty) {
                return row.getRowNum();
            }
        }
    return -1; // Returns -1 if no empty rows found
    }
}

