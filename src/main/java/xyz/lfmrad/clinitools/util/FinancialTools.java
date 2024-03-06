package xyz.lfmrad.clinitools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import xyz.lfmrad.clinitools.Configuration;
import xyz.lfmrad.clinitools.model.*;

public final class FinancialTools {
    private FinancialTools() {
        throw new AssertionError("FinancialTools should not be instantiated.");
    }

    public static void generateSettlement(List<Appointment> appointments, boolean includeUnpaid) {
        String templateFilename = Configuration.getFilesAndPaths().get("settlementTemplateFilename");
        String templateFilepath = Configuration.getXlsxFilePath(templateFilename);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(templateFilepath)))) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowIndex = Configuration.getExcelIndex("templateHeaderRows");
            int createdColumns = 0;
            
            Appointment refereceAppointmentForDate = appointments.get(0);
            String settlementDate = DateTools.getDateAsFormattedString(refereceAppointmentForDate.getAppointmentTimeData(), false);
            String dateFieldTitle = DateTools.getDateAsLocalizedVerboseString(refereceAppointmentForDate.getAppointmentTimeData(), true);
            if (Configuration.isDebugEnabled()) { 
                System.out.println("EXTRACTED FORMATTED DATES: " + "settlement: " + settlementDate + " / dateField: " + dateFieldTitle);
            }
            Row dateTitleFieldRow = sheet.getRow(Configuration.getExcelIndex("dateFieldExcelIndex") - 1);
            Cell dateTitleCell = dateTitleFieldRow.getCell(0);
            dateTitleCell.setCellValue(dateFieldTitle);

            for (Appointment appointment : appointments) {
                Row row = sheet.createRow(rowIndex);
               
                Cell dateCell = row.createCell(0); 
                dateCell.setCellValue(DateTools.convertToFormattedDateTimeString(appointment.getAppointmentTimeData()));
                Cell nameCell = row.createCell(1); 
                nameCell.setCellValue(appointment.getClientName());

                // if there are multiple activities, additional rows are created
                int additionalRowsCreated = 0;
                double totalPrice = 0;
                double totalCost = 0;
                for (Activity activity : appointment.getActivities()) {
                    if (!includeUnpaid && !activity.isPaidFor()) {
                        continue;
                    }
                    Row activityRow = sheet.getRow(rowIndex) == null ? sheet.createRow(rowIndex) : sheet.getRow(rowIndex);

                    Cell activityNameCell = activityRow.createCell(2);
                    activityNameCell.setCellValue(activity.getName());
                    Cell activityPriceCell = activityRow.createCell(3);
                    activityPriceCell.setCellValue(activity.getPrice());
                    Cell activityNetPriceCell = activityRow.createCell(4);
                    activityNetPriceCell.setCellValue(activity.getNetPrice());

                    Cell activityPaymentStatus = activityRow.createCell(5);
                    activityPaymentStatus.setCellValue(activity.getPaymentStatus());
                    Cell activityCostWithTaxCell = activityRow.createCell(6);
                    activityCostWithTaxCell.setCellValue(-activity.getCostWithTax());

                    // TEMPORAL SOL.
                    if (activity.getCostWithTax() > 0) {
                        Cell activityIVACell = activityRow.createCell(7);
                        activityIVACell.setCellValue(activity.getTaxValue());    
                        Cell activityNetCostCell = activityRow.createCell(8);
                        activityNetCostCell.setCellValue(-activity.getNetCost());    
                        // TEMP.
                        // Cell activityNetCostCopyCell = activityRow.createCell(21);
                        // activityNetCostCopyCell.setCellValue(-activity.getNetCost());       
                    }

                    Cell activityCostWithTaxThirdPartyCell = activityRow.createCell(9);
                    activityCostWithTaxThirdPartyCell.setCellValue(-activity.getCostWithTaxThirdParty());
                    
                    // TEMPORAL SOL.
                    if (activity.getCostWithTaxThirdParty() > 0) {
                        Cell activityThirdPartyIVACell = activityRow.createCell(10);
                        activityThirdPartyIVACell.setCellValue(activity.getTaxValue());    
                        Cell activityThirdPartyNetCostCell = activityRow.createCell(11);
                        activityThirdPartyNetCostCell.setCellValue(-activity.getNetCostThirdParty());   
                    }
                     
                    rowIndex++;
                    additionalRowsCreated++;
                    totalPrice += activity.getNetPrice();
                    totalCost += -(activity.getNetCost() + activity.getNetCostThirdParty());
                }
            
                // comes back to the initial row after adding several activities 
                int correctedIndex = rowIndex - additionalRowsCreated;
                row = sheet.getRow(correctedIndex); 

                Cell cashAmountCell = row.createCell(12);
                cashAmountCell.setCellValue(appointment.getCashTotal());
                Cell cardAmountCell = row.createCell(13);
                cardAmountCell.setCellValue(appointment.getCardTotal());
                Cell bizumAmountCell = row.createCell(14);
                bizumAmountCell.setCellValue(appointment.getBizumTotal());
                Cell financingAmountCell = row.createCell(15);
                financingAmountCell.setCellValue(appointment.getFinancingTotal());
                Cell netProfit = row.createCell(16);
                netProfit.setCellValue(totalPrice + totalCost);

                
                Cell notesCell = row.createCell(17);
                notesCell.setCellValue(appointment.getNotes());

                // TEMPORAL FUNCTIONALITY
                // Implemented through Excel formulas. Pending actual implementation.
                addTemporaryExcelFormulas(row, correctedIndex + 1, appointment, includeUnpaid); 

                createdColumns = row.getLastCellNum() + 1;
            
                int lastRowIndex = rowIndex - 1;  // adjusts for the last row written in this iteration
                Row lastRowWritten = sheet.getRow(lastRowIndex);
                addClientSeparatorLine(lastRowWritten, workbook, createdColumns);
            }
            Row dummyRow = sheet.createRow(rowIndex);
            addEndOfDataFooter(dummyRow, workbook, createdColumns);
            writeToFile(workbook, settlementDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTemporaryExcelFormulas(Row row, int excelIndex, Appointment appointment, boolean includeUnpaid) {
        Cell invCell = row.createCell(18);
        invCell.setCellValue(appointment.getTotalNetPVP(includeUnpaid));
        Cell profitCell = row.createCell(19);
        profitCell.setCellValue(appointment.getNetProfit(includeUnpaid));
        Cell flagCell = row.createCell(20);
        if ((appointment.getCardTotal() + appointment.getBizumTotal() + appointment.getFinancingTotal()) > 0) {
            flagCell.setCellValue("x");
        }
    }

    private static void writeToFile(Workbook workbook, String settlementDate) throws FileNotFoundException, IOException {
        String filename = Configuration.getFilesAndPaths().get("settlementFilename") + "_" + settlementDate;
        try (FileOutputStream fileOut = new FileOutputStream(Configuration.getXlsxFilePath(filename))) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.evaluateAll();
            workbook.write(fileOut);
        }
    }

    private static String getDateFromAppointment(Appointment appointment) {
        return DateTools.getDateAsFormattedString(appointment.getAppointmentTimeData(), false);
    }


    public static void addClientSeparatorLine(Row row, Workbook workbook, int columnLength) {
        for (int i = 0; i < columnLength; i++) { 
            Cell cell = row.getCell(i);
            if (cell == null) {
                cell = row.createCell(i);
            }
            CellStyle currentCellStyle = cell.getCellStyle(); 
            CellStyle modifiedCellStyle = workbook.createCellStyle();
            modifiedCellStyle.cloneStyleFrom(currentCellStyle); // copies existing style so it doesn't lost the template's format
            modifiedCellStyle.setBorderBottom(BorderStyle.MEDIUM);
            cell.setCellStyle(modifiedCellStyle);
        }
    }

    public static void addEndOfDataFooter(Row row, Workbook workbook, int columnLength) {
        for (int i = 0; i < columnLength; i++) { 
            Cell cell = row.createCell(i);
            CellStyle currentCellStyle = cell.getCellStyle();
            CellStyle modifiedCellStyle = workbook.createCellStyle();
            modifiedCellStyle.cloneStyleFrom(currentCellStyle);
            modifiedCellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            modifiedCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(modifiedCellStyle);
        }
    }

    public static String parseEmptySymbolIfZero(double valueToParse) {
        if (valueToParse == 0) {
            return Configuration.getOtherText().get("zeroSymbol");
        } else {
            return Double.toString(valueToParse);
        }
    }

    public static void printSummary(List<Appointment> appointments, boolean includeUnpaid) {
        double totalPVP = 0.0;
        double totalCost = 0.0;
        double totalPaid = 0.0;
        double totalCashPayments = 0.0;
        double totalCardPayments = 0.0;
        double totalBizumPayments = 0.0;
        double totalFinancing = 0.0;
        double parityCheck = 0.0;
        int numberOfClients = 0;
        
        for (Appointment appointment : appointments) {
            if (appointment.hasPayments()) {
                numberOfClients++;
            }
            totalPVP += appointment.getTotalPVP(includeUnpaid);
            totalCost += appointment.getTotalCost(includeUnpaid);
            totalPaid += appointment.getTotalPaid();
            totalCashPayments += appointment.getCashTotal();
            totalCardPayments += appointment.getCardTotal();
            totalBizumPayments += appointment.getBizumTotal();
            totalFinancing += appointment.getFinancingTotal();
        }

        parityCheck = totalPVP - totalPaid;
        
        System.out.println("\n\n" + Configuration.getOtherText().get("summaryHeading"));
        System.out.println(Configuration.getOtherText().get("numberOfClients") + numberOfClients);
        System.out.println(Configuration.getOtherText().get("totalPVP") + totalPVP);
        System.out.println(Configuration.getOtherText().get("totalCost") + totalCost);
        System.out.println(Configuration.getOtherText().get("totalPaid") + totalPaid);
        System.out.println();
        System.out.println("\n" + Configuration.getOtherText().get("parityCheck") + parityCheck);
        if (parityCheck == 0) {
            System.out.println(Configuration.getOtherText().get("parityCheckSuccess"));
        } else {
            System.out.println(Configuration.getOtherText().get("parityCheckFail"));
        }
        System.out.println();
    
        System.out.println(Configuration.getOtherText().get("totalCashPayments") + totalCashPayments);
        System.out.println(Configuration.getOtherText().get("totalCardPayments") + totalCardPayments);
        System.out.println(Configuration.getOtherText().get("totalBizumPayments") + totalBizumPayments);
        System.out.println(Configuration.getOtherText().get("totalFinancingInstallments") + totalFinancing);
    }
}
