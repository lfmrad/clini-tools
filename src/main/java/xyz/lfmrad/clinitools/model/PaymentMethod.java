package xyz.lfmrad.clinitools.model;

// note to self: doesn't enforce specific fields to allow encapsulation for the details of each method - 'interface: what actions can be done' no: 'how the data is stored' 

public interface PaymentMethod {
    double getAmount();
    String getDetails();
}
