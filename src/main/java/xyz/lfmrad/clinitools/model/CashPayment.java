package xyz.lfmrad.clinitools.model;

public class CashPayment implements PaymentMethod {
 
    private double amount;

    @Override
    public double getAmount() {
        return amount;
    } 

    @Override
    public String getDetails() {
        return "Card Payment";
    }
} 