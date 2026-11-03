package com.cartycoon.logic;

/** A move that is not allowed (not enough money, car is broken...). The screen shows the message to the player. */
public class GameException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
