package com.cartycoon.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Everything about one game. The whole object is written to save.dat to save the game.
 * Money can only change through earn / receive / spend / charge, so it is never changed by mistake.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String playerName;
    private int day = 1;
    private long money;
    private int reputation = 50;          // 0..100
    private long totalRevenue;            // money earned from rentals, decides the level
    private int rentalCount;
    private int happyCustomers;
    private int levelReached = 1;
    private long incomeToday;
    private long costToday;
    private int extraCustomersToday;
    private boolean gameOver;
    private String gameOverReason;
    private final List<Car> cars = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Rental> rentals = new ArrayList<>();
    private final List<String> log = new ArrayList<>();
    private final Set<Achievement> achievements = EnumSet.noneOf(Achievement.class);

    public GameState(String playerName, long money) {
        this.playerName = playerName;
        this.money = money;
    }

    /** Money from a rental or a tip: counts for the level. */
    public void earn(long amount) {
        money += amount;
        totalRevenue += amount;
        incomeToday += amount;
    }

    /** Money that is not a rental (selling a car, a prize...): does not count for the level. */
    public void receive(long amount) {
        money += amount;
        incomeToday += amount;
    }

    /** A purchase the player chooses: refused when there is not enough money (GameEngine checks first and explains). */
    public void spend(long amount) {
        if (amount > money) {
            throw new IllegalStateException("Không đủ tiền: cần " + amount + ", chỉ có " + money);
        }
        money -= amount;
        costToday += amount;
    }

    /** A cost the player cannot refuse (garage fee, fine): money may go below 0. */
    public void charge(long amount) {
        money -= amount;
        costToday += amount;
    }

    public void changeReputation(int delta) {
        reputation = Math.max(0, Math.min(100, reputation + delta));
    }

    /** 1 to 5 stars. */
    public int getStars() {
        return 1 + reputation * 4 / 100;
    }

    public Level getLevel() {
        return Level.of(totalRevenue);
    }

    public void addLog(String text) {
        log.add("Ngày " + day + ": " + text);
    }

    public void startNewDay() {
        day++;
        incomeToday = 0;
        costToday = 0;
        extraCustomersToday = 0;
    }

    public void endGame(String reason) {
        gameOver = true;
        gameOverReason = reason;
    }

    public void countRental() {
        rentalCount++;
    }

    public void countHappyCustomer() {
        happyCustomers++;
    }

    public String getPlayerName() { return playerName; }
    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }
    public long getMoney() { return money; }
    public int getReputation() { return reputation; }
    public long getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(long totalRevenue) { this.totalRevenue = totalRevenue; }
    public int getRentalCount() { return rentalCount; }
    public int getHappyCustomers() { return happyCustomers; }
    public int getLevelReached() { return levelReached; }
    public void setLevelReached(int levelReached) { this.levelReached = levelReached; }
    public long getIncomeToday() { return incomeToday; }
    public long getCostToday() { return costToday; }
    public int getExtraCustomersToday() { return extraCustomersToday; }
    public void addExtraCustomersToday(int count) { extraCustomersToday += count; }
    public boolean isGameOver() { return gameOver; }
    public String getGameOverReason() { return gameOverReason; }
    public List<Car> getCars() { return cars; }
    public List<Customer> getCustomers() { return customers; }
    public List<Rental> getRentals() { return rentals; }
    public List<String> getLog() { return log; }
    public Set<Achievement> getAchievements() { return achievements; }
}
