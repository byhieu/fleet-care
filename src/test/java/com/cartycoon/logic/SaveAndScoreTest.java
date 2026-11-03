package com.cartycoon.logic;

import com.cartycoon.model.GameState;
import com.cartycoon.model.Sedan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SaveAndScoreTest {

    @Test
    @DisplayName("Lưu game ra file rồi đọc lại được đúng như cũ")
    void saveAndLoad(@TempDir Path dir) {
        GameEngine engine = GameEngine.newGame("An", new Random(5));
        engine.nextDay();
        SaveManager saves = new SaveManager(dir.resolve("save.dat"));
        assertFalse(saves.hasSave());
        saves.save(engine.getState());
        GameState loaded = saves.load();
        assertEquals("An", loaded.getPlayerName());
        assertEquals(2, loaded.getDay());
        assertEquals(engine.getState().getMoney(), loaded.getMoney());
        assertEquals(engine.getState().getCars().size(), loaded.getCars().size());
        saves.delete();
        assertFalse(saves.hasSave());
    }

    @Test
    @DisplayName("Điểm = tiền mặt + giá bán xe + uy tín x 100.000 đ")
    void score() {
        GameState s = new GameState("An", 10_000_000);
        s.getCars().add(new Sedan("51A-1", "Toyota Vios")); // sells for 4.200.000
        assertEquals(10_000_000 + 4_200_000 + 5_000_000, ScoreBoard.score(s));
    }

    @Test
    @DisplayName("Xếp hạng S / A / B / C")
    void ranks() {
        assertEquals("S", ScoreBoard.rank(100_000_000));
        assertEquals("A", ScoreBoard.rank(70_000_000));
        assertEquals("B", ScoreBoard.rank(45_000_000));
        assertEquals("C", ScoreBoard.rank(44_999_999));
    }

    @Test
    @DisplayName("Bảng xếp hạng giữ 5 điểm cao nhất, xếp giảm dần")
    void highScores(@TempDir Path dir) {
        ScoreBoard board = new ScoreBoard(dir.resolve("highscores.txt"));
        for (int i = 1; i <= 7; i++) board.add("Người " + i, i * 10_000_000L, LocalDate.of(2026, 10, i));
        List<ScoreBoard.HighScore> top = board.load();
        assertEquals(5, top.size());
        assertEquals("Người 7", top.get(0).name());
        assertEquals(30_000_000, top.get(4).score());
    }
}
