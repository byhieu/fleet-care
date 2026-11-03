package com.cartycoon.model;

/** Shop level, reached by the total money earned from rentals. Higher levels unlock cars and customers. */
public enum Level {
    LV1(1, 0, "Bãi xe nhỏ"),
    LV2(2, 15_000_000, "Cửa hàng khu phố"),
    LV3(3, 40_000_000, "Công ty cho thuê"),
    LV4(4, 80_000_000, "Chuỗi cửa hàng"),
    LV5(5, 150_000_000, "Ông trùm cho thuê xe");

    private final int number;
    private final long revenueNeeded;
    private final String title;

    Level(int number, long revenueNeeded, String title) {
        this.number = number;
        this.revenueNeeded = revenueNeeded;
        this.title = title;
    }

    public static Level of(long totalRevenue) {
        Level result = LV1;
        for (Level level : values()) {
            if (totalRevenue >= level.revenueNeeded) result = level;
        }
        return result;
    }

    /** The next level, or null at the top. */
    public Level next() {
        return ordinal() + 1 < values().length ? values()[ordinal() + 1] : null;
    }

    public int getNumber() { return number; }
    public long getRevenueNeeded() { return revenueNeeded; }
    public String getTitle() { return title; }

    @Override
    public String toString() {
        return "Cấp " + number + " - " + title;
    }
}
