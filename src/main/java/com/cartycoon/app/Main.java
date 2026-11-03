package com.cartycoon.app;

import com.cartycoon.logic.SaveManager;
import com.cartycoon.logic.ScoreBoard;
import com.cartycoon.ui.StartWindow;
import com.cartycoon.ui.UiUtil;

import javax.swing.SwingUtilities;
import java.nio.file.Path;

/** Starts Car Tycoon. The game is saved in save.dat, the top 5 in highscores.txt (next to the program). */
public class Main {

    public static void main(String[] args) {
        SaveManager saves = new SaveManager(Path.of("save.dat"));
        ScoreBoard scoreBoard = new ScoreBoard(Path.of("highscores.txt"));
        SwingUtilities.invokeLater(() -> {
            UiUtil.setupLookAndFeel();
            new StartWindow(saves, scoreBoard).setVisible(true);
        });
    }
}
