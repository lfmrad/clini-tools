package xyz.lfmrad.clinitools.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class Appointment {

    private Client client;
    private ZonedDateTime appointmentTimeData;
    private ArrayList<Activity> treatments; // Order not maintained, duplicates allowed
    private PaymentMethod payment;


    public Appointment(Client client, ArrayList<Activity> treatments, ZonedDateTime appointmentTimeData) {
        this.client = client;
        this.treatments = treatments;
        this.appointmentTimeData = appointmentTimeData;
    }
}
