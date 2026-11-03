package com.cartycoon.logic.event;

import com.cartycoon.model.GameState;

import java.util.Random;

public class FairPrizeEvent implements RandomEvent {
    public static final long AMOUNT = 2_000_000;

    @Override
    public String getTitle() {
        return "Trúng thưởng";
    }

    @Override
    public String apply(GameState state, Random random) {
        state.receive(AMOUNT);
        return "cửa hàng trúng giải hội chợ ô tô: +2.000.000 đ!";
    }
}
