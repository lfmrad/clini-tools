package xyz.lfmrad.clinitools.model;

public class CardPayment implements PaymentMethod {
 
    private double amount;

    @Override
    public double getAmount() {
        return amount;
    } 

    @Override
    public String getDetails() {
        return "Cash Payment";
    }
} 