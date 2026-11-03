package com.cartycoon.model;

/** Small, cheap city car. */
public class CityCar extends Car {

    public CityCar(String plate, String model) {
        super(plate, model);
    }

    @Override public String getTypeName() { return "Xe nhỏ"; }
    @Override public int getSeats() { return 4; }
    @Override public long getBuyPrice() { return 5_000_000; }
    @Override public long getDailyPrice() { return 500_000; }
    @Override public int getWearPerDay() { return 3; }
}
