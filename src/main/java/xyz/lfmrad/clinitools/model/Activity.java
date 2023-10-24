package xyz.lfmrad.clinitools.model;

import java.util.Objects;

public class Activity {
    private String name;
    private double price;
    private double costWithTax;
    private double costWithTaxThirdParty;

    public Activity(String name, double price, double costWithTax, double costWithTaxThirdParty) {
        this.name = name;
        this.price = price;
        this.costWithTax = costWithTax;
        this.costWithTaxThirdParty = costWithTaxThirdParty;
    }
    
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getCostWithTax() {
        return costWithTax;
    }

    public double getNetCost() {
        return costWithTaxThirdParty;
    }

    public double getCostWithTaxThirdParty() {
        return costWithTaxThirdParty;
    }

    public double getNetCostThirdParty() {
        return costWithTaxThirdParty;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", costWithTax=" + costWithTax +
                ", costWithTaxThirdParty=" + costWithTaxThirdParty +
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
