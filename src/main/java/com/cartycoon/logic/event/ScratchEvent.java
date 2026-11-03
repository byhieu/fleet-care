package com.cartycoon.logic.event;

import com.cartycoon.model.Car;
import com.cartycoon.model.CarStatus;
import com.cartycoon.model.GameState;

import java.util.List;
import java.util.Random;

/** A parked car gets scratched: -15% condition. */
public class ScratchEvent implements RandomEvent {
    public static final int DAMAGE = 15;

    @Override
    public String getTitle() {
        return "Xe bị trầy";
    }

    @Override
    public String apply(GameState state, Random random) {
        List<Car> parked = state.getCars().stream().filter(c -> c.getStatus() != CarStatus.RENTED).toList();
        if (parked.isEmpty()) return "may quá, xe nào cũng đang chạy ngoài đường.";
        Car car = parked.get(random.nextInt(parked.size()));
        car.wear(DAMAGE);
        return car + " bị trầy khi đậu ở bãi (-15% tình trạng).";
    }
}
