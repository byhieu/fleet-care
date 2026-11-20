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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình chính của Car Tycoon theo chuẩn thiết kế GitHub Dark.
 * Thanh trên chứa các ô thống kê tinh tế, 2 bảng giữa đối xứng, dải nút hành động nổi bật.
 */
public class MainWindow extends JFrame implements GameListener {
    private static final long serialVersionUID = 1L;

    private final transient GameEngine engine;
    private final transient SaveManager saves;
    private final transient ScoreBoard scoreBoard;

    // Các thành phần thống kê thanh trên
    private final JLabel dayTitle = new JLabel("NGÀY");
    private final JLabel dayVal = new JLabel("1 / 30");

    private final JLabel moneyTitle = new JLabel("TIỀN MẶT");
    private final JLabel moneyVal = new JLabel("15.000.000 đ");

    private final JLabel repTitle = new JLabel("UY TÍN");
    private final JLabel repVal = new JLabel();

    private final JLabel levelTitle = new JLabel("CẤP 1");
    private final JLabel levelVal = new JLabel("Khởi nghiệp");

    private final JLabel progressTitle = new JLabel("LÊN CẤP 2 · 0/15 TR");
    private final TopProgressBar progressBar = new TopProgressBar();

    private final CustomerPanel customerPanel = new CustomerPanel();
    private final GaragePanel garagePanel = new GaragePanel();
    private final JTextArea logArea = new JTextArea(5, 80);

    public MainWindow(GameEngine engine, SaveManager saves, ScoreBoard scoreBoard) {
        super("Car Tycoon - Ông trùm cho thuê xe");
        this.engine = engine;
        this.saves = saves;
        this.scoreBoard = scoreBoard;
        getContentPane().setBackground(UiUtil.COLOR_BG);

        // =====================================================================
        // 1. THANH TRÊN: Khung bo góc 10px, nền PANEL, viền LINE
        // =====================================================================
        JPanel topCard = UiUtil.card(UiUtil.COLOR_PANEL, UiUtil.COLOR_LINE);
        topCard.setLayout(new BorderLayout(16, 0));
        topCard.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        // Nhóm các ô thống kê bên trái
        JPanel statsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        statsLeft.setOpaque(false);

        dayVal.setFont(UiUtil.FONT_TITLE_LARGE);
        dayVal.setForeground(UiUtil.COLOR_TEXT);
        statsLeft.add(createStatBox(dayTitle, dayVal));

        moneyVal.setFont(UiUtil.FONT_TITLE_LARGE);
        moneyVal.setForeground(UiUtil.COLOR_OK);
        statsLeft.add(createStatBox(moneyTitle, moneyVal));

        repVal.setFont(UiUtil.FONT_TITLE_LARGE);
        repVal.setForeground(UiUtil.COLOR_TEXT);
        statsLeft.add(createStatBox(repTitle, repVal));

        levelVal.setFont(UiUtil.FONT_TITLE_LARGE);
        levelVal.setForeground(UiUtil.COLOR_TEXT);
        statsLeft.add(createStatBox(levelTitle, levelVal));

        // Ô tiến độ lên cấp
        JPanel progressBox = new JPanel(new BorderLayout(0, 8));
        progressBox.setOpaque(false);
        progressTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        progressTitle.setForeground(UiUtil.COLOR_MUTED);
        progressBox.add(progressTitle, BorderLayout.NORTH);
        progressBox.add(progressBar, BorderLayout.CENTER);
        statsLeft.add(progressBox);

        // 3 nút điều khiển bên phải
        JPanel buttonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        buttonsRight.setOpaque(false);

        JButton achBtn = UiUtil.button("Thành tích", null, this::showAchievements);
        JButton saveBtn = UiUtil.button("Lưu game", null, () -> UiUtil.run(this, this::saveGameManual));
        JButton homeBtn = UiUtil.button("Màn hình chính", null, this::backToStart);

        buttonsRight.add(achBtn);
        buttonsRight.add(saveBtn);
        buttonsRight.add(homeBtn);

        topCard.add(statsLeft, BorderLayout.CENTER);
        topCard.add(buttonsRight, BorderLayout.EAST);

        // =====================================================================
        // 2. GIỮA: 2 KHUNG "KHÁCH HÔM NAY" VÀ "GARAGE CỦA TÔI"
        // =====================================================================
        customerPanel.onSelect(garagePanel::setCustomer);

        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, customerPanel, garagePanel);
        centerSplit.setUI(new UiUtil.GitHubSplitPaneUI());
        centerSplit.setDividerSize(8);
        centerSplit.setContinuousLayout(true);
        centerSplit.setResizeWeight(0.45);
        centerSplit.setDividerLocation(660);
        centerSplit.setOpaque(false);
        centerSplit.setBackground(UiUtil.COLOR_BG);
        centerSplit.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // =====================================================================
        // 3. DẢI NÚT HÀNH ĐỘNG CĂN GIỮA
        // =====================================================================
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        actions.setOpaque(false);

