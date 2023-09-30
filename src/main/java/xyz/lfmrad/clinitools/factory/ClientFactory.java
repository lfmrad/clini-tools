package xyz.lfmrad.clinitools.factory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import xyz.lfmrad.clinitools.model.Client;
import xyz.lfmrad.clinitools.common.AppConstants;

public class ClientFactory {

    // This set will store the names of clients we've already processed
    private static Set<String> existingClients = new HashSet<>();
    
    public static Client fromRowData(Map<String, String> rowData) {

        String clientName = rowData.get(AppConstants.CLIENT_NAME_KEY);

        if (existingClients.contains(clientName)) {
            return null;
        } else {
            existingClients.add(clientName);
            return new Client(clientName);
        }
    }
}
