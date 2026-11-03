package com.cartycoon.logic;

import com.cartycoon.logic.CarCatalog.CarOffer;
import com.cartycoon.logic.event.EventFactory;
import com.cartycoon.logic.event.RandomEvent;
import com.cartycoon.model.Achievement;
import com.cartycoon.model.Car;
import com.cartycoon.model.CarStatus;
import com.cartycoon.model.CityCar;
import com.cartycoon.model.Customer;
import com.cartycoon.model.GameState;
import com.cartycoon.model.Level;
import com.cartycoon.model.Rental;
import com.cartycoon.model.Sedan;
import com.cartycoon.model.SportsCar;
import com.cartycoon.model.Suv;
import com.cartycoon.model.Van;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.cartycoon.util.Format.money;

/**
 * All the rules of the game. The screen only calls these methods; it never changes the GameState itself.
 * A move that is not allowed throws a GameException with a message for the player.
 */
public class GameEngine {
    public static final int LAST_DAY = 30;
    public static final long START_MONEY = 15_000_000;
    public static final long WASH_PRICE = 100_000;
    public static final long GARAGE_FEE_PER_CAR = 100_000;
    public static final int MAX_CUSTOMERS_PER_DAY = 8;
    public static final int DAMAGE_ON_ACCIDENT = 20;

    private final GameState state;
    private final Random random;
    private final CustomerFactory customerFactory;
    private final EventFactory eventFactory;
    private final List<GameListener> listeners = new ArrayList<>();

    /** The Random is given from outside so the tests can use a fixed seed and always get the same game. */
    public GameEngine(GameState state, Random random) {
        this.state = state;
        this.random = random;
        this.customerFactory = new CustomerFactory(random);
        this.eventFactory = new EventFactory(random);
    }

    /** Day 1: 15.000.000 đ and two old cars. */
    public static GameEngine newGame(String playerName, Random random) {
        GameEngine engine = new GameEngine(new GameState(playerName, START_MONEY), random);
        GameState s = engine.state;
        s.getCars().add(new CityCar(engine.newPlate(), "Kia Morning").used(85, 42_000, true));
        s.getCars().add(new Sedan(engine.newPlate(), "Toyota Vios").used(75, 61_000, false));
        s.addLog("Chào mừng " + playerName + "! Bạn có " + LAST_DAY + " ngày để trở thành ông trùm cho thuê xe.");
        engine.startMorning(new ArrayList<>(), false);
        return engine;
    }

    /** For the class presentation: starts at day 25, level 4, with a bigger garage. */
    public static GameEngine demoGame(String playerName, Random random) {
        GameEngine engine = new GameEngine(new GameState(playerName, 45_000_000), random);
        GameState s = engine.state;
        s.setDay(25);
        s.setTotalRevenue(85_000_000);
        s.setLevelReached(s.getLevel().getNumber());
        s.changeReputation(25);
        s.getCars().add(new CityCar(engine.newPlate(), "Kia Morning").used(70, 80_000, true));
        s.getCars().add(new Sedan(engine.newPlate(), "Toyota Vios").used(90, 70_500, true));
        s.getCars().add(new Suv(engine.newPlate(), "Toyota Innova").used(60, 51_000, false));
        s.getCars().add(new Van(engine.newPlate(), "Ford Transit").used(45, 33_000, true));
        s.getCars().add(new SportsCar(engine.newPlate(), "Mazda MX-5").used(25, 12_000, false));
        for (Achievement a : Achievement.values()) {
            if (a.isReached(s)) s.getAchievements().add(a); // no popups for what the demo already has
        }
        s.addLog("Ván demo: bắt đầu từ ngày 25 với 5 xe. Hãy kết thúc thật đẹp!");
        engine.startMorning(new ArrayList<>(), false);
        return engine;
    }

    public GameState getState() {
        return state;
    }

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    // ------------------------------------------------------------------ player actions

