package com.cartycoon.model;

/** Fast, expensive, wears out quickly. */
public class SportsCar extends Car {

    public SportsCar(String plate, String model) {
        super(plate, model);
    }

    @Override public String getTypeName() { return "Xe thể thao"; }
    @Override public int getSeats() { return 2; }
    @Override public long getBuyPrice() { return 25_000_000; }
    @Override public long getDailyPrice() { return 3_000_000; }
    @Override public int getWearPerDay() { return 8; }

    /** Sports car parts are rare: repairs cost 50% more than for other cars. */
    @Override
    public long getRepairCost() {
        return Math.round(super.getRepairCost() * 1.5);
    }
}