        JButton rentBtn = UiUtil.button("Cho thuê →", UiUtil.COLOR_OK, () -> act(() -> engine.rent(customerPanel.getSelected(), garagePanel.getSelected())));
        JButton washBtn = UiUtil.button("Rửa xe · 100.000 đ", null, () -> act(() -> engine.wash(garagePanel.getSelected())));
        JButton repairBtn = UiUtil.button("Sửa xe", null, this::repair);
        JButton sellBtn = UiUtil.button("Bán xe", null, this::sell);
        JButton shopBtn = UiUtil.button("Cửa hàng xe", null, () -> new ShopDialog(this, engine).setVisible(true));
        JButton nextDayBtn = UiUtil.button("Ngày tiếp theo »", UiUtil.COLOR_OK, this::nextDay);

        actions.add(rentBtn);
        actions.add(washBtn);
        actions.add(repairBtn);
        actions.add(sellBtn);
        actions.add(shopBtn);
        actions.add(nextDayBtn);

        // =====================================================================
        // 4. DƯỚI CÙNG: KHUNG "NHẬT KÝ HOẠT ĐỘNG"
        // =====================================================================
        logArea.setEditable(false);
        logArea.setBackground(UiUtil.COLOR_PANEL);
        logArea.setForeground(UiUtil.COLOR_MUTED);
        logArea.setCaretColor(UiUtil.COLOR_ACCENT);
        logArea.setFont(UiUtil.FONT_MONO);
        logArea.setLineWrap(true);
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JScrollPane logScroll = new JScrollPane(logArea);
        UiUtil.styleScrollPane(logScroll);
        logScroll.getViewport().setBackground(UiUtil.COLOR_PANEL);

