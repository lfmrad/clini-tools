package xyz.lfmrad.clinitools.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import xyz.lfmrad.clinitools.Configuration;

public final class DataCleaner {
    private DataCleaner() {
        throw new AssertionError("DataCleaner should not be instantiated.");
    }

    public static List<Map<String, String>> clean(List<Map<String, String>> data) {
        Iterator<Map<String, String>> iterator = data.iterator();
        String lastLeadingRowClient = null;
        while (iterator.hasNext()) {
            Map<String, String> row = iterator.next();
            if (isLeadingRow(row)) {
                lastLeadingRowClient = row.get(Configuration.getAppointmentsHeaders().get("name"));
            }
            if (isSummaryRow(row)) { // deletes summary rows
                iterator.remove();
            } else if (isIrrelevantDocumentation(row) || isMeaninglessData(row)) { 
                int currentIndex = data.indexOf(row); // negligible performance impact for the small datasets (30-40 rows) this is going to process
                Optional<Map<String, String>> nextRowOpt = getAdjacentRow(data, currentIndex, RowType.NEXT);
                // copies Client to the next row before deleting if the next one is associated with the former but does not have a Client
                // example: row 1 - client_name + IRRELEVANT DATA; row 2 - no name + RELEVANT DATA identified with the previous row
                if (isLeadingRow(row) && nextRowOpt.map(DataCleaner::isDependentOnPreviousRow).orElse(false)) { 
                    copyClient(nextRowOpt.get(), lastLeadingRowClient); // can safely unwrap bc it only gets here if there is a next row
                }
                iterator.remove();
            } else if (isDependentOnPreviousRow(row)) {
                copyClient(row, lastLeadingRowClient);
            }
        }
        return data;
    }

    private enum RowType {
        PREVIOUS, NEXT
    }

    private static Optional<Map<String, String>> getAdjacentRow(List<Map<String, String>> data, int currentIndex, RowType mode) {
        if (mode == RowType.NEXT && currentIndex < data.size() - 1) {
            return Optional.of(data.get(currentIndex + 1));
        } else if (mode == RowType.PREVIOUS && currentIndex > 0) {
            return Optional.of(data.get(currentIndex - 1));
        } else {
            return Optional.empty();
        }
    }
    
    private static boolean isSummaryRow(Map<String, String> row) {
        return row.values().stream().anyMatch(value -> value.contains(Configuration.getAppointmentsHeaders().get("summaryText")));
    }

    private static boolean isMeaninglessData(Map<String, String> row) {
        return !row.containsKey(Configuration.getAppointmentsHeaders().get("activities"));
    }

    private static boolean isIrrelevantDocumentation(Map<String, String> row) {
        return row.containsValue(Configuration.getOtherText().get("documentText"));
    }

    private static boolean isLeadingRow(Map<String, String> row) {
        return row.containsKey(Configuration.getOtherText().get("documentText"));
    }

    private static boolean isDependentOnPreviousRow(Map<String, String> row) {
        return !row.containsKey(Configuration.getAppointmentsHeaders().get("name"));
    }

    private static void copyClient(Map<String, String> row, String clientName) {
        row.put(Configuration.getAppointmentsHeaders().get("name"), clientName);
    }
}