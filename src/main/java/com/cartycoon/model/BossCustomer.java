package com.cartycoon.model;

/** A VIP: sedan or luxury car only, it must be clean and in very good condition. Tips 10% when happy. */
public class BossCustomer extends Customer {

    public BossCustomer(String name, int days) {
        super(name, days);
    }

    @Override public String getTypeName() { return "Sếp lớn"; }
    @Override public String getRequest() { return "Sedan/xe sang, sạch, từ 80%"; }
    @Override public double getPayRate() { return 1.3; }
    @Override public int getDamageChance() { return 5; }

    @Override
    public boolean accepts(Car car) {
        return (car instanceof Sedan || car instanceof LuxuryCar) && car.isClean() && car.getCondition() >= 80;
    }

    @Override
    public long getTip(long price) {
        return price / 10;
    }
}
