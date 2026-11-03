package com.cartycoon.model;

/** 7-seat car, families love it. */
public class Suv extends Car {

    public Suv(String plate, String model) {
        super(plate, model);
    }

    @Override public String getTypeName() { return "SUV 7 chỗ"; }
    @Override public int getSeats() { return 7; }
    @Override public long getBuyPrice() { return 10_000_000; }
    @Override public long getDailyPrice() { return 1_200_000; }
    @Override public int getWearPerDay() { return 4; }
}
