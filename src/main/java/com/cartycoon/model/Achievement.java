package com.cartycoon.model;

import java.util.function.Predicate;

/** Small goals that pop up when reached. Each one knows how to check itself. */
public enum Achievement {
    FIRST_RENTAL("Khai trương", "Cho thuê chiếc xe đầu tiên", s -> s.getRentalCount() >= 1),
    HAPPY_10("Khách quen", "10 khách hài lòng", s -> s.getHappyCustomers() >= 10),
    FIVE_CARS("Bãi xe đông vui", "Sở hữu 5 chiếc xe", s -> s.getCars().size() >= 5),
    SPORTS_CAR("Chơi lớn", "Mua một chiếc xe thể thao", s -> s.getCars().stream().anyMatch(c -> c instanceof SportsCar)),
    RICH("Đại gia", "Có 100.000.000 đ tiền mặt", s -> s.getMoney() >= 100_000_000),
    FIVE_STARS("5 sao", "Uy tín đạt 100", s -> s.getReputation() >= 100);

    private final String title;
    private final String description;
    private final Predicate<GameState> condition;

    Achievement(String title, String description, Predicate<GameState> condition) {
        this.title = title;
        this.description = description;
        this.condition = condition;
    }

    public boolean isReached(GameState state) {
        return condition.test(state);
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return title + ": " + description;
    }
}
