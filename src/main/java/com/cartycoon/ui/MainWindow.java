package com.cartycoon.ui;

import com.cartycoon.logic.CarCatalog;
import com.cartycoon.logic.CustomerFactory;
import com.cartycoon.logic.DayReport;
import com.cartycoon.logic.GameEngine;
import com.cartycoon.logic.GameListener;
import com.cartycoon.logic.SaveManager;
import com.cartycoon.logic.ScoreBoard;
import com.cartycoon.model.Achievement;
import com.cartycoon.model.Car;
import com.cartycoon.model.GameState;
import com.cartycoon.model.Level;
import com.cartycoon.util.Format;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/** The game screen with dark navy & gold theme, tooltips, and real-time updates. */
public class MainWindow extends JFrame implements GameListener {
    private static final long serialVersionUID = 1L;

    private final transient GameEngine engine;
    private final transient SaveManager saves;
    private final transient ScoreBoard scoreBoard;

    private final JLabel dayLabel = topLabel();
    private final JLabel moneyLabel = topLabel();
    private final JLabel starsLabel = topLabel();
    private final JLabel levelLabel = topLabel();
    private final JProgressBar levelBar = new RoundedProgressBar(0, 100);
    private final CustomerPanel customerPanel = new CustomerPanel();
    private final GaragePanel garagePanel = new GaragePanel();
    private final JTextArea logArea = new JTextArea(6, 80);

