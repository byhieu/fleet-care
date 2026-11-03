package com.cartycoon.logic;

import com.cartycoon.model.Achievement;
import com.cartycoon.model.GameState;
import com.cartycoon.model.Level;

/** Observer: the screen listens to the engine and redraws itself. The engine does not know the screen exists. */
public interface GameListener {

    /** Something changed (money, cars, customers...). */
    void onGameChanged(GameState state);

    default void onLevelUp(Level level) {
    }

    default void onAchievement(Achievement achievement) {
    }
}
