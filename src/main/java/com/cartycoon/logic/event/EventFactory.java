package com.cartycoon.logic.event;

import java.util.List;
import java.util.Random;

/** Each morning there is a 30% chance that one random event happens. */
public class EventFactory {
    public static final int CHANCE_PERCENT = 30;

    private final List<RandomEvent> events = List.of(
            new StormEvent(), new FineEvent(), new FairPrizeEvent(), new TouristSeasonEvent(),
            new ScratchEvent(), new ReferralEvent(), new BillEvent());
    private final Random random;

    public EventFactory(Random random) {
        this.random = random;
    }

    /** An event, or null when nothing special happens today. */
    public RandomEvent pick() {
        if (random.nextInt(100) >= CHANCE_PERCENT) return null;
        return events.get(random.nextInt(events.size()));
    }

    public List<RandomEvent> all() {
        return events;
    }
}
