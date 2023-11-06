package xyz.lfmrad.clinitools.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import xyz.lfmrad.clinitools.Configuration;

public class Appointment {
    private String clientName;
    private ZonedDateTime appointmentTimeData;
    private List<Activity> activities; 
    private List<Payment> payments;
    private String paymentStatus;
    private String notes;

    public Appointment() {
        this.activities = new ArrayList<>();
        this.payments = new ArrayList<>();
    }

    public String getClientName() {
        return clientName;
    }

    public ZonedDateTime getAppointmentTimeData() {
        return appointmentTimeData;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getNotes() {
        return notes;
    }

    public double getTotalPVP() {
        double totalPVP = 0;
        for (Activity activity : activities) {
            totalPVP += activity.getPrice();
        }
        return totalPVP;
    }

    public double getTotalCost() {
        double totalCost = 0;
        for (Activity activity : activities) {
            totalCost += -(activity.getCostWithTax() + activity.getCostWithTaxThirdParty());
        }
        return totalCost;
    }

    public double getCashTotal() {
        return getTotalPaidFor(Configuration.getOtherText().get("cashPayment"));
    }

    public double getCardTotal() {
        return getTotalPaidFor(Configuration.getOtherText().get("cardPayment"));
    }

    public double getBizumTotal() {
        return getTotalPaidFor(Configuration.getOtherText().get("bizumPayment"));
    }

    public double getFinancingTotal() {
        return getTotalPaidFor(Configuration.getOtherText().get("financingInstallment"));
    }

    private double getTotalPaidFor(String paymentMethod) {
        double totalPaid = 0;
        for (Payment payment : payments) {
            if (paymentMethod.equalsIgnoreCase(payment.getPaymentMethod())) {
                totalPaid += payment.getAmount();
            } 
        }
        return totalPaid;
    }

    public double getProfit() {
        return getTotalPVP() + getTotalCost();
    }


    public boolean setClientName(String clientName) {
        if (this.clientName == null) { 
            this.clientName = clientName;
            return true;
        }
        return false;
    }

    public void setAppointmentTimeData(ZonedDateTime appointmentTimeData) {
        if (this.appointmentTimeData == null) { 
            this.appointmentTimeData = appointmentTimeData;
        }
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
        // legacy data related logic discarded bc: if row repeats is precisely bc of there is an activity, so it should be able to be repeated (the same activity can be done twice) *
        // if (!activities.contains(activity)) {
        //     activities.add(activity);
        // }
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        // legacy data related logic discarded bc: analog to *
        // if (!payments.contains(payment)) {
        //     payments.add(payment);
        // }
    }

    public void setPaymentStatus(String paymentStatus) {
        if (this.paymentStatus == null) { 
            this.paymentStatus = paymentStatus;
        }
    }

    // add concatenation case for multiple notes for same client
    public void setNotes(String notes) {
        if (this.notes == null) { 
            this.notes = notes;
        } else if (notes != null) {
            this.notes += "; " + notes;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Appointment {\n");
        sb.append("\tClient Name: ").append(clientName).append("\n");
        sb.append("\tAppointment Time: ").append(appointmentTimeData).append("\n");
        sb.append("\tActivities: \n");
        
        for (Activity activity : activities) {
            sb.append("\t\t").append(activity).append("\n");
        }

        sb.append("\tPayment Status: ").append(paymentStatus).append("\n");
        
        sb.append("\tPayments: \n");
        for (Payment payment : payments) {
            sb.append("\t\t").append(payment).append("\n");
        }

        sb.append("\tNotes: ").append(notes).append("\n");
        sb.append("}");

        return sb.toString();
    }
}
