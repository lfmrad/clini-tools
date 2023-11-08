package xyz.lfmrad.clinitools.util;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.lfmrad.clinitools.Configuration;
import xyz.lfmrad.clinitools.model.*;

public final class DataBuilder {
    public static Map<String, Appointment> appointmentsMap = new HashMap<>();
    
    private DataBuilder() {
        throw new AssertionError("DataModelConstructor should not be instantiated.");
    }

    public static List<Appointment> buildAppointmentsList(List<Map<String, String>> payments, List<Map<String, String>> appointments) {
        for (Map<String, String> appointmentRow : appointments) {
            String clientName = appointmentRow.get(Configuration.getAppointmentsHeaders().get("name"));

            String timeAsString = appointmentRow.get(Configuration.getAppointmentsHeaders().get("startHour"));
            String dateAsString = appointmentRow.get(Configuration.getAppointmentsHeaders().get("date"));
            ZonedDateTime dateTime = DateTools.combineToZonedDateTime(dateAsString, timeAsString, Configuration.getZoneId());

            String activity = appointmentRow.get(Configuration.getAppointmentsHeaders().get("activities"));
            double price = parseDoubleOrZero(appointmentRow.get(Configuration.getAppointmentsHeaders().get("price")));
            double costWithTax = parseDoubleOrZero(appointmentRow.get(Configuration.getAppointmentsHeaders().get("costWithTax")));
            double costWithTaxThirdParty = parseDoubleOrZero(appointmentRow.get(Configuration.getAppointmentsHeaders().get
            ("costWithTaxThirdParty")));
            Activity currentActivity = new Activity(activity, price, costWithTax, costWithTaxThirdParty);

            String paymentStatus = appointmentRow.get(Configuration.getAppointmentsHeaders().get("paymentStatus"));
            String notes = appointmentRow.get(Configuration.getAppointmentsHeaders().get("notes"));
        
            Appointment targetAppointment = getAppointment(clientName);
            targetAppointment.setClientName(clientName);
            targetAppointment.setAppointmentTimeData(dateTime);
            targetAppointment.addActivity(currentActivity);
            targetAppointment.setNotes(notes);
            targetAppointment.setPaymentStatus(paymentStatus);
        }

        for (Map<String, String> paymentRow : payments) {

            String clientName = paymentRow.get(Configuration.getPaymentHeaders().get("name"));

            String paymentMethod = paymentRow.get(Configuration.getPaymentHeaders().get("paymentMethod"));
            Double paymentAmount = parseDoubleOrZero(paymentRow.get(Configuration.getPaymentHeaders().get("paidAmount")));
        
            Appointment targetAppointment = getAppointment(clientName);
            if (targetAppointment.setClientName(clientName)) {
                targetAppointment.setNotes(Configuration.getOtherText().get("historyError"));
                targetAppointment.setPaymentStatus(Configuration.getOtherText().get("unknownPayment"));

                String dateAsString = paymentRow.get(Configuration.getPaymentHeaders().get("date"));
                LocalDate date = LocalDate.parse(dateAsString, Configuration.getDateFormat(false));
                ZonedDateTime defaultDateTime = date.atStartOfDay(Configuration.getZoneId());

                String activity = paymentRow.get(Configuration.getPaymentHeaders().get("activities"));
                Activity currentActivity = new Activity(activity, 0, 0, 0);
                
                targetAppointment.setAppointmentTimeData(defaultDateTime);
                targetAppointment.addActivity(currentActivity);
            }
            targetAppointment.addPayment(new Payment(paymentAmount, paymentMethod));
        }

        List<Appointment> appointmentsList = new ArrayList<>(appointmentsMap.values());
        appointmentsList.sort(
            Comparator.comparing(Appointment::getAppointmentTimeData)
                    .thenComparing(Appointment::getClientName)
            );
        if (Configuration.isDebugEnabled()) {
            System.out.println(appointmentsList);
        }
        return appointmentsList;
    }

    public static Appointment getAppointment(String clientName) {
        if (appointmentsMap.containsKey(clientName)) {
                // Existing client - fetch the Appointment object
                return appointmentsMap.get(clientName);     
            } else {
                // New client - create a new Appointment object
                Appointment newAppointment = new Appointment();
                appointmentsMap.put(clientName, newAppointment);
                return newAppointment;
            }
    }

    public static double parseDoubleOrZero(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
    
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
