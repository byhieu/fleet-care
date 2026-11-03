package com.cartycoon.model;

/** Only wants a sports car, pays a lot, but drives it hard. */
public class RacerCustomer extends Customer {

    public RacerCustomer(String name, int days) {
        super(name, days);
    }

    @Override public String getTypeName() { return "Dân chơi"; }
    @Override public String getRequest() { return "Chỉ thích xe thể thao!"; }
    @Override public boolean accepts(Car car) { return car instanceof SportsCar; }
    @Override public double getPayRate() { return 1.5; }
    @Override public int getDamageChance() { return 40; }

    @Override
    public int getWearMultiplier() {
        return 2;
    }
}
