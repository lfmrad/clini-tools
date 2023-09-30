package xyz.lfmrad.clinitools.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import xyz.lfmrad.clinitools.common.AppConstants;

public class DataCleaner {
    public static List<Map<String, String>> clean(List<Map<String, String>> data) {
        Iterator<Map<String, String>> iterator = data.iterator();
        while (iterator.hasNext()) {
            Map<String, String> row = iterator.next();
            int currentIndex = data.indexOf(row);
    
            Map<String, String> nextRow = null;
            if (currentIndex < data.size() - 1) {
                nextRow = data.get(currentIndex + 1);
            }
    
            if (isSummaryRow(row) || isSuperfluousData(row, nextRow)) {
                iterator.remove();
            } else if (!isRowAssociatedToClient(row) && currentIndex > 0) {
                Map<String, String> prevRow = data.get(currentIndex - 1);
                String prevRowName = prevRow.get(AppConstants.CLIENT_NAME_KEY); 
                row.put(AppConstants.CLIENT_NAME_KEY, prevRowName);
            }
        }
        return data;
    }
    
    private static boolean isSummaryRow(Map<String, String> row) {
        return row.values().stream().anyMatch(value -> value.contains(AppConstants.SUMMARY_TEXT));
    }

    private static boolean isSuperfluousData(Map<String, String> row, Map<String, String> nextRow) {
        if (row.containsValue(AppConstants.DOCUMENT_TEXT)) {
            if (isRowAssociatedToClient(row) && isNextRowRelated(nextRow)) {
                // copies name to next row before deleting this one
                nextRow.put(AppConstants.CLIENT_NAME_KEY, row.get(AppConstants.CLIENT_NAME_KEY));
            } 
            return true;
        } else {
            return false;
        }
    }

    private static boolean isRowAssociatedToClient(Map<String, String> row) {
        return row.containsKey(AppConstants.CLIENT_NAME_KEY);
    }

    private static boolean isNextRowRelated(Map<String, String> nextRow) {
        if (nextRow == null) { // prevents error when there is no nextRow available (last item)
            return false; // 
        } else {
            return !nextRow.containsKey(AppConstants.CLIENT_NAME_KEY);
        }
    }
}
