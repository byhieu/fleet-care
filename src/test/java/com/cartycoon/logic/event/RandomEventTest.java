package com.cartycoon.logic.event;

import com.cartycoon.model.Car;
import com.cartycoon.model.CityCar;
import com.cartycoon.model.GameState;
import com.cartycoon.model.Sedan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomEventTest {
    private final Random random = new Random(1);
    private GameState state;
    private Car parked;
    private Car rented;

    @BeforeEach
    void setUp() {
        state = new GameState("Test", 1_000_000);
        parked = new CityCar("51A-1", "Kia Morning");
        rented = new Sedan("51A-2", "Toyota Vios");
        rented.rentOut();
        state.getCars().add(parked);
        state.getCars().add(rented);
    }

    @Test
    @DisplayName("Mưa bão: xe ở bãi bẩn, xe đang chạy không bị ảnh hưởng")
    void storm() {
        new StormEvent().apply(state, random);
        assertFalse(parked.isClean());
        assertTrue(rented.isClean());
    }

    @Test
    @DisplayName("Phạt nguội -500.000 đ, tiền điện nước -300.000 đ")
    void costs() {
        new FineEvent().apply(state, random);
        new BillEvent().apply(state, random);
        assertEquals(200_000, state.getMoney());
    }

    @Test
    @DisplayName("Trúng thưởng +2.000.000 đ nhưng không tính vào doanh thu lên cấp")
    void prize() {
        new FairPrizeEvent().apply(state, random);
        assertEquals(3_000_000, state.getMoney());
        assertEquals(0, state.getTotalRevenue());
    }

    @Test
    @DisplayName("Mùa du lịch +2 khách, khách giới thiệu +5 uy tín, xe ở bãi bị trầy -15%")
    void others() {
        new TouristSeasonEvent().apply(state, random);
        assertEquals(2, state.getExtraCustomersToday());
        new ReferralEvent().apply(state, random);
        assertEquals(55, state.getReputation());
        new ScratchEvent().apply(state, random);
        assertEquals(85, parked.getCondition());
        assertEquals(100, rented.getCondition());
    }

    @Test
    @DisplayName("Khoảng 30% số buổi sáng có sự kiện")
    void chance() {
        EventFactory factory = new EventFactory(new Random(42));
        int count = 0;
        for (int i = 0; i < 1000; i++) {
            if (factory.pick() != null) count++;
        }
        assertTrue(count > 250 && count < 350, "events: " + count);
    }
}
