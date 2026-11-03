package com.cartycoon.logic;

import java.util.List;

/** What happened when the player pressed "Ngày tiếp theo", shown in the end-of-day popup. */
public record DayReport(int day, List<String> lines, long income, long costs, boolean gameOver) {
}