        JPanel logCard = UiUtil.createSectionCard("NHẬT KÝ HOẠT ĐỘNG", null, logScroll);
        logCard.setPreferredSize(new Dimension(logCard.getPreferredSize().width, 130));

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 6));
        bottomPanel.setOpaque(false);
        bottomPanel.add(actions, BorderLayout.NORTH);
        bottomPanel.add(logCard, BorderLayout.CENTER);

        // Bọc toàn bộ vào root panel có padding viền
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UiUtil.COLOR_BG);
        root.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        root.add(topCard, BorderLayout.NORTH);
        root.add(centerSplit, BorderLayout.CENTER);
        root.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(root);

        engine.addListener(this);
        onGameChanged(engine.getState());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    int w = centerSplit.getWidth();
                    if (w > 0) {
                        centerSplit.setDividerLocation((int) (w * 0.45));
                    } else {
                        centerSplit.setDividerLocation(660);
                    }
                });
            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (!engine.getState().isGameOver()) UiUtil.run(MainWindow.this, MainWindow.this::saveGame);
                System.exit(0);
            }
        });
        setSize(1500, 860);
        setMinimumSize(new Dimension(1200, 720));
        setLocationRelativeTo(null);
    }

    private static JPanel createStatBox(JLabel title, JLabel val) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        val.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        title.setForeground(UiUtil.COLOR_MUTED);
        p.add(title, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
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
        dayVal.setText(s.getDay() + " / " + GameEngine.LAST_DAY);
        moneyVal.setText(Format.money(s.getMoney()));
        moneyVal.setForeground(s.getMoney() < 1_000_000 ? UiUtil.COLOR_BAD : UiUtil.COLOR_OK);

        // Hiển thị sao màu WARN và điểm uy tín màu TEXT
        repVal.setText("<html><span style='color:#d29922; font-family: Segoe UI Symbol, Dialog, sans-serif; font-size: 16px;'>"
                + UiUtil.stars(s.getStars()) + "</span> <span style='color:#e6edf3; font-family: Segoe UI, sans-serif; font-size: 18px; font-weight: bold;'>"
                + s.getReputation() + "</span></html>");

        Level level = s.getLevel();
        levelTitle.setText("CẤP " + level.getNumber());
        levelVal.setText(level.getTitle());

        Level next = level.next();
        if (next == null) {
            progressBar.setValue(100);
            progressTitle.setText("CẤP CAO NHẤT");
        } else {
            long from = level.getRevenueNeeded();
            long done = s.getTotalRevenue() - from;
            long need = next.getRevenueNeeded() - from;
            int pct = (int) Math.max(0, Math.min(100, (done * 100 / need)));
            progressBar.setValue(pct);
            progressTitle.setText("LÊN CẤP " + next.getNumber() + " · " + (done / 1_000_000) + "/" + (need / 1_000_000) + " TR");
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
        dialog.getContentPane().setBackground(UiUtil.COLOR_BG);

        JLabel title = new JLabel("DANH HIỆU & THÀNH TÍCH", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UiUtil.COLOR_TEXT);

        JPanel listPanel = new JPanel(new GridLayout(0, 1, 8, 8));
        listPanel.setOpaque(false);

        for (Achievement a : Achievement.values()) {
            boolean done = engine.getState().getAchievements().contains(a);
            JPanel card = UiUtil.card(UiUtil.COLOR_PANEL, done ? UiUtil.COLOR_OK : UiUtil.COLOR_LINE);
            card.setLayout(new BorderLayout(8, 4));
            card.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

            JLabel name = new JLabel((done ? "✔ [ĐÃ ĐẠT] " : "🔒 [CHƯA] ") + a.getTitle());
            name.setFont(new Font("Segoe UI", Font.BOLD, 13));
            name.setForeground(done ? UiUtil.COLOR_OK : UiUtil.COLOR_MUTED);

            JLabel desc = new JLabel(a.getDescription());
            desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            desc.setForeground(UiUtil.COLOR_TEXT);

            card.add(name, BorderLayout.NORTH);
            card.add(desc, BorderLayout.CENTER);
            listPanel.add(card);
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtil.COLOR_LINE, 1));
        scroll.setPreferredSize(new Dimension(520, 360));
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.add(UiUtil.button("Đóng", null, dialog::dispose));

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(UiUtil.COLOR_BG);
        root.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
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

    /** Thanh tiến độ lên cấp cao 8px bo tròn màu ACCENT */
    private static class TopProgressBar extends JComponent {
        private static final long serialVersionUID = 1L;
        private int value = 0;

        TopProgressBar() {
            setPreferredSize(new Dimension(180, 8));
            setMinimumSize(new Dimension(150, 8));
            setOpaque(false);
        }

        public void setValue(int v) {
            this.value = Math.max(0, Math.min(100, v));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = h;

            // Track nền tối
            g.setColor(new Color(33, 38, 45));
            g.fillRoundRect(0, 0, w, h, arc, arc);

            // Tiến độ màu ACCENT
            int fillW = (int) Math.round(w * (value / 100.0));
            if (fillW > 0) {
                g.setColor(UiUtil.COLOR_ACCENT);
                g.fillRoundRect(0, 0, Math.max(fillW, arc), h, arc, arc);
            }
            g.dispose();
        }
    }
}
