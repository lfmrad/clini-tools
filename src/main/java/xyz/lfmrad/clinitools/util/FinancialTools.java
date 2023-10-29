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
            int rowIndex = Configuration.getNumberOfHeaders("templateHeaderRows");
            int createdColumns = 0;
            String settlementDate = getDateFromAppointment(appointments.get(0));

            for (Appointment appointment : appointments) {
                if(!includeUnpaid && isPaymentPending(appointment)) {
                    continue;
                }
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
                    Row activityRow = sheet.getRow(rowIndex) == null ? sheet.createRow(rowIndex) : sheet.getRow(rowIndex);
                    
                    Cell activityNameCell = activityRow.createCell(2);
                    activityNameCell.setCellValue(activity.getName());
                    Cell activityPriceCell = activityRow.createCell(3);
                    activityPriceCell.setCellValue(activity.getPrice());
                    Cell activtyCostWithTaxCell = activityRow.createCell(4);
                    activtyCostWithTaxCell.setCellValue(-activity.getCostWithTax());
                    Cell activityCostWithTaxThirdPartyCell = activityRow.createCell(5);
                    activityCostWithTaxThirdPartyCell.setCellValue(-activity.getCostWithTaxThirdParty());
            
                    rowIndex++;
                    additionalRowsCreated++;
                    totalPrice += activity.getPrice();
                    totalCost += -(activity.getCostWithTax() + activity.getCostWithTaxThirdParty());
                }
            
                double cashTotal = 0;
                double cardTotal = 0;
                double bizumTotal = 0;
                for (Payment payment : appointment.getPayments()) {
                    if (Configuration.getOtherText().get("cashPayment").equals(payment.getPaymentMethod())) {
                        cashTotal += payment.getAmount();
                    } else if (Configuration.getOtherText().get("cardPayment").equals(payment.getPaymentMethod())) {
                        cardTotal += payment.getAmount();
                    } else if (Configuration.getOtherText().get("bizumPayment").equals(payment.getPaymentMethod())) {
                        bizumTotal += payment.getAmount();
                    }
                }
            
                // comes back to the initial row after adding several activities 
                int correctedIndex = rowIndex - additionalRowsCreated;
                row = sheet.getRow(correctedIndex); 

                Cell cashAmountCell = row.createCell(6);
                cashAmountCell.setCellValue(cashTotal);
                Cell cardAmountCell = row.createCell(7);
                cardAmountCell.setCellValue(cardTotal);
                Cell bizumAmountCell = row.createCell(8);
                bizumAmountCell.setCellValue(bizumTotal);
                Cell netProfit = row.createCell(9);
                netProfit.setCellValue(totalPrice + totalCost);
                Cell paymentStatusCell = row.createCell(10);
                paymentStatusCell.setCellValue(appointment.getPaymentStatus());
                Cell notesCell = row.createCell(11);
                notesCell.setCellValue(appointment.getNotes());

                createdColumns = row.getLastCellNum();

                // TEMPORAL FUNCTIONALITY
                // Implemented through Excel formulas. Pending actual implementation.
                addTemporaryExcelFormulas(row, correctedIndex + 1, cardTotal + bizumTotal); 
            
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

    private static void addTemporaryExcelFormulas(Row row, int excelIndex, double cardBizumTotal) {
        Cell formulaCell1 = row.createCell(12);
        formulaCell1.setCellFormula("IF(O" + (excelIndex) + "=\"x\",D" + (excelIndex) + ",\"-\")");
        Cell formulaCell2 = row.createCell(13);
        formulaCell2.setCellFormula("IF(O" + (excelIndex) + "=\"x\",J" + (excelIndex) + ",\"-\")");
        Cell formulaCell3 = row.createCell(14);
        if (cardBizumTotal > 0) {
            formulaCell3.setCellValue("x");
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
        return DateTools.getDateAsFormattedString(appointment.getAppointmentTimeData());
    }

    private static boolean isPaymentPending(Appointment appointment) {
        return appointment.getPaymentStatus().equals(Configuration.getOtherText().get("pendingPayment"));
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
        double totalCostWithTax = 0.0;
        double totalCostWithTaxThirdParty = 0.0;
        double totalCashPayments = 0.0;
        double totalCardPayments = 0.0;
        double totalBizumPayments = 0.0;
        int clientsThatHaveNotPaid = 0;

        for (Appointment appointment : appointments) {
            if(!includeUnpaid && isPaymentPending(appointment)) {
                    clientsThatHaveNotPaid++;
                    continue;
            }
            for (Activity activity : appointment.getActivities()) {
                totalPVP += activity.getPrice();
                totalCostWithTax += activity.getCostWithTax();
                totalCostWithTaxThirdParty += activity.getCostWithTaxThirdParty();
            }
            for (Payment payment : appointment.getPayments()) {
                if (Configuration.getOtherText().get("cashPayment").equals(payment.getPaymentMethod())) {
                    totalCashPayments += payment.getAmount();
                } else if (Configuration.getOtherText().get("cardPayment").equals(payment.getPaymentMethod())) {
                    totalCardPayments += payment.getAmount();
                } else if (Configuration.getOtherText().get("bizumPayment").equals(payment.getPaymentMethod())) {
                    totalBizumPayments += payment.getAmount();
                }
            }
        }
        System.out.println(Configuration.getOtherText().get("summaryHeading"));
        System.out.println(Configuration.getOtherText().get("numberOfClients") + (appointments.size() - clientsThatHaveNotPaid));
        System.out.println(Configuration.getOtherText().get("totalPVP") + totalPVP);
        System.out.println(Configuration.getOtherText().get("totalCostWithTax") + totalCostWithTax);
        System.out.println(Configuration.getOtherText().get("totalCostWithTaxThirdParty") + totalCostWithTaxThirdParty);
        System.out.println(Configuration.getOtherText().get("totalCashPayments") + totalCashPayments);
        System.out.println(Configuration.getOtherText().get("totalCardPayments") + totalCardPayments);
        System.out.println(Configuration.getOtherText().get("totalBizumPayments") + totalBizumPayments);
    }
}
