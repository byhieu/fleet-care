package com.cartycoon.model;

/** A tourist group: needs a 16-seat car for several days. */
public class TouristCustomer extends Customer {

    public TouristCustomer(String name, int days) {
        super(name, days);
    }

    @Override public String getTypeName() { return "Đoàn du lịch"; }
    @Override public String getRequest() { return "Xe từ 16 chỗ cho cả đoàn"; }
    @Override public boolean accepts(Car car) { return car.getSeats() >= 16; }
    @Override public double getPayRate() { return 1.1; }
    @Override public int getDamageChance() { return 15; }
}
