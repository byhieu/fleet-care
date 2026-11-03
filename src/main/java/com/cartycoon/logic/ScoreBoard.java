package com.cartycoon.logic;

import com.cartycoon.model.Car;
import com.cartycoon.model.GameState;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Final score, rank, and the top 5 table saved in a small text file (one line per score: name;score;rank;date). */
public class ScoreBoard {
    public static final int TOP = 5;

    public record HighScore(String name, long score, String rank, LocalDate date) {
    }

    private final Path file;

    public ScoreBoard(Path file) {
        this.file = file;
    }

    /** Score = cash + what the cars could be sold for + reputation x 100.000 đ. */
    public static long score(GameState state) {
        long cars = state.getCars().stream().mapToLong(Car::getSellPrice).sum();
        return state.getMoney() + cars + state.getReputation() * 100_000L;
    }

    public static String rank(long score) {
        if (score >= 100_000_000) return "S";
        if (score >= 70_000_000) return "A";
        if (score >= 45_000_000) return "B";
        return "C";
    }

    public static String rankText(String rank) {
        return switch (rank) {
            case "S" -> "Ông trùm thực thụ!";
            case "A" -> "Chủ hãng xe giỏi";
            case "B" -> "Làm ăn khá đấy";
            default -> "Cố gắng ván sau nhé";
        };
    }

    public List<HighScore> load() {
        List<HighScore> list = new ArrayList<>();
        if (!Files.exists(file)) return list;
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] parts = line.split(";");
                if (parts.length != 4) continue;
                try {
                    list.add(new HighScore(parts[0], Long.parseLong(parts[1]), parts[2], LocalDate.parse(parts[3])));
                } catch (RuntimeException ignored) {
                    // a broken line is skipped
                }
            }
        } catch (IOException e) {
            throw new GameException("Không đọc được bảng xếp hạng: " + e.getMessage(), e);
        }
        return list;
    }

    /** Adds a score, keeps the best 5 and saves the file. Returns the new table. */
    public List<HighScore> add(String name, long score, LocalDate date) {
        List<HighScore> list = load();
        String clean = name == null || name.isBlank() ? "Người chơi" : name.replace(";", " ").trim();
        list.add(new HighScore(clean, score, rank(score), date));
        list.sort(Comparator.comparingLong(HighScore::score).reversed());
        List<HighScore> top = new ArrayList<>(list.subList(0, Math.min(TOP, list.size())));
        List<String> lines = top.stream().map(h -> h.name() + ";" + h.score() + ";" + h.rank() + ";" + h.date()).toList();
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GameException("Không lưu được bảng xếp hạng: " + e.getMessage(), e);
        }
        return top;
    }
}
