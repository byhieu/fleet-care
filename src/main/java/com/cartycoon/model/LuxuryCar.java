package com.cartycoon.model;

/** Luxury car for VIP customers. Well built, so it wears out slowly. */
public class LuxuryCar extends Car {

    public LuxuryCar(String plate, String model) {
        super(plate, model);
    }

    @Override public String getTypeName() { return "Xe sang"; }
    @Override public int getSeats() { return 5; }
    @Override public long getBuyPrice() { return 35_000_000; }
    @Override public long getDailyPrice() { return 4_500_000; }
    @Override public int getWearPerDay() { return 3; }
}
