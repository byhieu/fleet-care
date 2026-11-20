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

/**
 * Màn hình khởi đầu của Car Tycoon theo chuẩn thiết kế GitHub Dark.
 * Nền gradient nhẹ, tiêu đề CAR TYCOON với chữ TYCOON màu ACCENT,
 * thẻ Tiếp tục ván đang chơi viền xanh lá, nút Ván mới là nút chính.
 */
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

        // Header Tiêu đề lớn (chữ TYCOON màu vàng #FFD966)
        JLabel title = new JLabel("<html><span style='color:#e6edf3;'>CAR </span><span style='color:#FFD966;'>TYCOON</span></html>", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));

        JLabel subtitle = new JLabel("Ông trùm cho thuê xe - 30 ngày để làm giàu!", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(UiUtil.COLOR_MUTED);

        JPanel head = new JPanel(new BorderLayout(6, 6));
        head.setOpaque(false);
        head.add(title, BorderLayout.NORTH);
        head.add(new CarPicture(), BorderLayout.CENTER);
        head.add(subtitle, BorderLayout.SOUTH);

        // Ô nhập tên người chơi (bo góc, nền PANEL, viền LINE)
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        namePanel.setOpaque(false);

        JLabel namePrompt = new JLabel("Tên của bạn:");
        namePrompt.setForeground(UiUtil.COLOR_TEXT);
        namePrompt.setFont(new Font("Segoe UI", Font.BOLD, 14));

        nameField.setBackground(UiUtil.COLOR_PANEL);
        nameField.setForeground(UiUtil.COLOR_TEXT);
        nameField.setCaretColor(UiUtil.COLOR_ACCENT);
        nameField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiUtil.COLOR_LINE, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        namePanel.add(namePrompt);
        namePanel.add(nameField);

        // Thẻ Tiếp tục ván đang chơi (viền xanh lá)
        JPanel continueCard = buildContinueCard();

        // Danh sách nút menu chính (Ván mới là nút chính màu xanh lá, còn lại là nút thường)
        JPanel actionButtons = new JPanel(new GridLayout(0, 1, 8, 8));
        actionButtons.setOpaque(false);

        JButton newGameBtn = UiUtil.button("Ván mới", UiUtil.COLOR_OK, () -> start(GameEngine.newGame(playerName(), new Random())));
        JButton demoGameBtn = UiUtil.button("Ván demo (bắt đầu từ ngày 25)", null, () -> start(GameEngine.demoGame(playerName(), new Random())));
        JButton scoresBtn = UiUtil.button("Bảng xếp hạng", null, this::showHighScores);
        JButton helpBtn = UiUtil.button("Hướng dẫn chơi", null, this::showHelp);
        JButton exitBtn = UiUtil.button("Thoát", null, () -> System.exit(0));

        actionButtons.add(newGameBtn);
        actionButtons.add(demoGameBtn);
        actionButtons.add(scoresBtn);
        actionButtons.add(helpBtn);
        actionButtons.add(exitBtn);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(6, 24, 6, 24));
        centerPanel.add(namePanel, BorderLayout.NORTH);

        JPanel cardsAndButtons = new JPanel(new BorderLayout(10, 12));
        cardsAndButtons.setOpaque(false);
        cardsAndButtons.add(continueCard, BorderLayout.NORTH);
        cardsAndButtons.add(actionButtons, BorderLayout.CENTER);
        centerPanel.add(cardsAndButtons, BorderLayout.CENTER);

        // Root Panel với gradient nền rất nhẹ từ #161b22 xuống #0d1117
        JPanel root = new JPanel(new BorderLayout(10, 14)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x16, 0x1b, 0x22), 0, getHeight(), new Color(0x0d, 0x11, 0x17));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setBorder(BorderFactory.createEmptyBorder(18, 24, 20, 24));
        root.setPreferredSize(new Dimension(640, 640));
        root.add(head, BorderLayout.NORTH);
        root.add(centerPanel, BorderLayout.CENTER);

        setContentPane(root);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /** Xây dựng thẻ "Tiếp tục ván đang chơi" viền xanh lá theo phong cách GitHub Dark */
    private JPanel buildContinueCard() {
        if (!saves.hasSave()) {
            JPanel card = UiUtil.card(UiUtil.COLOR_PANEL, UiUtil.COLOR_LINE);
            card.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 10));
            JLabel lbl = new JLabel("Chưa có ván chơi dở • Bấm \"Ván mới\" để bắt đầu!");
            lbl.setForeground(UiUtil.COLOR_MUTED);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            card.add(lbl);
            return card;
        }

        GameState saved;
        try {
            saved = saves.load();
        } catch (RuntimeException e) {
            JPanel card = UiUtil.card(UiUtil.COLOR_PANEL, UiUtil.COLOR_LINE);
            card.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 10));
            JLabel lbl = new JLabel("File lưu bị hỏng hoặc không tương thích");
            lbl.setForeground(UiUtil.COLOR_BAD);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            card.add(lbl);
            return card;
        }

        // Thẻ có viền xanh lá (COLOR_OK)
        JPanel card = UiUtil.card(UiUtil.COLOR_PANEL, UiUtil.COLOR_OK);
        card.setLayout(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Dòng trên: tiêu đề + badge Vào game
        JPanel headerLine = new JPanel(new BorderLayout());
        headerLine.setOpaque(false);

        JLabel cardTitle = new JLabel("TIẾP TỤC VÁN ĐANG CHƠI");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cardTitle.setForeground(UiUtil.COLOR_OK);

        JLabel badge = new JLabel(" VÀO GAME ");
        badge.setOpaque(true);
        badge.setBackground(UiUtil.BTN_PRIMARY_BG);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiUtil.BTN_PRIMARY_BORDER, 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));

        headerLine.add(cardTitle, BorderLayout.WEST);
        headerLine.add(badge, BorderLayout.EAST);

        // Dòng chi tiết thông tin ván chơi
        JPanel detailsRow = new JPanel(new GridLayout(1, 4, 8, 4));
        detailsRow.setOpaque(false);

        JLabel pName = new JLabel(saved.getPlayerName());
        pName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pName.setForeground(UiUtil.COLOR_TEXT);

        JLabel pDay = new JLabel("Ngày " + saved.getDay() + "/30");
        pDay.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pDay.setForeground(UiUtil.COLOR_TEXT);

        JLabel pMoney = new JLabel(Format.money(saved.getMoney()));
        pMoney.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pMoney.setForeground(UiUtil.COLOR_OK);

        JLabel pRep = new JLabel("Uy tín: " + saved.getReputation());
        pRep.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pRep.setForeground(UiUtil.COLOR_WARN);

        detailsRow.add(pName);
        detailsRow.add(pDay);
        detailsRow.add(pMoney);
        detailsRow.add(pRep);

        card.add(headerLine, BorderLayout.NORTH);
        card.add(detailsRow, BorderLayout.CENTER);

        // Hiệu ứng hover
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                continueGame();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(UiUtil.BTN_HOVER);
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(UiUtil.COLOR_PANEL);
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

    /** Xe hoạt hình đồ họa phong cách GitHub Dark với bánh xe quay mượt mà */
    private static class CarPicture extends JComponent {
        private static final long serialVersionUID = 1L;
        private double wheelAngle = 0;
        private int roadOffset = 0;
        private final transient Timer timer;

        CarPicture() {
            setPreferredSize(new Dimension(360, 135));
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

            // Đường chạy với vạch kẻ đứt đoạn màu vàng #FFD966
            int roadY = y + 96;
            g.setColor(new Color(22, 27, 34));
            g.fillRect(0, roadY, getWidth(), 24);

            g.setColor(new Color(0xFF, 0xD9, 0x66, 190));
            g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int rx = -40 + roadOffset; rx < getWidth() + 40; rx += 40) {
                g.drawLine(rx, roadY + 8, rx + 20, roadY + 8);
            }

            g.setColor(UiUtil.COLOR_LINE);
            g.drawLine(0, roadY, getWidth(), roadY);

            // Bóng xe
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRoundRect(x + 10, roadY - 3, 220, 8, 8, 8);

            // Thân xe: Thể thao màu vàng #FFD966
            GradientPaint bodyGrad = new GradientPaint(x, y + 25, new Color(0xFF, 0xD9, 0x66), x, y + 75, new Color(218, 165, 32));
            g.setPaint(bodyGrad);
            g.fillRoundRect(x, y + 32, 235, 42, 22, 22);

            // Nóc xe
            GradientPaint roofGrad = new GradientPaint(x + 50, y + 4, new Color(0xFF, 0xDF, 0x75), x + 50, y + 45, new Color(210, 155, 25));
            g.setPaint(roofGrad);
            g.fillRoundRect(x + 48, y + 4, 130, 44, 26, 26);

            // Cửa kính xe: Xám đậm để nhìn rõ ràng nổi bật trên nền xe vàng
            GradientPaint winGrad = new GradientPaint(x + 60, y + 12, new Color(48, 54, 61), x + 60, y + 34, new Color(22, 27, 34));
            g.setPaint(winGrad);
            g.fillRoundRect(x + 58, y + 12, 50, 24, 10, 10);
            g.fillRoundRect(x + 116, y + 12, 52, 24, 10, 10);
            g.setColor(new Color(68, 76, 86));
            g.setStroke(new BasicStroke(1f));
            g.drawRoundRect(x + 58, y + 12, 50, 24, 10, 10);
            g.drawRoundRect(x + 116, y + 12, 52, 24, 10, 10);

            // Đường sọc trang trí thân xe
            g.setColor(new Color(0xFF, 0xEC, 0xB3));
            g.fillRect(x + 14, y + 50, 207, 4);

            // Đèn pha
            g.setColor(new Color(230, 237, 243));
            g.fillOval(x + 218, y + 43, 15, 13);
            g.setColor(new Color(255, 255, 255, 220));
            g.fillOval(x + 224, y + 46, 7, 6);

            // Đèn hậu
            g.setColor(UiUtil.COLOR_BAD);
            g.fillRoundRect(x + 1, y + 45, 6, 13, 4, 4);

            // 2 Bánh xe trước và sau
            drawWheel(g, x + 38, y + 56, 38, wheelAngle);
            drawWheel(g, x + 162, y + 56, 38, wheelAngle);

            g.dispose();
        }

        private void drawWheel(Graphics2D g, int wx, int wy, int size, double angle) {
            // Lốp xe
            g.setColor(new Color(20, 24, 30));
            g.fillOval(wx, wy, size, size);
            g.setColor(new Color(48, 54, 61));
            g.setStroke(new BasicStroke(2));
            g.drawOval(wx, wy, size, size);

            // Vành xe
            int rimSize = size - 14;
            int rx = wx + 7;
            int ry = wy + 7;
            g.setColor(new Color(180, 190, 205));
            g.fillOval(rx, ry, rimSize, rimSize);
            g.setColor(new Color(110, 120, 135));
            g.drawOval(rx, ry, rimSize, rimSize);

            // Nan hoa hợp kim quay
            int cx = wx + size / 2;
            int cy = wy + size / 2;
            g.setColor(new Color(60, 68, 78));
            g.setStroke(new BasicStroke(2));
            for (int i = 0; i < 5; i++) {
                double a = angle + i * (2 * Math.PI / 5);
                int sx = (int) (cx + (rimSize / 2.0 - 2) * Math.cos(a));
                int sy = (int) (cy + (rimSize / 2.0 - 2) * Math.sin(a));
                g.drawLine(cx, cy, sx, sy);
            }

            // Trục giữa (bánh xe giữ màu tối)
            g.setColor(new Color(48, 54, 61));
            g.fillOval(cx - 3, cy - 3, 6, 6);
        }
    }
}
