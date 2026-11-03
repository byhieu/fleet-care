package com.cartycoon.logic;

import com.cartycoon.model.Car;
import com.cartycoon.model.CityCar;
import com.cartycoon.model.Level;
import com.cartycoon.model.LuxuryCar;
import com.cartycoon.model.Sedan;
import com.cartycoon.model.SportsCar;
import com.cartycoon.model.Suv;
import com.cartycoon.model.Van;

import java.util.List;
import java.util.function.BiFunction;

/** The car shop. Factory: each offer knows how to build its car. Better cars unlock at higher levels. */
public final class CarCatalog {

    public record CarOffer(String model, int minLevel, BiFunction<String, String, Car> maker) {

        public Car create(String plate) {
            return maker.apply(plate, model);
        }

        /** A car that is not in the game, only used to show the prices in the shop. */
        public Car preview() {
            return create("-");
        }

        public boolean isUnlocked(Level level) {
            return level.getNumber() >= minLevel;
        }
    }

    public static final List<CarOffer> OFFERS = List.of(
            new CarOffer("Kia Morning", 1, CityCar::new),
            new CarOffer("Toyota Vios", 1, Sedan::new),
            new CarOffer("Toyota Innova", 1, Suv::new),
            new CarOffer("Ford Transit", 2, Van::new),
            new CarOffer("Mazda MX-5", 3, SportsCar::new),
            new CarOffer("Mercedes S450", 4, LuxuryCar::new));

    private CarCatalog() {
    }
}
