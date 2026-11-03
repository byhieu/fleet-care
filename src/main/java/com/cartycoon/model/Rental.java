package com.cartycoon.model;

import java.io.Serializable;

/** A car that is out with a customer. */
public class Rental implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Customer customer;
    private final Car car;
    private final long price;
    private final boolean happy;
    private int daysLeft;

    public Rental(Customer customer, Car car, long price, boolean happy) {
        this.customer = customer;
        this.car = car;
        this.price = price;
        this.happy = happy;
        this.daysLeft = customer.getDays();
    }

    /** One more day on the road; returns true when the rental is over. */
    public boolean passDay() {
        daysLeft--;
        return daysLeft <= 0;
    }

    public Customer getCustomer() { return customer; }
    public Car getCar() { return car; }
    public long getPrice() { return price; }
    public boolean isHappy() { return happy; }
    public int getDaysLeft() { return daysLeft; }
}
