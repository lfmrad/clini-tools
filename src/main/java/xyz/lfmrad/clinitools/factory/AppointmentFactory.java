package xyz.lfmrad.clinitools.factory;

import java.util.Map;
import xyz.lfmrad.clinitools.model.Appointment;



public class AppointmentFactory {
    public static Appointment fromRowData(Map<String, String> rowData) {

        Client client = new ClientFactory.fromRowData(rowData)

        Appointment appointment = new Appointment(null, null, null);
    }
}
