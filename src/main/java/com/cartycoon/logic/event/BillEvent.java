package com.cartycoon.logic.event;

import com.cartycoon.model.GameState;

import java.util.Random;

public class BillEvent implements RandomEvent {
    public static final long AMOUNT = 300_000;

    @Override
    public String getTitle() {
        return "Tiền điện nước";
    }

    @Override
    public String apply(GameState state, Random random) {
        state.charge(AMOUNT);
        return "hóa đơn điện nước của bãi xe tăng: -300.000 đ.";
    }
}
