package com.cartycoon.logic.event;

import com.cartycoon.model.Car;
import com.cartycoon.model.CarStatus;
import com.cartycoon.model.GameState;

import java.util.Random;

/** All cars parked at the shop get dirty. */
public class StormEvent implements RandomEvent {

    @Override
    public String getTitle() {
        return "Mưa bão";
    }

    @Override
    public String apply(GameState state, Random random) {
        int count = 0;
        for (Car car : state.getCars()) {
            if (car.getStatus() != CarStatus.RENTED && car.isClean()) {
                car.makeDirty();
                count++;
            }
        }
        return count == 0 ? "may quá, không có xe sạch nào ở bãi." : count + " xe ở bãi bị bẩn hết.";
    }
}
