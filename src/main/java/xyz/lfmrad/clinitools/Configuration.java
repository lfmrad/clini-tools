package xyz.lfmrad.clinitools;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.text.Normalizer;

public class Configuration {
    // Static members holding configuration data
    private static Map<String, String> customDateTimePatterns;
    private static Map<String, String> appointmentsHeaders;
    private static Map<String, String> paymentHeaders;
    private static Map<String, String> launchCommands;
    private static Map<String, String> filesAndPaths;
    private static Map<String, String> otherText;
    private static String workingDir;
    public enum OperationMode {
        SETTLEMENT,
        EXPANDED_SETTLEMENT,
        DEVELOPMENT
    }
    private static OperationMode opMode = OperationMode.DEVELOPMENT;
    private static boolean showDebugInfo = false;

    private Configuration() {
        throw new AssertionError("Configuration should not be instantiated.");
    }

    static {
        try (InputStream is = Configuration.class.getResourceAsStream("/config.json")) {
            ObjectMapper mapper = new ObjectMapper();
            ConfigStructure config = mapper.readValue(is, ConfigStructure.class);
            customDateTimePatterns = config.getCustomDateTimePatterns();
            appointmentsHeaders = config.getAppointmentsHeaders();
            paymentHeaders = config.getPaymentHeaders();
            launchCommands = config.getLaunchCommands();
            filesAndPaths = config.getFilesAndPaths();
            otherText = config.getOtherText();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static void setMode(OperationMode opMode) {
        Configuration.opMode = opMode;
        if (opMode == OperationMode.DEVELOPMENT) {
            workingDir = filesAndPaths.get("testFolder");
        } else {
            workingDir = filesAndPaths.get("deploymentFolder");
        }
        if (showDebugInfo) {
            showStatus();
        }
    }

     public static OperationMode getOperationMode() {
        return opMode;
    }

    public static boolean isDebugEnabled() {
        return showDebugInfo;
    }
    
    public static void debugInfo(boolean showDebugInfo) {
        Configuration.showDebugInfo = showDebugInfo;
    }

    public static void setWorkingDir(String workingDir) {
        Configuration.workingDir = workingDir;
    }

    public static void showStatus() {
        System.out.println("OpMode: " + opMode);
        System.out.println("Working Dir: " + getXlsxFilePath("testFile"));
    }

    public static String getWorkingDir() {
        return workingDir;
    }

    public static String getXlsxFilePath(String filename) {
        return workingDir + filename + ".xlsx";
    }

    public static String getHtmlFilePath(String filename) {
        return workingDir + filename + ".html";
    }

    public static int getNumberOfHeaders(String key) {
        return Integer.parseInt(filesAndPaths.get(key));
    }

    // STRING TOOLS; move to independent class?
    public static String removeDiacriticalMarks(String string) {
        // Normalize the string to decomposed form, where diacritical marks are separated from characters
        String normalized = Normalizer.normalize(string, Normalizer.Form.NFD);
        // Remove diacritical marks using a regex that matches all non-ASCII characters
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    public static boolean compareStringsIgnoringGrammar(String str1, String str2) {
        // Remove diacritics and compare strings ignoring case
        return removeDiacriticalMarks(str1).equalsIgnoreCase(removeDiacriticalMarks(str2));
    }

    public static Map<String, String> getCustomDateTimePatterns() {
        return new HashMap<>(customDateTimePatterns); // Returns a defensive copy of the paymentHeaders map to prevent external modifications 
    }

    public static Map<String, String> getAppointmentsHeaders() {
        return new HashMap<>(appointmentsHeaders);
    }

    public static Map<String, String> getPaymentHeaders() {
        return new HashMap<>(paymentHeaders);
    }

    public static Map<String, String> getLaunchCommands() {
        return new HashMap<>(launchCommands);
    }

    public static Map<String, String> getFilesAndPaths() {
        return new HashMap<>(filesAndPaths);
    }

    public static Map<String, String> getOtherText() {
        return new HashMap<>(otherText);
    }

    public static DateTimeFormatter getDateAndTimeFormat() {
        return DateTimeFormatter.ofPattern(customDateTimePatterns.get("dateAndTime"));
    }

    public static DateTimeFormatter getDateFormat() {
        return DateTimeFormatter.ofPattern(customDateTimePatterns.get("date"));
    }

    public static DateTimeFormatter getTimeFormat() {
        return DateTimeFormatter.ofPattern(customDateTimePatterns.get("time"));
    }

    public static ZoneId getZoneId() {
        return ZoneId.of(customDateTimePatterns.get("zoneId"));
    }

    private static class ConfigStructure {
        private Map<String, String> customDateTimePatterns;
        private Map<String, String> appointmentsHeaders;
        private Map<String, String> paymentHeaders;
        private Map<String, String> launchCommands;
        private Map<String, String> filesAndPaths;
        private Map<String, String> otherText;

        public Map<String, String> getCustomDateTimePatterns() {
            return customDateTimePatterns;
        }

        public Map<String, String> getAppointmentsHeaders() {
            return appointmentsHeaders;
        }

        public Map<String, String> getPaymentHeaders() {
            return paymentHeaders;
        }

        public Map<String, String> getLaunchCommands() {
            return launchCommands;
        }

        public Map<String, String> getFilesAndPaths() {
            return filesAndPaths;
        }

        public Map<String, String> getOtherText() {
            return otherText;
        }
    }
}