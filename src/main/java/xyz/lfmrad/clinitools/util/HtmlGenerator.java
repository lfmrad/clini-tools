package xyz.lfmrad.clinitools.util;

import java.util.Map;        
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HtmlGenerator {

    public static String generateTable(List<Map<String, String>> data, List<String> headers) {
        StringBuilder sb = new StringBuilder();
    
        sb.append("<html lang='en'>"
            + "<head>"
            + "    <meta charset='UTF-8'>"
            + "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>"
            + "    <title>Data</title>"
            + "    <link rel='stylesheet' href='table.css'>"
            + "</head>"
            + "<table>"
        );
        sb.append("<tr>");
        for (String header : headers) {
            sb.append("<th>").append(header).append("</th>");
        }
        sb.append("</tr>");
        for (Map<String, String> row : data) {
            sb.append("<tr>");
            for (String header : headers) {
                sb.append("<td>");
                if(row.containsKey(header)) {
                    sb.append(row.get(header));
                }
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>"
            + "</body>"
            + "</html>"
        );
        return sb.toString();
    }

    public static void writeToFile(String html, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(html);
            System.out.println("HTML written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
