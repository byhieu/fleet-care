package com.cartycoon.logic.event;

import com.cartycoon.model.GameState;

import java.util.Random;

/**
 * Something unexpected that may happen in the morning. Each event is its own class:
 * to add a new event, write a new class and add it to EventFactory, nothing else changes.
 */
public interface RandomEvent {

    String getTitle();

    /** Changes the game and returns the text shown to the player. */
    String apply(GameState state, Random random);
}
