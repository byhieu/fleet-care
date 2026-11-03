package com.cartycoon.logic.event;

import com.cartycoon.model.GameState;

import java.util.Random;

public class ReferralEvent implements RandomEvent {
    public static final int BONUS = 5;

    @Override
    public String getTitle() {
        return "Khách giới thiệu";
    }

    @Override
    public String apply(GameState state, Random random) {
        state.changeReputation(BONUS);
        return "một khách quen khen cửa hàng trên mạng: uy tín +5.";
    }
}
