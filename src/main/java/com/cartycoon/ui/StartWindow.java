package com.cartycoon.ui;

import com.cartycoon.logic.GameEngine;
import com.cartycoon.logic.SaveManager;
import com.cartycoon.logic.ScoreBoard;
import com.cartycoon.model.GameState;
import com.cartycoon.util.Format;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

/** First screen: dark navy & gold theme, animated cartoon car with full wheels, card-style continue. */
public class StartWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    private final transient SaveManager saves;
    private final transient ScoreBoard scoreBoard;
    private final JTextField nameField = new JTextField("Người chơi", 14);

    public StartWindow(SaveManager saves, ScoreBoard scoreBoard) {
        super("Car Tycoon - Ông trùm cho thuê xe");
        this.saves = saves;
        this.scoreBoard = scoreBoard;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(UiUtil.NAVY_BG);

        // Header Title & Animated Car
        JLabel title = new JLabel("CAR TYCOON", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 42f));
        title.setForeground(UiUtil.GOLD);

        JLabel subtitle = new JLabel("Ông trùm cho thuê xe - 30 ngày để làm giàu!", SwingConstants.CENTER);
        subtitle.setFont(subtitle.getFont().deriveFont(15f));
        subtitle.setForeground(UiUtil.TEXT_MUTED);

        JPanel head = new JPanel(new BorderLayout(6, 6));
        head.setOpaque(false);
        head.add(title, BorderLayout.NORTH);
        head.add(new CarPicture(), BorderLayout.CENTER);
        head.add(subtitle, BorderLayout.SOUTH);

        // Player Name Input Panel
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        namePanel.setOpaque(false);
        JLabel namePrompt = new JLabel("Tên của bạn:");
        namePrompt.setForeground(UiUtil.TEXT_PRIMARY);
        namePrompt.setFont(namePrompt.getFont().deriveFont(Font.BOLD, 14f));
        nameField.setBackground(UiUtil.NAVY_CARD);
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(UiUtil.GOLD);
        nameField.setFont(nameField.getFont().deriveFont(Font.BOLD, 14f));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiUtil.NAVY_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        namePanel.add(namePrompt);
        namePanel.add(nameField);

        // Card-style Continue Button / Slot
        JPanel continueCard = buildContinueCard();

        // Main Actions Menu Buttons
        JPanel actionButtons = new JPanel(new GridLayout(0, 1, 8, 8));
        actionButtons.setOpaque(false);
        actionButtons.add(UiUtil.button("⭐ Ván mới", UiUtil.GOLD, () -> start(GameEngine.newGame(playerName(), new Random()))));
        actionButtons.add(UiUtil.button("🕹️ Ván demo (bắt đầu từ ngày 25)", null, () -> start(GameEngine.demoGame(playerName(), new Random()))));
        actionButtons.add(UiUtil.button("🏆 Bảng xếp hạng", null, this::showHighScores));
        actionButtons.add(UiUtil.button("📖 Hướng dẫn chơi", null, this::showHelp));
        actionButtons.add(UiUtil.button("Thoát", null, () -> System.exit(0)));

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(6, 36, 6, 36));
        centerPanel.add(namePanel, BorderLayout.NORTH);

        JPanel cardsAndButtons = new JPanel(new BorderLayout(10, 12));
        cardsAndButtons.setOpaque(false);
        cardsAndButtons.add(continueCard, BorderLayout.NORTH);
        cardsAndButtons.add(actionButtons, BorderLayout.CENTER);
        centerPanel.add(cardsAndButtons, BorderLayout.CENTER);

        JPanel root = new JPanel(new BorderLayout(10, 14));
        root.setBackground(UiUtil.NAVY_BG);
        root.setBorder(BorderFactory.createEmptyBorder(18, 22, 20, 22));
        root.add(head, BorderLayout.NORTH);
        root.add(centerPanel, BorderLayout.CENTER);

        setContentPane(root);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /** Builds an interactive card-styled Continue component. */
    private JPanel buildContinueCard() {
        if (!saves.hasSave()) {
            JPanel card = UiUtil.card(new Color(20, 28, 48), UiUtil.NAVY_BORDER);
            card.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 10));
            JLabel lbl = new JLabel("Chưa có ván chơi dở • Bấm \"Ván mới\" để bắt đầu!");
            lbl.setForeground(new Color(110, 125, 150));
            lbl.setFont(lbl.getFont().deriveFont(13f));
            card.add(lbl);
            return card;
        }

        GameState saved;
        try {
            saved = saves.load();
        } catch (RuntimeException e) {
            JPanel card = UiUtil.card(new Color(40, 20, 25), UiUtil.NAVY_BORDER);
            card.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 10));
            JLabel lbl = new JLabel("⚠️ File lưu bị hỏng hoặc không tương thích");
            lbl.setForeground(new Color(248, 113, 113));
            card.add(lbl);
            return card;
        }

        JPanel card = UiUtil.card(UiUtil.NAVY_CARD, UiUtil.GOLD);
        card.setLayout(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Header line: title + badge
        JPanel headerLine = new JPanel(new BorderLayout());
        headerLine.setOpaque(false);

        JLabel cardTitle = new JLabel("🎮 TIẾP TỤC VÁN ĐANG CHƠI");
        cardTitle.setFont(cardTitle.getFont().deriveFont(Font.BOLD, 14f));
        cardTitle.setForeground(UiUtil.GOLD);

        JLabel badge = new JLabel(" ▶ VÀO GAME ");
        badge.setOpaque(true);
        badge.setBackground(new Color(22, 101, 52));
        badge.setForeground(Color.WHITE);
        badge.setFont(badge.getFont().deriveFont(Font.BOLD, 11f));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 197, 94), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));

        headerLine.add(cardTitle, BorderLayout.WEST);
        headerLine.add(badge, BorderLayout.EAST);

        // Details row (Name, Day, Money, Reputation)
        JPanel detailsRow = new JPanel(new GridLayout(1, 4, 8, 4));
        detailsRow.setOpaque(false);

        JLabel pName = new JLabel("👤 " + saved.getPlayerName());
        pName.setForeground(UiUtil.TEXT_PRIMARY);

        JLabel pDay = new JLabel("📅 Ngày " + saved.getDay() + "/30");
        pDay.setForeground(UiUtil.TEXT_PRIMARY);

        JLabel pMoney = new JLabel("💰 " + Format.money(saved.getMoney()));
        pMoney.setForeground(new Color(74, 222, 128));
        pMoney.setFont(pMoney.getFont().deriveFont(Font.BOLD));

        JLabel pRep = new JLabel("⭐ " + UiUtil.stars(saved.getStars()));
        pRep.setForeground(UiUtil.TEXT_GOLD);

        detailsRow.add(pName);
        detailsRow.add(pDay);
        detailsRow.add(pMoney);
        detailsRow.add(pRep);

        card.add(headerLine, BorderLayout.NORTH);
        card.add(detailsRow, BorderLayout.CENTER);

        // Hover & Click interaction
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                continueGame();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(UiUtil.NAVY_HOVER);
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(UiUtil.NAVY_CARD);
                card.repaint();
            }
        });

        return card;
    }

    private String playerName() {
        String name = nameField.getText().trim();
        return name.isEmpty() ? "Người chơi" : name;
    }

    private void start(GameEngine engine) {
        if (saves.hasSave() && !UiUtil.confirm(this, "Bắt đầu ván mới sẽ xóa ván đang chơi dở. Tiếp tục?")) return;
        open(engine);
    }

    private void continueGame() {
        UiUtil.run(this, () -> open(new GameEngine(saves.load(), new Random())));
    }

    private void open(GameEngine engine) {
        saves.save(engine.getState());
        dispose();
        new MainWindow(engine, saves, scoreBoard).setVisible(true);
    }

    private void showHighScores() {
        UiUtil.showHighScoresDialog(this, scoreBoard.load());
    }

    private void showHelp() {
        UiUtil.showHelpDialog(this);
    }

    /** Fully visible cartoon sports car with spinning wheels and animated road lines. */
    private static class CarPicture extends JComponent {
        private static final long serialVersionUID = 1L;
        private double wheelAngle = 0;
        private int roadOffset = 0;
        private final transient Timer timer;

        CarPicture() {
            setPreferredSize(new Dimension(360, 135));
            // Subtle smooth animation: road marks move and wheel spokes rotate
            timer = new Timer(35, e -> {
                wheelAngle += 0.16;
                roadOffset = (roadOffset + 3) % 40;
                repaint();
            });
            timer.start();
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            if (timer != null) timer.stop();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int carWidth = 240;
            int x = (getWidth() - carWidth) / 2;
            int y = 14;

            // Road track with animated dashed markings
            int roadY = y + 96;
            g.setColor(new Color(25, 35, 58));
            g.fillRect(0, roadY, getWidth(), 24);

            g.setColor(new Color(245, 180, 20, 190));
            g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int rx = -40 + roadOffset; rx < getWidth() + 40; rx += 40) {
                g.drawLine(rx, roadY + 8, rx + 20, roadY + 8);
            }

            g.setColor(new Color(55, 75, 115));
            g.drawLine(0, roadY, getWidth(), roadY);

            // Car shadow
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRoundRect(x + 10, roadY - 3, 220, 8, 8, 8);

            // Car Body: Red sporty gradient
            GradientPaint bodyGrad = new GradientPaint(x, y + 25, new Color(230, 57, 70), x, y + 75, new Color(170, 20, 35));
            g.setPaint(bodyGrad);
            g.fillRoundRect(x, y + 32, 235, 42, 22, 22);

            // Cabin / Roof
            GradientPaint roofGrad = new GradientPaint(x + 50, y + 4, new Color(200, 40, 55), x + 50, y + 45, new Color(140, 15, 25));
            g.setPaint(roofGrad);
            g.fillRoundRect(x + 48, y + 4, 130, 44, 26, 26);

            // Tinted Windows
            GradientPaint winGrad = new GradientPaint(x + 60, y + 12, new Color(190, 225, 255), x + 60, y + 34, new Color(90, 150, 210));
            g.setPaint(winGrad);
            g.fillRoundRect(x + 58, y + 12, 50, 24, 10, 10);
            g.fillRoundRect(x + 116, y + 12, 52, 24, 10, 10);

            // Gold Racing Stripe
            g.setColor(UiUtil.GOLD);
            g.fillRect(x + 14, y + 50, 207, 5);

            // Headlight glow
            g.setColor(new Color(254, 240, 138));
            g.fillOval(x + 218, y + 43, 15, 13);
            g.setColor(new Color(255, 255, 255, 210));
            g.fillOval(x + 224, y + 46, 7, 6);

            // Taillight
            g.setColor(new Color(239, 68, 68));
            g.fillRoundRect(x + 1, y + 45, 6, 13, 4, 4);

            // Front & Back Wheels: placed at y+56, size 38 -> wheel bottom at y+94, road at y+96
            // The wheels are completely inside component bounds (height 135) and fully visible!
            drawWheel(g, x + 38, y + 56, 38, wheelAngle);
            drawWheel(g, x + 162, y + 56, 38, wheelAngle);

            g.dispose();
        }

        private void drawWheel(Graphics2D g, int wx, int wy, int size, double angle) {
            // Tire (dark charcoal)
            g.setColor(new Color(28, 28, 30));
            g.fillOval(wx, wy, size, size);
            g.setColor(new Color(55, 55, 60));
            g.setStroke(new BasicStroke(2));
            g.drawOval(wx, wy, size, size);

            // Rim (silver alloy)
            int rimSize = size - 14;
            int rx = wx + 7;
            int ry = wy + 7;
            g.setColor(new Color(205, 210, 220));
            g.fillOval(rx, ry, rimSize, rimSize);
            g.setColor(new Color(130, 135, 145));
            g.drawOval(rx, ry, rimSize, rimSize);

            // Rotating Alloy Spokes
            int cx = wx + size / 2;
            int cy = wy + size / 2;
            g.setColor(new Color(75, 80, 90));
            g.setStroke(new BasicStroke(2));
            for (int i = 0; i < 5; i++) {
                double a = angle + i * (2 * Math.PI / 5);
                int sx = (int) (cx + (rimSize / 2.0 - 2) * Math.cos(a));
                int sy = (int) (cy + (rimSize / 2.0 - 2) * Math.sin(a));
                g.drawLine(cx, cy, sx, sy);
            }

            // Center Gold Hub Cap
            g.setColor(UiUtil.GOLD);
            g.fillOval(cx - 3, cy - 3, 6, 6);
        }
    }
}
