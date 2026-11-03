package com.cartycoon.model;

import java.io.Serializable;

/**
 * A customer waiting in the shop. Every type of customer (student, family, racer...) is a subclass that decides
 * which car it accepts, how much it pays and how hard it drives.
 */
public abstract class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int days;

    protected Customer(String name, int days) {
        this.name = name;
        this.days = days;
    }

    /** "Sinh viên", "Gia đình"... */
    public abstract String getTypeName();

    /** What the customer asks for, shown on screen. */
    public abstract String getRequest();

    /** Would this customer take this car? */
    public abstract boolean accepts(Car car);

    /** 0.8 = pays 80% of the normal price, 1.5 = pays 50% more. */
    public abstract double getPayRate();

    /** Chance (in %) that the car comes back damaged. */
    public abstract int getDamageChance();

    /** Racers drive harder: their cars wear out twice as fast. */
    public int getWearMultiplier() {
        return 1;
    }

    /** By default a customer is happy with a clean car in good condition. */
    public boolean isHappyWith(Car car) {
        return car.isClean() && car.getCondition() >= 60;
    }

    /** Extra money given when the car comes back and the customer is happy. */
    public long getTip(long price) {
        return 0;
    }

    /** Price of the whole rental: daily price x days x pay rate. */
    public long price(Car car) {
        return Math.round(car.getDailyPrice() * days * getPayRate());
    }

    public String getName() { return name; }
    public int getDays() { return days; }

    @Override
    public String toString() {
        return name + " (" + getTypeName() + ")";
    }
}
