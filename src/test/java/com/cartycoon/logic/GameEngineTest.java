package com.cartycoon.logic;

import com.cartycoon.logic.CarCatalog.CarOffer;
import com.cartycoon.model.Achievement;
import com.cartycoon.model.Car;
import com.cartycoon.model.CarStatus;
import com.cartycoon.model.Customer;
import com.cartycoon.model.FamilyCustomer;
import com.cartycoon.model.GameState;
import com.cartycoon.model.Level;
import com.cartycoon.model.Sedan;
import com.cartycoon.model.StudentCustomer;
import com.cartycoon.model.Suv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {
    private GameState state;
    private GameEngine engine;
    private Car vios;

    /** A customer that never damages the car, so the test does not depend on luck. */
    private static Customer carefulStudent(int days) {
        return new StudentCustomer("Em Test", days) {
            @Override
            public int getDamageChance() {
                return 0;
            }
        };
    }

    @BeforeEach
    void setUp() {
        state = new GameState("Test", 10_000_000);
        engine = new GameEngine(state, new Random(1));
        vios = new Sedan("51A-111.11", "Toyota Vios");
        state.getCars().add(vios);
    }

    @Test
    @DisplayName("Ván mới: ngày 1, 15 triệu, 2 xe cũ và 4 khách đang chờ")
    void newGame() {
        GameState s = GameEngine.newGame("An", new Random(7)).getState();
        assertEquals(1, s.getDay());
        assertEquals(GameEngine.START_MONEY, s.getMoney());
        assertEquals(2, s.getCars().size());
        assertEquals(4, s.getCustomers().size());
    }

    @Test
    @DisplayName("Cho thuê: khách trả tiền ngay, xe chuyển sang Đang cho thuê")
    void rentPaysAtOnce() {
        Customer student = carefulStudent(2);
        state.getCustomers().add(student);
        engine.rent(student, vios);
        assertEquals(11_280_000, state.getMoney());
        assertEquals(CarStatus.RENTED, vios.getStatus());
        assertTrue(state.getCustomers().isEmpty());
        assertEquals(1, state.getRentals().size());
    }

    @Test
    @DisplayName("Không cho thuê xe hỏng, xe đang thuê, hoặc xe khách không chịu")
    void rentRefused() {
        Customer family = new FamilyCustomer("Chị Lan", 1);
        state.getCustomers().add(family);
        GameException e = assertThrows(GameException.class, () -> engine.rent(family, vios));
        assertTrue(e.getMessage().contains("7 chỗ"));

        Customer student = carefulStudent(1);
        state.getCustomers().add(student);
        vios.used(20, 0, true); // broken
        assertThrows(GameException.class, () -> engine.rent(student, vios));
    }

    @Test
    @DisplayName("Hết ngày thuê: xe về bãi, bị bẩn; phí bãi 100.000 đ/xe")
    void carComesBack() {
        Customer student = carefulStudent(1);
        state.getCustomers().add(student);
        engine.rent(student, vios);
        DayReport report = engine.nextDay();
        assertEquals(CarStatus.FREE, vios.getStatus());
        assertFalse(vios.isClean());
        assertTrue(vios.getKm() > 0);
        assertEquals(97, vios.getCondition()); // 3% wear for one day
        assertEquals(100_000, report.costs());
        assertEquals(2, state.getDay());
        assertTrue(state.getRentals().isEmpty());
    }

    @Test
    @DisplayName("Khách không được phục vụ bỏ đi: mỗi khách -1 uy tín")
    void unservedCustomersLeave() {
        state.getCustomers().add(new FamilyCustomer("a", 1));
        state.getCustomers().add(new FamilyCustomer("b", 1));
        int before = state.getReputation();
        engine.nextDay();
        assertTrue(state.getReputation() <= before - 2 + 5); // a "referral" event could give +5
        assertFalse(state.getCustomers().isEmpty()); // new customers for the new day
    }

    @Test
    @DisplayName("Rửa xe 100.000 đ; sửa xe theo công thức; không rửa xe đã sạch")
    void washAndRepair() {
        assertThrows(GameException.class, () -> engine.wash(vios));
        vios.used(60, 0, false);
        engine.wash(vios);
        engine.repair(vios);
        assertTrue(vios.isClean());
        assertEquals(100, vios.getCondition());
        assertEquals(10_000_000 - 100_000 - 840_000, state.getMoney());
    }

    @Test
    @DisplayName("Mua xe: xe chưa mở khóa và không đủ tiền đều bị từ chối")
    void buy() {
        CarOffer transit = CarCatalog.OFFERS.get(3); // unlocks at level 2
        assertThrows(GameException.class, () -> engine.buy(transit));
        CarOffer innova = CarCatalog.OFFERS.get(2);
        Car bought = engine.buy(innova);
        assertTrue(bought instanceof Suv);
        assertEquals(2, state.getCars().size());
        GameException e = assertThrows(GameException.class, () -> engine.buy(innova)); // 0 đ left
        assertTrue(e.getMessage().startsWith("Không đủ tiền"));
    }

    @Test
    @DisplayName("Bán xe được tiền theo tình trạng, nhưng phải giữ lại 1 xe")
    void sell() {
        assertThrows(GameException.class, () -> engine.sell(vios));
        Car innova = new Suv("51A-222.22", "Toyota Innova").used(50, 0, true);
        state.getCars().add(innova);
        engine.sell(innova);
        assertEquals(13_000_000, state.getMoney()); // 10.000.000 x 50% x 0,6
    }

    @Test
    @DisplayName("Tiền âm cuối ngày thì phá sản, sau đó không chơi tiếp được")
    void bankrupt() {
        GameState poor = new GameState("Nghèo", 50_000);
        GameEngine e = new GameEngine(poor, new Random(1));
        poor.getCars().add(new Sedan("51A-333.33", "Toyota Vios"));
        DayReport report = e.nextDay();
        assertTrue(report.gameOver());
        assertTrue(poor.getGameOverReason().startsWith("Phá sản"));
        assertThrows(GameException.class, e::nextDay);
    }

    @Test
    @DisplayName("Ván chơi kết thúc sau ngày 30")
    void endsAfterDay30() {
        state.setDay(GameEngine.LAST_DAY);
        assertTrue(engine.nextDay().gameOver());
    }

    @Test
    @DisplayName("Lên cấp và thành tích được báo cho người nghe (Observer)")
    void listenersAreNotified() {
        List<Level> levels = new ArrayList<>();
        List<Achievement> achievements = new ArrayList<>();
        engine.addListener(new GameListener() {
            @Override public void onGameChanged(GameState s) { }
            @Override public void onLevelUp(Level level) { levels.add(level); }
            @Override public void onAchievement(Achievement a) { achievements.add(a); }
        });
        state.setTotalRevenue(14_500_000);
        Customer student = carefulStudent(1);
        state.getCustomers().add(student);
        engine.rent(student, vios); // +640.000 -> more than 15.000.000 earned
        assertEquals(List.of(Level.LV2), levels);
        assertTrue(achievements.contains(Achievement.FIRST_RENTAL));
    }

    @Test
    @DisplayName("Mỗi sáng có 1 khách 'nhử' của cấp sau, còn lại là khách của cấp hiện tại")
    void morningCustomers() {
        List<Customer> list = new CustomerFactory(new Random(3)).morning(Level.LV1, 4);
        assertEquals(4, list.size());
        assertEquals(1, list.stream().filter(c -> c.getTypeName().equals("Đoàn du lịch")).count());
    }
}
