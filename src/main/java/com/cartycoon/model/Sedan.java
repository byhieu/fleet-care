package com.cartycoon.model;

public class Sedan extends Car {

    public Sedan(String plate, String model) {
        super(plate, model);
    }

    @Override public String getTypeName() { return "Sedan"; }
    @Override public int getSeats() { return 5; }
    @Override public long getBuyPrice() { return 7_000_000; }
    @Override public long getDailyPrice() { return 800_000; }
    @Override public int getWearPerDay() { return 3; }
}
