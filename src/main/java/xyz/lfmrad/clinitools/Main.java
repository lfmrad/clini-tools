package xyz.lfmrad.clinitools;

import java.util.List;
import java.util.Map;

import xyz.lfmrad.clinitools.model.*;
import xyz.lfmrad.clinitools.util.*;

public class Main {
    private static List<Map<String, String>> appointments, payments;

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args.length > 1 && Configuration.getLaunchCommands().get("debug").equals(args[1])) {
                Configuration.debugInfo(true);
            }
            if (Configuration.getLaunchCommands().get("settlement").equals(args[0])) {
                Configuration.setMode(Configuration.OperationMode.SETTLEMENT);
                readData();
                dataAnalysis(false);
            } else if (Configuration.getLaunchCommands().get("expandedSettlement").equals(args[0])) {
                Configuration.setMode(Configuration.OperationMode.EXPANDED_SETTLEMENT);
                readData();
                dataAnalysis(true);
            }
        } else {
            Configuration.debugInfo(true);
            Configuration.setMode(Configuration.OperationMode.DEVELOPMENT);
            readData();
            dataAnalysis(false);
        }
    }

    private static void readData() {
        String historyFilename = Configuration.getFilesAndPaths().get("historyFilename");
        String paymentsFilename = Configuration.getFilesAndPaths().get("paymentsFilename");
        int numberOfHeaderRows = Configuration.getExcelIndex("dataHeaderRows");
        appointments = ExcelTools.readXlxsSheet(Configuration.getXlsxFilePath(historyFilename), numberOfHeaderRows);
        payments = ExcelTools.readXlxsSheet(Configuration.getXlsxFilePath(paymentsFilename), numberOfHeaderRows);
        if (Configuration.isDebugEnabled()) {
            DataWriter.printToConsole(appointments);
            DataWriter.printToConsole(payments);
            generateValidationFiles();
        }
    }

    public static void dataAnalysis(boolean includeUnpaid) {
        List<Appointment> processedAppointments = DataBuilder.buildAppointmentsList(payments, appointments);
        FinancialTools.printSummary(processedAppointments, includeUnpaid);
        FinancialTools.generateSettlement(processedAppointments, includeUnpaid);
    }

    private static void generateValidationFiles() {
        Map<String, String> appointmentsHeaders = Configuration.getAppointmentsHeaders();
        List<String> selectedAppointmentsHeaders = List.of(
            appointmentsHeaders.get("date"), 
            appointmentsHeaders.get("startHour"),
            appointmentsHeaders.get("name"),
            appointmentsHeaders.get("activities"),
            appointmentsHeaders.get("price"),
            appointmentsHeaders.get("costWithTax"),
            appointmentsHeaders.get("costWithTaxThirdParty"),
            appointmentsHeaders.get("notes")
        );
        String htmlTable = DataWriter.generateHTMLTable(appointments, selectedAppointmentsHeaders);
        String filename = Configuration.getFilesAndPaths().get("historyFilename");
        DataWriter.writeToFile(htmlTable, Configuration.getHtmlFilePath(filename));
        Map<String, String> paymentHeaders = Configuration.getPaymentHeaders();
        List<String> selectedPaymentHeaders = List.of(
            paymentHeaders.get("date"), 
            paymentHeaders.get("name"),
            paymentHeaders.get("activities"),
            paymentHeaders.get("paymentMethod"),
            paymentHeaders.get("paidAmount"),
            paymentHeaders.get("paymentStatus")
        );
        String htmlTable2 = DataWriter.generateHTMLTable(payments, selectedPaymentHeaders);
        filename = Configuration.getFilesAndPaths().get("paymentsFilename");
        DataWriter.writeToFile(htmlTable2, Configuration.getHtmlFilePath(filename));
    }
}
