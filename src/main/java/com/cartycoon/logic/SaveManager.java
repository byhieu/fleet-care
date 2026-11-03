package com.cartycoon.logic;

import com.cartycoon.model.GameState;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Saves the whole GameState object into one file with Java serialization, and reads it back. */
public class SaveManager {
    private final Path file;

    public SaveManager(Path file) {
        this.file = file;
    }

    public boolean hasSave() {
        return Files.exists(file);
    }

    public void save(GameState state) {
        try (OutputStream out = Files.newOutputStream(file); ObjectOutputStream objects = new ObjectOutputStream(out)) {
            objects.writeObject(state);
        } catch (IOException e) {
            throw new GameException("Không lưu được game: " + e.getMessage(), e);
        }
    }

    public GameState load() {
        try (InputStream in = Files.newInputStream(file); ObjectInputStream objects = new ObjectInputStream(in)) {
            return (GameState) objects.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new GameException("File lưu bị hỏng hoặc của phiên bản cũ, hãy chơi ván mới.", e);
        }
    }

    /** A finished game cannot be continued, so its save file is removed. */
    public void delete() {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new GameException("Không xóa được file lưu: " + e.getMessage(), e);
        }
    }
}
