package xyz.lfmrad.clinitools.model;

import java.util.Objects;

public class Payment {
    private double amount;
    private String paymentMethod;

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Payment(double amount, String paymentMethod) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
    return "Payment{" +
           "amount=" + amount +
           ", paymentMethod='" + paymentMethod + '\'' +
           '}';
    }

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Double.compare(payment.amount, amount) == 0 &&
               Objects.equals(paymentMethod, payment.paymentMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, paymentMethod);
    }
} 