    public void rent(Customer customer, Car car) {
        ensurePlaying();
        if (customer == null || car == null) throw new GameException("Hãy chọn một khách (bên trái) và một xe (bên phải).");
        if (!state.getCustomers().contains(customer)) throw new GameException("Khách này không còn chờ nữa.");
        if (car.getStatus() == CarStatus.RENTED) throw new GameException(car + " đang được thuê.");
        if (car.getStatus() == CarStatus.BROKEN) throw new GameException(car + " đang hỏng, hãy sửa trước.");
        if (!customer.accepts(car)) {
            throw new GameException(customer.getTypeName() + " không chịu xe này.\nYêu cầu: " + customer.getRequest());
        }
        long price = customer.price(car);
        state.getCustomers().remove(customer);
        state.getRentals().add(new Rental(customer, car, price, customer.isHappyWith(car)));
        car.rentOut();
        state.earn(price);
        state.countRental();
        state.addLog("Cho " + customer + " thuê " + car + " " + customer.getDays() + " ngày: +" + money(price));
        changed();
    }

    public void wash(Car car) {
        ensurePlaying();
        requireParked(car);
        if (car.isClean()) throw new GameException(car + " đã sạch rồi.");
        pay(WASH_PRICE);
        car.wash();
        state.addLog("Rửa " + car + ": -" + money(WASH_PRICE));
        changed();
    }

    public void repair(Car car) {
        ensurePlaying();
        requireParked(car);
        if (car.getCondition() >= 100) throw new GameException(car + " còn mới, không cần sửa.");
        long cost = car.getRepairCost();
        pay(cost);
        car.repair();
        state.addLog("Sửa " + car + " lên 100%: -" + money(cost));
        changed();
    }

    public Car buy(CarOffer offer) {
        ensurePlaying();
        if (!offer.isUnlocked(state.getLevel())) {
            throw new GameException(offer.model() + " sẽ mở khóa ở cấp " + offer.minLevel() + ".");
        }
        Car car = offer.create(newPlate());
        pay(car.getBuyPrice());
        state.getCars().add(car);
        state.addLog("Mua " + car + ": -" + money(car.getBuyPrice()));
        changed();
        return car;
    }

    public void sell(Car car) {
        ensurePlaying();
        requireParked(car);
        if (state.getCars().size() == 1) throw new GameException("Phải giữ lại ít nhất 1 chiếc xe.");
        long price = car.getSellPrice();
        state.getCars().remove(car);
        state.receive(price);
        state.addLog("Bán " + car + ": +" + money(price));
        changed();
    }

    // ------------------------------------------------------------------ end of the day

    /**
     * Rented cars drive one day, finished rentals come back, waiting customers leave, the garage fee is paid.
     * Then either the game ends (bankrupt or day 30) or a new morning starts.
     */
    public DayReport nextDay() {
        ensurePlaying();
        int day = state.getDay();
        List<String> lines = new ArrayList<>();
        returnCars(lines);

        int waiting = state.getCustomers().size();
        if (waiting > 0) {
            state.changeReputation(-waiting);
            note(lines, waiting + " khách không được phục vụ đã bỏ đi (uy tín -" + waiting + ").");
            state.getCustomers().clear();
        }
        long fee = GARAGE_FEE_PER_CAR * state.getCars().size();
        state.charge(fee);
        note(lines, "Phí bãi xe cho " + state.getCars().size() + " xe: -" + money(fee) + ".");

        long income = state.getIncomeToday();
        long costs = state.getCostToday();
        if (state.getMoney() < 0) {
            state.endGame("Phá sản! Tiền bị âm vào cuối ngày " + day + ".");
            note(lines, state.getGameOverReason());
        } else if (day >= LAST_DAY) {
            state.endGame("Đã hết " + LAST_DAY + " ngày! Cùng xem bạn đạt hạng gì.");
            note(lines, state.getGameOverReason());
        } else {
            state.startNewDay();
            startMorning(lines, true);
        }
        changed();
        return new DayReport(day, lines, income, costs, state.isGameOver());
    }

