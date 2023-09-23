package xyz.lfmrad.clinitools.model;

import java.util.ArrayList;

public class MixedPayment implements PaymentMethod {
    private ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();

    public void addPaymentMethod(PaymentMethod method) {
        paymentMethods.add(method);
    }

    @Override
    public double getAmount() {
        return paymentMethods.stream().mapToDouble(PaymentMethod::getAmount).sum();
    }

    @Override
    public String getDetails() {
        return paymentMethods.stream()
                             .map(PaymentMethod::getDetails)
                             .reduce((details1, details2) -> details1 + " + " + details2)
                             .orElse("No payment details");
    }
}
