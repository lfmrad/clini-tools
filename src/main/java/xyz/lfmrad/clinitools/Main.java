package xyz.lfmrad.clinitools;

import java.util.List;
import java.util.Map;
import xyz.lfmrad.clinitools.common.AppConstants;
import xyz.lfmrad.clinitools.util.*;


public class Main {
    public static void main(String[] args) {

        List<Map<String, String>> data = ExcelReader.readSheet("test-resources/partA.xls");

        List<Map<String, String>> cleanData = DataCleaner.clean(data);

        ExcelTest.testDataPrinter(cleanData);

        List<String> headers = List.of(
            AppConstants.CLIENT_NAME_KEY, 
            AppConstants.ACTIVITY_DESCRIPTION,
            AppConstants.ACTIVITY_PRICE_WITH_TAX,
            AppConstants.ACTIVITY_COST_WITH_TAX,
            AppConstants.ACTIVITY_COST_WITH_TAX_EXT,
            AppConstants.PVP
        );

        String htmlTable = HtmlGenerator.generateTable(data, headers);
        HtmlGenerator.writeToFile(htmlTable, "test-resources/partA-test.html");
    }
}
