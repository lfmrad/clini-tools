package xyz.lfmrad.clinitools.model;

import java.util.Objects;

import xyz.lfmrad.clinitools.Configuration;

public class Activity {
    private String name;
    private double price;
    private double costWithTax;
    private double costWithTaxThirdParty;
    private String paymentStatus;

    public Activity(String name, double price, double costWithTax, double costWithTaxThirdParty, String paymentStatus) {
        this.name = name;
        this.price = price;
        this.costWithTax = costWithTax;
        this.costWithTaxThirdParty = costWithTaxThirdParty;
        this.paymentStatus = paymentStatus;
    }
    
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getNetPrice() {
        return price / 1.21f;
    }

    public double getCostWithTax() {
        return costWithTax;
    }

    public double getCostWithTaxThirdParty() {
        return costWithTaxThirdParty;
    }

    // TEMP. SEMI HARDCODED SOLUTION
    public double getTaxValue() {
        if (this.name.contains(Configuration.getOtherText().get("lowTaxMatch"))) {
            return Configuration.getIVA4() / 100f;
        } else {
            return Configuration.getIVA21() / 100f;
        }
    }

    public double getNetCost() {
        return costWithTax / (1 + getTaxValue());
    } 

    public double getNetCostThirdParty() {
        return costWithTaxThirdParty / (1 + getTaxValue());
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public boolean isPaidFor() {
        if (paymentStatus.equals(Configuration.getOtherText().get("pendingPayment"))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", costWithTax=" + costWithTax +
                ", costWithTaxThirdParty=" + costWithTaxThirdParty +
                ", paymentStatus=" + paymentStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Double.compare(activity.price, price) == 0 &&
            Double.compare(activity.costWithTax, costWithTax) == 0 &&
            Double.compare(activity.costWithTaxThirdParty, costWithTaxThirdParty) == 0 &&
            Objects.equals(name, activity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, costWithTax, costWithTaxThirdParty);
    }
}
