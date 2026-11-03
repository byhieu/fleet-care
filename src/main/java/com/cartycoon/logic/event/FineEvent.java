package com.cartycoon.logic.event;

import com.cartycoon.model.GameState;

import java.util.Random;

public class FineEvent implements RandomEvent {
    public static final long AMOUNT = 500_000;

    @Override
    public String getTitle() {
        return "Phạt nguội";
    }

    @Override
    public String apply(GameState state, Random random) {
        state.charge(AMOUNT);
        return "một khách cũ chạy quá tốc độ, cửa hàng phải nộp phạt 500.000 đ.";
    }
}
