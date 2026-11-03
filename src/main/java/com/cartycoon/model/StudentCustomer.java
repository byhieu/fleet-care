package com.cartycoon.model;

/** Wants a cheap car, pays a bit less, does not care if the car is dirty. */
public class StudentCustomer extends Customer {
    public static final long MAX_DAILY_PRICE = 900_000;

    public StudentCustomer(String name, int days) {
        super(name, days);
    }

    @Override public String getTypeName() { return "Sinh viên"; }
    @Override public String getRequest() { return "Xe rẻ (tối đa 900.000 đ/ngày)"; }
    @Override public boolean accepts(Car car) { return car.getDailyPrice() <= MAX_DAILY_PRICE; }
    @Override public double getPayRate() { return 0.8; }
    @Override public int getDamageChance() { return 20; }

    @Override
    public boolean isHappyWith(Car car) {
        return car.getCondition() >= 40; // a dirty car is fine for a student
    }
}
