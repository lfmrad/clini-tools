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


    public String getNotes() {
        return notes;
    }

    public double getTotalPVP(boolean includeUnpaid) {
        double totalPVP = 0;
        for (Activity activity : activities) {
            if (!includeUnpaid && !activity.isPaidFor()) {
                continue;
            }
            totalPVP += activity.getPrice();
        }
        return totalPVP;
    }

    public double getTotalNetPVP(boolean includeUnpaid) {
        return getTotalPVP(includeUnpaid) / (1 + Configuration.getIVA21() / 100f);
    }

    public double getTotalCost(boolean includeUnpaid) {
        double totalCost = 0;
        for (Activity activity : activities) {
            if (!includeUnpaid && !activity.isPaidFor()) {
                continue;
            }
            totalCost += -(activity.getCostWithTax() + activity.getCostWithTaxThirdParty());
        }
        return totalCost;
    }

    public double getTotalNetCost(boolean includeUnpaid) {
        double totalNetCost = 0;
        for (Activity activity : activities) {
            if (!includeUnpaid && !activity.isPaidFor()) {
                continue;
            }
            totalNetCost += -(activity.getNetCost() + activity.getNetCostThirdParty());
        }
        return totalNetCost;
    }

    public double getCashTotal() {
        return getTotalPaidWith(Configuration.getOtherText().get("cashPayment"));
    }

    public double getCardTotal() {
        return getTotalPaidWith(Configuration.getOtherText().get("cardPayment"));
    }

    public double getBizumTotal() {
        return getTotalPaidWith(Configuration.getOtherText().get("bizumPayment"));
    }

    public double getFinancingTotal() {
        return getTotalPaidWith(Configuration.getOtherText().get("financingInstallment"));
    }

    private double getTotalPaidWith(String paymentMethod) {
        double totalPaid = 0;
        for (Payment payment : payments) {
            if (Configuration.compareStringsIgnoringGrammar(paymentMethod, payment.getPaymentMethod())) {
                totalPaid += payment.getAmount();
            } 
        }
        return totalPaid;
    }

    public double getTotalPaid() {
        double totalPaid = 0;
        for (Payment payment : payments) {
            totalPaid += payment.getAmount();
        }
        return totalPaid;
    }

    public double getNetProfit(boolean includeUnpaid) {
        return getTotalNetPVP(includeUnpaid) + getTotalNetCost(includeUnpaid);
    }

    public boolean hasPayments() {
        for (Activity activity : activities) {
            if (activity.isPaidFor()) {
                return true;
            }
        }
        return false;
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

        sb.append("\tPayments: \n");
        for (Payment payment : payments) {
            sb.append("\t\t").append(payment).append("\n");
        }

        sb.append("\tNotes: ").append(notes).append("\n");
        sb.append("}");

        return sb.toString();
    }
}
