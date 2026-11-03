package com.cartycoon.logic;

import com.cartycoon.logic.CarCatalog.CarOffer;
import com.cartycoon.model.Car;
import com.cartycoon.model.CarStatus;
import com.cartycoon.model.Customer;
import com.cartycoon.model.GameState;
import com.cartycoon.model.StudentCustomer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/** A simple bot plays whole games, to check that the game can be won and that buying cars pays off. */
class BalanceTest {
    private static final long BUFFER = 3_000_000;

    /** One day of a simple strategy: repair broken cars, serve the best-paying customers with clean cars, maybe buy a car. */
    private static void playDay(GameEngine engine, boolean buysCars) {
        GameState s = engine.getState();
        for (Car car : new ArrayList<>(s.getCars())) {
            if (car.getStatus() == CarStatus.BROKEN && s.getMoney() > car.getRepairCost() + BUFFER) engine.repair(car);
        }
        for (Customer customer : new ArrayList<>(s.getCustomers())) {
            Car best = null;
            for (Car car : s.getCars()) {
                if (car.getStatus() == CarStatus.FREE && customer.accepts(car)
                        && (best == null || customer.price(car) > customer.price(best))) best = car;
            }
            if (best == null) continue;
            if (!best.isClean() && !(customer instanceof StudentCustomer) && s.getMoney() >= GameEngine.WASH_PRICE) {
                engine.wash(best);
            }
            if (best.getCondition() < 60 && s.getMoney() > best.getRepairCost() + BUFFER) {
                engine.repair(best); // a worn car makes the customer unhappy
            }
            engine.rent(customer, best);
        }
        if (buysCars) {
            List<CarOffer> offers = new ArrayList<>(CarCatalog.OFFERS);
            for (int i = offers.size() - 1; i >= 0; i--) {
                CarOffer offer = offers.get(i);
                long owned = s.getCars().stream().filter(c -> c.getModel().equals(offer.model())).count();
                if (offer.isUnlocked(s.getLevel()) && owned < 2 && s.getMoney() > offer.preview().getBuyPrice() + BUFFER) {
                    engine.buy(offer);
                    break;
                }
            }
        }
        engine.nextDay();
    }

    private static long play(long seed, boolean buysCars) {
        GameEngine engine = GameEngine.newGame("Bot", new Random(seed));
        while (!engine.getState().isGameOver()) playDay(engine, buysCars);
        assertFalse(engine.getState().getGameOverReason().startsWith("Phá sản"), "bot went bankrupt, seed " + seed);
        return ScoreBoard.score(engine.getState());
    }

    @Test
    @DisplayName("Cân bằng game: chơi chăm (mua thêm xe) được hạng A trở lên, chơi lười chỉ hạng B hoặc C")
    void buyingCarsPaysOff() {
        long active = 0;
        long lazy = 0;
        for (long seed = 1; seed <= 10; seed++) {
            active += play(seed, true);
            lazy += play(seed, false);
        }
        long activeAverage = active / 10;
        long lazyAverage = lazy / 10;
        List<String> ranks = List.of("S", "A", "B", "C"); // best first
        assertTrue(ranks.indexOf(ScoreBoard.rank(activeAverage)) <= 1, "active bot average " + activeAverage);
        assertTrue(ranks.indexOf(ScoreBoard.rank(lazyAverage)) >= 2, "lazy bot average " + lazyAverage);
    }
}
