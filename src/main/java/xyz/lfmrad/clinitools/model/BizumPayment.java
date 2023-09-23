package xyz.lfmrad.clinitools.model;

public class BizumPayment implements PaymentMethod {
 
    private double amount;

    @Override
    public double getAmount() {
        return amount;
    } 

    @Override
    public String getDetails() {
        return "Bizum Payment";
    }
} 