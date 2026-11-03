package com.cartycoon.model;

/** Needs 7 seats, drives carefully and leaves a small tip when happy. */
public class FamilyCustomer extends Customer {

    public FamilyCustomer(String name, int days) {
        super(name, days);
    }

    @Override public String getTypeName() { return "Gia đình"; }
    @Override public String getRequest() { return "Xe từ 7 chỗ"; }
    @Override public boolean accepts(Car car) { return car.getSeats() >= 7; }
    @Override public double getPayRate() { return 1.0; }
    @Override public int getDamageChance() { return 5; }

    @Override
    public long getTip(long price) {
        return 200_000;
    }
}
