package xyz.lfmrad.clinitools.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class Appointment {
    
    private Client client;
    private ZonedDateTime appointmentTimeData;
    private ArrayList<Treatment> treatments; // Order not maintained, duplicates allowed
    private PaymentMethod payment;


    public Appointment(Client client, ArrayList<Treatment> treatments, ZonedDateTime appointmentTimeData) {
        this.client = client;
        this.treatments = treatments;
        this.appointmentTimeData = appointmentTimeData;
    }
}
