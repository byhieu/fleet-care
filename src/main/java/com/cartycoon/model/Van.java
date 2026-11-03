package com.cartycoon.model;

/** 16-seat minibus for tourist groups. */
public class Van extends Car {

    public Van(String plate, String model) {
        super(plate, model);
    }

    @Override public String getTypeName() { return "Xe 16 chỗ"; }
    @Override public int getSeats() { return 16; }
    @Override public long getBuyPrice() { return 15_000_000; }
    @Override public long getDailyPrice() { return 1_800_000; }
    @Override public int getWearPerDay() { return 5; }
}