    public MainWindow(GameEngine engine, SaveManager saves, ScoreBoard scoreBoard) {
        super("Car Tycoon - Ông trùm cho thuê xe");
        this.engine = engine;
        this.saves = saves;
        this.scoreBoard = scoreBoard;
        getContentPane().setBackground(UiUtil.NAVY_BG);

        // Top Header Bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        top.setBackground(UiUtil.NAVY_DARKEST);
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UiUtil.GOLD));

        levelBar.setStringPainted(true);
        levelBar.setPreferredSize(new Dimension(180, 24));
        levelBar.setToolTipText("Tiến độ doanh thu cần đạt để nâng cấp showroom");

        dayLabel.setToolTipText("Thời gian kinh doanh hiện tại (Tối đa 30 ngày)");
        moneyLabel.setToolTipText("Tiền mặt hiện có. CẢNH BÁO: Âm tiền cuối ngày sẽ phá sản!");
        starsLabel.setToolTipText("Điểm uy tín cửa hàng (Tăng nhờ xe sạch và bảo dưỡng tốt)");
        levelLabel.setToolTipText("Cấp độ kinh doanh hiện tại của bạn");

        top.add(dayLabel);
        top.add(moneyLabel);
        top.add(starsLabel);
        top.add(levelLabel);
        top.add(levelBar);

        JButton achBtn = UiUtil.button("🎖️ Thành tích", null, this::showAchievements);
        achBtn.setToolTipText("Xem danh sách thành tích đã đạt và chưa đạt");
        JButton saveBtn = UiUtil.button("💾 Lưu game", null, () -> UiUtil.run(this, this::saveGameManual));
        saveBtn.setToolTipText("Lưu lại tiến trình ván chơi vào máy");
        JButton homeBtn = UiUtil.button("🏠 Màn hình chính", null, this::backToStart);
        homeBtn.setToolTipText("Lưu game và quay lại màn hình khởi đầu");

        top.add(achBtn);
        top.add(saveBtn);
        top.add(homeBtn);

        // Center Split Pane
        customerPanel.onSelect(garagePanel::setCustomer);
        JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, customerPanel, garagePanel);
        center.setDividerLocation(520);
        center.setBackground(UiUtil.NAVY_DARKEST);
        center.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // Bottom Actions Bar
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        actions.setOpaque(false);

        JButton rentBtn = UiUtil.button("🔑 Cho thuê →", UiUtil.GREEN, () -> act(() -> engine.rent(customerPanel.getSelected(), garagePanel.getSelected())));
        rentBtn.setToolTipText("Cho khách được chọn thuê xe được chọn (Khách thanh toán tiền ngay)");

        JButton washBtn = UiUtil.button("🫧 Rửa xe (100.000 đ)", null, () -> act(() -> engine.wash(garagePanel.getSelected())));
        washBtn.setToolTipText("Rửa sạch xe đang bẩn để khách hài lòng và gia tăng uy tín");

        JButton repairBtn = UiUtil.button("🔧 Sửa xe", null, this::repair);
        repairBtn.setToolTipText("Sửa chữa xe đã chọn phục hồi tình trạng máy móc về 100%");

        JButton sellBtn = UiUtil.button("🏷️ Bán xe", null, this::sell);
        sellBtn.setToolTipText("Thanh lý xe đã chọn để thu hồi tiền mặt");

        JButton shopBtn = UiUtil.button("🏬 Cửa hàng xe", UiUtil.BLUE, () -> new ShopDialog(this, engine).setVisible(true));
        shopBtn.setToolTipText("Mở showroom để mua thêm các dòng xe mới bổ sung vào garage");

        JButton nextDayBtn = UiUtil.button("🌅 Ngày tiếp theo >>", UiUtil.GOLD, this::nextDay);
        nextDayBtn.setToolTipText("Kết thúc ngày, nhận xe khách trả, tính phí bãi và đón khách mới");

        actions.add(rentBtn);
        actions.add(washBtn);
        actions.add(repairBtn);
        actions.add(sellBtn);
        actions.add(shopBtn);
        actions.add(nextDayBtn);

        // Bottom Log Area
        logArea.setEditable(false);
        logArea.setBackground(UiUtil.NAVY_DARKEST);
        logArea.setForeground(UiUtil.TEXT_PRIMARY);
        logArea.setCaretColor(UiUtil.GOLD);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setLineWrap(true);
        logArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.getViewport().setBackground(UiUtil.NAVY_DARKEST);
        logScroll.setBorder(BorderFactory.createLineBorder(UiUtil.NAVY_BORDER, 1));

        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
        bottom.add(actions, BorderLayout.NORTH);
        bottom.add(UiUtil.titled("Nhật ký hoạt động", logScroll), BorderLayout.CENTER);

        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(center, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);

        engine.addListener(this);
        onGameChanged(engine.getState());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!engine.getState().isGameOver()) UiUtil.run(MainWindow.this, MainWindow.this::saveGame);
                System.exit(0);
            }
        });
        setSize(1360, 800);
        setLocationRelativeTo(null);
    }

    private static JLabel topLabel() {
        JLabel label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        return label;
    }

    private void act(Runnable action) {
        UiUtil.run(this, action);
    }

    private void saveGameManual() {
        saveGame();
        UiUtil.info(this, "Lưu game thành công", "Tiến trình ván chơi đã được lưu an toàn!");
    }

    // ------------------------------------------------------------------ Observer callbacks

    @Override
    public void onGameChanged(GameState s) {
        dayLabel.setText("📅 Ngày " + s.getDay() + "/" + GameEngine.LAST_DAY);
        moneyLabel.setText("💰 " + Format.money(s.getMoney()));
        moneyLabel.setForeground(s.getMoney() < 1_000_000 ? new Color(248, 113, 113) : new Color(74, 222, 128));
        starsLabel.setText("⭐ " + UiUtil.stars(s.getStars()) + " (" + s.getReputation() + ")");
        Level level = s.getLevel();
        levelLabel.setText("🏆 Cấp " + level.getNumber() + ": " + level.getTitle());
        Level next = level.next();
        if (next == null) {
            levelBar.setValue(100);
            levelBar.setString("Cấp cao nhất!");
        } else {
            long from = level.getRevenueNeeded();
            long done = s.getTotalRevenue() - from;
            long need = next.getRevenueNeeded() - from;
            levelBar.setValue((int) (done * 100 / need));
            levelBar.setString("Lên cấp: " + done / 1_000_000 + "/" + need / 1_000_000 + " tr");
        }
        customerPanel.refresh(s);
        garagePanel.refresh(s);
        garagePanel.setCustomer(customerPanel.getSelected());
        List<String> log = s.getLog();
        logArea.setText(String.join("\n", log.subList(Math.max(0, log.size() - 200), log.size())));
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    @Override
    public void onLevelUp(Level level) {
        List<String> unlocked = new ArrayList<>();
        CarCatalog.OFFERS.stream().filter(o -> o.minLevel() == level.getNumber()).forEach(o -> unlocked.add("xe " + o.model()));
        CustomerFactory.newTypesAt(level).forEach(t -> unlocked.add("khách " + t));
        String text = "Chúc mừng! Cửa hàng lên " + level + ".\n"
                + (unlocked.isEmpty() ? "Mỗi ngày sẽ có thêm khách." : "Mở khóa: " + String.join(", ", unlocked) + ".\nMỗi ngày cũng có thêm khách.");
        UiUtil.info(this, "LÊN CẤP " + level.getNumber() + "!", text);
    }

    @Override
    public void onAchievement(Achievement achievement) {
        UiUtil.info(this, "Thành tích mới!", "Bạn vừa đạt thành tích \"" + achievement.getTitle() + "\"\n" + achievement.getDescription());
    }

    // ------------------------------------------------------------------ actions

    private void repair() {
        Car car = garagePanel.getSelected();
        act(() -> {
            if (car != null && car.getCondition() < 100
                    && !UiUtil.confirm(this, "Sửa " + car + " lên 100% với giá " + Format.money(car.getRepairCost()) + "?")) return;
            engine.repair(car);
        });
    }

    private void sell() {
        Car car = garagePanel.getSelected();
        act(() -> {
            if (car != null && !UiUtil.confirm(this, "Bán " + car + " với giá " + Format.money(car.getSellPrice()) + "?")) return;
            engine.sell(car);
        });
    }

    private void nextDay() {
        GameState s = engine.getState();
        if (!s.getCustomers().isEmpty() && !UiUtil.confirm(this,
                "Còn " + s.getCustomers().size() + " khách chưa được phục vụ, họ sẽ bỏ đi (mất uy tín).\nVẫn sang ngày mới?")) {
            return;
        }
        act(() -> {
            DayReport report = engine.nextDay();
            if (!report.gameOver()) saveGame();
            DaySummaryDialog.show(this, report, engine.getState());
            if (report.gameOver()) {
                saves.delete();
                new GameOverDialog(this, engine.getState(), scoreBoard).setVisible(true);
                dispose();
                new StartWindow(saves, scoreBoard).setVisible(true);
            }
        });
    }

    private void saveGame() {
        saves.save(engine.getState());
    }

    private void showAchievements() {
        JDialog dialog = new JDialog(this, "Thành tích người chơi", true);
        dialog.getContentPane().setBackground(UiUtil.NAVY_BG);

        JLabel title = new JLabel("🎖️ DANH HIỆU & THÀNH TÍCH", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setForeground(UiUtil.GOLD);

        JPanel listPanel = new JPanel(new GridLayout(0, 1, 8, 8));
        listPanel.setOpaque(false);

        for (Achievement a : Achievement.values()) {
            boolean done = engine.getState().getAchievements().contains(a);
            JPanel card = UiUtil.card(done ? new Color(20, 50, 32) : UiUtil.NAVY_CARD,
                    done ? new Color(34, 197, 94) : UiUtil.NAVY_BORDER);
            card.setLayout(new BorderLayout(6, 4));
            card.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            JLabel name = new JLabel((done ? "✔ [ĐÃ ĐẠT] " : "🔒 [CHƯA] ") + a.getTitle());
            name.setFont(name.getFont().deriveFont(Font.BOLD, 13f));
            name.setForeground(done ? new Color(110, 231, 145) : UiUtil.TEXT_MUTED);

            JLabel desc = new JLabel(a.getDescription());
            desc.setFont(desc.getFont().deriveFont(12f));
            desc.setForeground(UiUtil.TEXT_PRIMARY);

            card.add(name, BorderLayout.NORTH);
            card.add(desc, BorderLayout.CENTER);
            listPanel.add(card);
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtil.NAVY_BORDER, 1));
        scroll.setPreferredSize(new Dimension(520, 360));
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.add(UiUtil.button("Đóng", UiUtil.GOLD, dialog::dispose));

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UiUtil.NAVY_BG);
        root.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        root.add(title, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void backToStart() {
        act(this::saveGame);
        dispose();
        new StartWindow(saves, scoreBoard).setVisible(true);
    }

    /** Sleek rounded progress bar with dark track and gold fill. */
    private static class RoundedProgressBar extends JProgressBar {
        private static final long serialVersionUID = 1L;

        RoundedProgressBar(int min, int max) {
            super(min, max);
            setOpaque(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = h;

            // Track background
            g.setColor(new Color(25, 36, 60));
            g.fillRoundRect(0, 0, w, h, arc, arc);

            // Fill
            double pct = (double) (getValue() - getMinimum()) / Math.max(1, (getMaximum() - getMinimum()));
            int fillW = (int) Math.round(w * Math.max(0, Math.min(1.0, pct)));
            if (fillW > 0) {
                g.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, h, arc, arc));
                g.setPaint(new GradientPaint(0, 0, new Color(234, 179, 8), w, 0, new Color(250, 204, 21)));
                g.fillRoundRect(0, 0, Math.max(fillW, arc), h, arc, arc);
                g.setClip(null);
            }

            // Border
            g.setColor(new Color(42, 60, 96));
            g.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

            // String
            String str = getString();
            if (str != null && !str.isEmpty()) {
                Font font = getFont().deriveFont(Font.BOLD, 12f);
                g.setFont(font);
                FontMetrics fm = g.getFontMetrics();
                int tx = (w - fm.stringWidth(str)) / 2;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;

                g.setColor(new Color(11, 19, 43, 220));
                g.drawString(str, tx + 1, ty + 1);
                g.setColor(Color.WHITE);
                g.drawString(str, tx, ty);
            }
            g.dispose();
        }
    }
}