    private void returnCars(List<String> lines) {
        Iterator<Rental> it = state.getRentals().iterator();
        while (it.hasNext()) {
            Rental rental = it.next();
            Car car = rental.getCar();
            Customer customer = rental.getCustomer();
            car.drive(80 + random.nextInt(171));
            car.wear(car.getWearPerDay() * customer.getWearMultiplier());
            if (!rental.passDay()) continue;

            it.remove();
            boolean accident = random.nextInt(100) < customer.getDamageChance();
            if (accident) car.wear(DAMAGE_ON_ACCIDENT);
            car.giveBack();
            StringBuilder text = new StringBuilder(customer + " trả " + car);
            if (accident) text.append(", xe bị va quẹt (-" + DAMAGE_ON_ACCIDENT + "%)");
            if (car.getStatus() == CarStatus.BROKEN) text.append(", xe HỎNG cần sửa");
            if (rental.isHappy()) {
                state.changeReputation(3);
                state.countHappyCustomer();
                long tip = customer.getTip(rental.getPrice());
                if (tip > 0) {
                    state.earn(tip);
                    text.append(". Khách rất vui, bo thêm ").append(money(tip));
                } else {
                    text.append(". Khách hài lòng (uy tín +3)");
                }
            } else {
                state.changeReputation(-5);
                text.append(". Khách chê xe bẩn hoặc cũ (uy tín -5)");
            }
            note(lines, text + ".");
        }
    }

    /** Morning: maybe a random event, then new customers arrive. */
    private void startMorning(List<String> lines, boolean withEvent) {
        if (withEvent) {
            RandomEvent event = eventFactory.pick();
            if (event != null) note(lines, "Sự kiện \"" + event.getTitle() + "\": " + event.apply(state, random));
        }
        int count = Math.min(MAX_CUSTOMERS_PER_DAY, 3 + state.getLevel().getNumber()
                + (state.getReputation() >= 80 ? 1 : 0) + state.getExtraCustomersToday());
        state.getCustomers().addAll(customerFactory.morning(state.getLevel(), count));
        note(lines, "Sáng ngày " + state.getDay() + ": " + count + " khách đang chờ thuê xe.");
    }

    // ------------------------------------------------------------------ helpers

    private void note(List<String> lines, String text) {
        lines.add(text);
        state.addLog(text);
    }

    private void pay(long amount) {
        if (amount > state.getMoney()) {
            throw new GameException("Không đủ tiền! Cần " + money(amount) + ", bạn chỉ có " + money(state.getMoney()) + ".");
        }
        state.spend(amount);
    }

    private void requireParked(Car car) {
        if (car == null) throw new GameException("Hãy chọn một xe trong garage.");
        if (car.getStatus() == CarStatus.RENTED) throw new GameException(car + " đang được khách thuê.");
    }

    private void ensurePlaying() {
        if (state.isGameOver()) throw new GameException("Ván chơi đã kết thúc. Hãy bắt đầu ván mới.");
    }

    /** A new plate like 51A-123.45 that no car of the shop has yet. */
    private String newPlate() {
        String plate;
        do {
            plate = String.format("51%c-%03d.%02d", "ABCDEFGHK".charAt(random.nextInt(9)), random.nextInt(1000), random.nextInt(100));
        } while (hasPlate(plate));
        return plate;
    }

    private boolean hasPlate(String plate) {
        return state.getCars().stream().anyMatch(c -> c.getPlate().equals(plate));
    }

    /** Checks level-ups and achievements, then tells the listeners (Observer). */
    private void changed() {
        Level level = state.getLevel();
        if (level.getNumber() > state.getLevelReached()) {
            state.setLevelReached(level.getNumber());
            state.addLog("LÊN CẤP: " + level);
            listeners.forEach(l -> l.onLevelUp(level));
        }
        for (Achievement a : Achievement.values()) {
            if (!state.getAchievements().contains(a) && a.isReached(state)) {
                state.getAchievements().add(a);
                state.addLog("Thành tích mới: " + a);
                listeners.forEach(l -> l.onAchievement(a));
            }
        }
        listeners.forEach(l -> l.onGameChanged(state));
    }
}
