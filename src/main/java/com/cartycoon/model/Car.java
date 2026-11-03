package com.cartycoon.model;

import java.io.Serializable;

/**
 * A car of the shop. Every kind of car (CityCar, Sedan, Suv...) is a subclass that gives its own seats,
 * prices and wear: the game treats all of them the same way through this class (polymorphism).
 */
public abstract class Car implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Below this condition the car is broken and cannot be rented. */
    public static final int BROKEN_BELOW = 30;

    private final String plate;
    private final String model;
    private int condition = 100; // 0..100 %
    private int km;
    private boolean clean = true;
    private CarStatus status = CarStatus.FREE;

    protected Car(String plate, String model) {
        this.plate = plate;
        this.model = model;
    }

    /** "Xe nhỏ", "Sedan", "SUV 7 chỗ"... */
    public abstract String getTypeName();

    public abstract int getSeats();

    public abstract long getBuyPrice();

    public abstract long getDailyPrice();

    /** How many % of condition the car loses for each day on the road. */
    public abstract int getWearPerDay();

    /** Repair back to 100%: (100 - condition)% x buy price x 0,3. */
    public long getRepairCost() {
        return Math.round((100 - condition) / 100.0 * getBuyPrice() * 0.3);
    }

    /** Selling price: buy price x condition% x 0,6. */
    public long getSellPrice() {
        return Math.round(getBuyPrice() * condition / 100.0 * 0.6);
    }

    public void drive(int distance) {
        km += distance;
    }

    public void wear(int points) {
        condition = Math.max(0, condition - points);
        if (condition < BROKEN_BELOW && status == CarStatus.FREE) status = CarStatus.BROKEN;
    }

    public void rentOut() {
        status = CarStatus.RENTED;
    }

    /** The customer brings the car back: it is always dirty, and broken if it was driven too hard. */
    public void giveBack() {
        clean = false;
        status = condition < BROKEN_BELOW ? CarStatus.BROKEN : CarStatus.FREE;
    }

    public void repair() {
        condition = 100;
        if (status == CarStatus.BROKEN) status = CarStatus.FREE;
    }

    public void wash() {
        clean = true;
    }

    public void makeDirty() {
        clean = false;
    }

    /** Used to build demo games and tests: an old car with some km and wear. */
    public Car used(int condition, int km, boolean clean) {
        this.condition = condition;
        this.km = km;
        this.clean = clean;
        this.status = condition < BROKEN_BELOW ? CarStatus.BROKEN : CarStatus.FREE;
        return this;
    }

    public String getPlate() { return plate; }
    public String getModel() { return model; }
    public int getCondition() { return condition; }
    public int getKm() { return km; }
    public boolean isClean() { return clean; }
    public CarStatus getStatus() { return status; }

    @Override
    public String toString() {
        return model + " (" + plate + ")";
    }
}
