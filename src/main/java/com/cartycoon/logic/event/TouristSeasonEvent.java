package com.cartycoon.logic.event;

import com.cartycoon.model.GameState;

import java.util.Random;

public class TouristSeasonEvent implements RandomEvent {
    public static final int EXTRA_CUSTOMERS = 2;

    @Override
    public String getTitle() {
        return "Mùa du lịch";
    }

    @Override
    public String apply(GameState state, Random random) {
        state.addExtraCustomersToday(EXTRA_CUSTOMERS);
        return "hôm nay có thêm 2 khách đến thuê xe.";
    }
}
