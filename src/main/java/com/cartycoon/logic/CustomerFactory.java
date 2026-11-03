package com.cartycoon.logic;

import com.cartycoon.model.BossCustomer;
import com.cartycoon.model.Customer;
import com.cartycoon.model.FamilyCustomer;
import com.cartycoon.model.Level;
import com.cartycoon.model.RacerCustomer;
import com.cartycoon.model.StudentCustomer;
import com.cartycoon.model.TouristCustomer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

/** Factory: creates the random customers of each morning. */
public class CustomerFactory {
    private static final String[] NAMES = {
            "Anh Minh", "Chị Lan", "Bác Hùng", "Em Vy", "Anh Tuấn", "Chị Hoa", "Cô Mai", "Anh Khoa", "Chị Thảo",
            "Anh Long", "Em Nam", "Chú Sơn", "Chị Ngọc", "Anh Phát", "Em Linh", "Anh Bảo", "Cô Hạnh", "Anh Dũng"};

    /** One kind of customer: from which level it comes, how to create it, how many days it rents. */
    private record Kind(int level, BiFunction<String, Integer, Customer> maker, int minDays, int maxDays) {
    }

    private static final List<Kind> KINDS = List.of(
            new Kind(1, StudentCustomer::new, 1, 2),
            new Kind(1, FamilyCustomer::new, 1, 3),
            new Kind(2, TouristCustomer::new, 2, 4),
            new Kind(3, RacerCustomer::new, 1, 2),
            new Kind(3, BossCustomer::new, 1, 3));

    private final Random random;

    public CustomerFactory(Random random) {
        this.random = random;
    }

    /**
     * The customers of one morning: mostly kinds the player can already serve,
     * plus one "teaser" from the next level to make the player want to grow.
     */
    public List<Customer> morning(Level level, int count) {
        List<Kind> unlocked = KINDS.stream().filter(k -> k.level() <= level.getNumber()).toList();
        List<Kind> teasers = KINDS.stream().filter(k -> k.level() == level.getNumber() + 1).toList();
        List<Customer> result = new ArrayList<>();
        if (!teasers.isEmpty() && count >= 2) {
            result.add(create(teasers.get(random.nextInt(teasers.size()))));
        }
        while (result.size() < count) {
            result.add(create(unlocked.get(random.nextInt(unlocked.size()))));
        }
        Collections.shuffle(result, random);
        return result;
    }

    /** Names of the customer types that start to come at this level, e.g. ["Đoàn du lịch"]. */
    public static List<String> newTypesAt(Level level) {
        return KINDS.stream().filter(k -> k.level() == level.getNumber())
                .map(k -> k.maker().apply("", 1).getTypeName()).toList();
    }

    private Customer create(Kind kind) {
        String name = NAMES[random.nextInt(NAMES.length)];
        int days = kind.minDays() + random.nextInt(kind.maxDays() - kind.minDays() + 1);
        return kind.maker().apply(name, days);
    }
}
