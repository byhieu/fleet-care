package com.cartycoon.ui;

import com.cartycoon.logic.GameException;
import com.cartycoon.logic.ScoreBoard.HighScore;
import com.cartycoon.util.Format;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/** Helpers shared by all windows: navy/gold colors, themed tables, dialogs, icons. */
public final class UiUtil {
    // Primary Dark Navy & Gold Theme Palette
    public static final Color NAVY_DARKEST = new Color(11, 19, 43);      // #0b132b
    public static final Color NAVY_BG = new Color(15, 23, 42);           // #0f172a
    public static final Color NAVY_PANEL = new Color(22, 33, 62);        // #16213e
    public static final Color NAVY_PANEL_ALT = new Color(28, 41, 74);    // #1c294a
    public static final Color NAVY_CARD = new Color(30, 41, 69);         // #1e2945
    public static final Color NAVY_BORDER = new Color(42, 60, 96);       // #2a3c60
    public static final Color NAVY_SELECTED = new Color(37, 72, 126);    // #25487e
    public static final Color NAVY_HOVER = new Color(38, 54, 90);        // #26365a

    public static final Color GOLD = new Color(245, 180, 20);            // #f5b414
    public static final Color GOLD_LIGHT = new Color(254, 215, 102);     // #fed766
    public static final Color GOLD_DARK = new Color(202, 138, 4);        // #ca8a04
    public static final Color GOLD_HOVER = new Color(250, 204, 21);      // #facc15

    public static final Color TEXT_PRIMARY = new Color(241, 245, 249);   // #f1f5f9
    public static final Color TEXT_MUTED = new Color(148, 163, 184);     // #94a3b8
    public static final Color TEXT_GOLD = new Color(253, 224, 71);       // #fde047

    // Status Badge Colors (Harmonious with Dark Navy)
    public static final Color STATUS_GREEN_BG = new Color(20, 60, 36);
    public static final Color STATUS_GREEN_TEXT = new Color(110, 231, 145);
    public static final Color STATUS_BLUE_BG = new Color(24, 49, 89);
    public static final Color STATUS_BLUE_TEXT = new Color(147, 197, 253);
    public static final Color STATUS_RED_BG = new Color(77, 22, 22);
    public static final Color STATUS_RED_TEXT = new Color(252, 165, 165);
    public static final Color STATUS_YELLOW_BG = new Color(69, 45, 12);
    public static final Color STATUS_YELLOW_TEXT = new Color(253, 224, 71);

    // Backward-compatibility aliases
    public static final Color GREEN = new Color(34, 197, 94);
    public static final Color BLUE = new Color(59, 130, 246);
    public static final Color YELLOW = new Color(234, 179, 8);
    public static final Color RED = new Color(239, 68, 68);
    public static final Color DARK = NAVY_DARKEST;

    private UiUtil() {
    }

    /** Setup LookAndFeel and Dark Navy + Gold UI Defaults. */
    public static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // keep standard look
        }

        // Apply dark navy and gold colors to standard Swing UI defaults
        UIManager.put("Panel.background", NAVY_BG);
        UIManager.put("OptionPane.background", NAVY_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Table.background", NAVY_BG);
        UIManager.put("Table.foreground", TEXT_PRIMARY);
        UIManager.put("Table.gridColor", new Color(35, 48, 77));
        UIManager.put("Table.selectionBackground", NAVY_SELECTED);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TableHeader.background", NAVY_DARKEST);
        UIManager.put("TableHeader.foreground", GOLD);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("ScrollPane.background", NAVY_BG);
        UIManager.put("Viewport.background", NAVY_BG);
        UIManager.put("SplitPane.background", NAVY_DARKEST);
        UIManager.put("SplitPane.dividerSize", 5);
        UIManager.put("ToolTip.background", NAVY_CARD);
        UIManager.put("ToolTip.foreground", TEXT_PRIMARY);
        UIManager.put("ToolTip.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("ToolTip.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        UIManager.put("TitledBorder.titleColor", GOLD);

        for (Object key : Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(key) instanceof FontUIResource f && f.getSize() < 14) {
                UIManager.put(key, new FontUIResource(f.deriveFont(14f)));
            }
        }
    }

    /** Returns icon for car types. */
    public static String getCarIcon(String carType) {
        if (carType == null) return "🚗";
        if (carType.contains("Xe nhỏ")) return "🚗";
        if (carType.contains("Sedan")) return "🚘";
        if (carType.contains("SUV")) return "🚙";
        if (carType.contains("16 chỗ") || carType.contains("Van")) return "🚐";
        if (carType.contains("Xe sang")) return "💎";
        if (carType.contains("thể thao")) return "🏎️";
        return "🚗";
    }

    /** Returns icon for customer types. */
    public static String getCustomerIcon(String customerType) {
        if (customerType == null) return "👤";
        if (customerType.contains("Sinh viên")) return "🎓";
        if (customerType.contains("Gia đình")) return "👨‍👩‍👧";
        if (customerType.contains("Dân chơi")) return "🕶️";
        if (customerType.contains("Sếp lớn")) return "💼";
        if (customerType.contains("du lịch")) return "🧳";
        return "👤";
    }

    /** Runs a player action; a GameException becomes a friendly message instead of a crash. */
    public static void run(Component parent, Runnable action) {
        try {
            action.run();
        } catch (GameException e) {
            warn(parent, e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Lỗi không mong muốn: " + e, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Không được rồi!", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /** Configures standard table styling. */
    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(NAVY_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(35, 48, 77));
        table.setShowGrid(true);
        table.setSelectionBackground(NAVY_SELECTED);
        table.setSelectionForeground(Color.WHITE);

        var header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(NAVY_DARKEST);
        header.setForeground(GOLD);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));
        header.setPreferredSize(new Dimension(header.getWidth(), 34));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                l.setBackground(NAVY_DARKEST);
                l.setForeground(GOLD);
                l.setFont(l.getFont().deriveFont(Font.BOLD, 13f));
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(42, 60, 96)),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        });
    }

    /** Themed table with zebra striping and custom tooltips. */
    public static class ThemedTable extends JTable {
        private static final long serialVersionUID = 1L;
        private BiFunction<Integer, Integer, String> toolTipProvider;

        public ThemedTable(TableModel model) {
            super(model);
            styleTable(this);
        }

        public void setToolTipProvider(BiFunction<Integer, Integer, String> provider) {
            this.toolTipProvider = provider;
        }

        @Override
        public String getToolTipText(MouseEvent e) {
            if (toolTipProvider != null) {
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    String tip = toolTipProvider.apply(row, col);
                    if (tip != null && !tip.isEmpty()) return tip;
                }
            }
            return super.getToolTipText(e);
        }

        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
            Component c = super.prepareRenderer(renderer, row, col);
            if (!isRowSelected(row)) {
                Color altBg = (row % 2 == 0) ? NAVY_BG : NAVY_PANEL_ALT;
                if (!hasSpecialStatusBackground(c)) {
                    c.setBackground(altBg);
                    c.setForeground(TEXT_PRIMARY);
                }
            } else {
                if (!hasSpecialStatusBackground(c)) {
                    c.setBackground(NAVY_SELECTED);
                    c.setForeground(Color.WHITE);
                }
            }
            return c;
        }

        private boolean hasSpecialStatusBackground(Component c) {
            Color bg = c.getBackground();
            return bg != null && (
                    bg.equals(STATUS_GREEN_BG) ||
                    bg.equals(STATUS_BLUE_BG) ||
                    bg.equals(STATUS_RED_BG) ||
                    bg.equals(STATUS_YELLOW_BG)
            );
        }
    }

    /** Styled button with dark navy / gold accents and hover glow. */
    public static JButton button(String text, Color accentColor, Runnable action) {
        JButton b = new JButton(text);
        boolean isGold = (accentColor != null && accentColor.equals(GOLD));
        boolean isGreen = (accentColor != null && accentColor.equals(GREEN));
        boolean isBlue = (accentColor != null && accentColor.equals(BLUE));

        Color bg;
        Color fg;
        Color borderCol;
        if (isGold) {
            bg = GOLD;
            fg = NAVY_DARKEST;
            borderCol = GOLD_DARK;
        } else if (isGreen) {
            bg = new Color(22, 101, 52); // emerald green
            fg = Color.WHITE;
            borderCol = new Color(34, 197, 94);
        } else if (isBlue) {
            bg = new Color(30, 64, 175); // royal navy
            fg = Color.WHITE;
            borderCol = new Color(96, 165, 250);
        } else {
            bg = NAVY_CARD;
            fg = TEXT_PRIMARY;
            borderCol = NAVY_BORDER;
        }

        b.setBackground(bg);
        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(b.getFont().deriveFont(Font.BOLD, 13f));
        Border padding = BorderFactory.createEmptyBorder(6, 14, 6, 14);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderCol, 1), padding));

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!b.isEnabled()) return;
                if (isGold) {
                    b.setBackground(GOLD_HOVER);
                } else if (isGreen) {
                    b.setBackground(new Color(34, 130, 65));
                } else if (isBlue) {
                    b.setBackground(new Color(37, 99, 235));
                } else {
                    b.setBackground(NAVY_HOVER);
                    b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GOLD, 1), padding));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!b.isEnabled()) return;
                b.setBackground(bg);
                b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderCol, 1), padding));
            }
        });

        b.addActionListener(e -> action.run());
        return b;
    }

    /** Creates a titled container with gold title and dark navy styling. */
    public static JPanel titled(String title, JComponent content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(NAVY_BG);
        var border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(NAVY_BORDER, 1),
                " " + title + " ");
        border.setTitleColor(GOLD);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        p.setBorder(border);
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    /** Creates a rounded card panel. */
    public static JPanel card(Color bg, Color border) {
        JPanel p = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg != null ? bg : NAVY_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (border != null) {
                    g2.setColor(border);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    /** "★★★☆☆" */
    public static String stars(int count) {
        return "★".repeat(Math.max(0, Math.min(5, count))) + "☆".repeat(Math.max(0, 5 - count));
    }

    /** Themed High Scores Dialog with dark navy & gold cards. */
    public static void showHighScoresDialog(Component parent, List<HighScore> top) {
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Bảng xếp hạng", true);
        dialog.getContentPane().setBackground(NAVY_BG);

        JLabel title = new JLabel("🏆 BẢNG XẾP HẠNG CAO THỦ", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setForeground(GOLD);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        content.add(title, BorderLayout.NORTH);

        if (top.isEmpty()) {
            JLabel empty = new JLabel("<html><center style='color:#94a3b8; font-size:14px;'>"
                    + "Chưa có ai hoàn thành 30 ngày.<br/>Hãy chơi và ghi tên bạn vào bảng vàng đầu tiên!</center></html>", SwingConstants.CENTER);
            empty.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
            content.add(empty, BorderLayout.CENTER);
        } else {
            ListTableModel<HighScore> model = new ListTableModel<HighScore>()
                    .column("#", h -> {
                        int pos = top.indexOf(h) + 1;
                        return switch (pos) {
                            case 1 -> "🥇 #1";
                            case 2 -> "🥈 #2";
                            case 3 -> "🥉 #3";
                            default -> "#" + pos;
                        };
                    })
                    .column("Người chơi", HighScore::name)
                    .column("Điểm", h -> Format.money(h.score()))
                    .column("Hạng", HighScore::rank)
                    .column("Ngày", HighScore::date);
            model.setRows(top);
            ThemedTable table = new ThemedTable(model);
            int[] widths = {60, 130, 130, 60, 100};
            for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(520, 200));
            scroll.setBorder(BorderFactory.createLineBorder(NAVY_BORDER, 1));
            content.add(scroll, BorderLayout.CENTER);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.add(button("Đóng", GOLD, dialog::dispose));
        content.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /** Themed Game Instructions Dialog with card layout (fixes raw HTML bug). */
    public static void showHelpDialog(Component parent) {
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Hướng dẫn chơi Car Tycoon", true);
        dialog.getContentPane().setBackground(NAVY_BG);

        JLabel title = new JLabel("📖 HƯỚNG DẪN CHƠI CAR TYCOON", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setForeground(GOLD);

        JLabel subtitle = new JLabel("Khởi nghiệp với 15 triệu đồng & 2 chiếc xe cũ - Mục tiêu trở thành Ông trùm trong 30 ngày!", SwingConstants.CENTER);
        subtitle.setFont(subtitle.getFont().deriveFont(13f));
        subtitle.setForeground(TEXT_MUTED);

        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setOpaque(false);
        header.add(title);
        header.add(subtitle);

        JPanel cardsContainer = new JPanel(new GridLayout(0, 1, 10, 10));
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        cardsContainer.add(createHelpCard("🎯 1. Mục tiêu tối thượng",
                "Bạn có 30 ngày để kinh doanh, mua bán, cho thuê xe. Kết thúc ngày 30, điểm số của bạn được tính bằng tổng tiền mặt + giá trị đội xe + uy tín tích lũy. Hãy làm giàu thật nhanh!"));

        cardsContainer.add(createHelpCard("🤝 2. Đón khách & Cho thuê xe",
                "Mỗi sáng có nhóm khách đợi thuê. Chọn 1 khách bên trái, 1 xe bên phải rồi bấm Cho thuê →. Khách sẽ trả tiền ngay. Cột 'Với khách chọn' chỉ rõ xe có hợp không và khách sẽ trả bao nhiêu."));

        cardsContainer.add(createHelpCard("🫧 3. Vệ sinh & Bảo dưỡng định kỳ",
                "Khách trả xe xong xe sẽ bị bẩn và hao mòn. Bấm Rửa xe (100.000 đ) và Sửa xe khi cần. Xe dưới 30% tình trạng sẽ bị HỎNG và không thể cho thuê. Giữ xe luôn sạch và tốt sẽ tăng uy tín!"));

        cardsContainer.add(createHelpCard("🏬 4. Nâng cấp & Mở rộng đội xe",
                "Dùng tiền kiếm được vào Cửa hàng xe mua thêm xe mới. Khi đạt doanh thu yêu cầu bạn sẽ LÊN CẤP, mở khóa các dòng xe xịn (SUV, Van 16 chỗ, Xe sang, Thể thao) và các khách VIP trả nhiều tiền hơn."));

        cardsContainer.add(createHelpCard("🌅 5. Chuyển ngày & Tránh phá sản",
                "Bấm Ngày tiếp theo >> để sang ngày mới. Khách chưa phục vụ sẽ bỏ đi (mỗi khách -1 uy tín). Mỗi xe ở bãi tốn phí 100.000 đ/ngày. CẢNH BÁO: Nếu tiền âm vào cuối ngày, bạn sẽ PHÁ SẢN ngay!"));

        cardsContainer.add(createHelpCard("🏆 6. Bảng xếp hạng vinh danh",
                "Điểm = Tiền mặt + Giá trị xe + Uy tín × 100.000 đ. Xếp hạng: Hạng S (≥ 100 triệu) - Ông trùm thực thụ; Hạng A (≥ 70 triệu); Hạng B (≥ 45 triệu); Hạng C (< 45 triệu)."));

        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(620, 420));
        scroll.setBorder(BorderFactory.createLineBorder(NAVY_BORDER, 1));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.add(button("Đã hiểu, vào game ngay!", GOLD, dialog::dispose));

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(NAVY_BG);
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static JPanel createHelpCard(String heading, String body) {
        JPanel card = card(NAVY_CARD, NAVY_BORDER);
        card.setLayout(new BorderLayout(6, 6));
        card.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel title = new JLabel(heading);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setForeground(GOLD);

        JLabel text = new JLabel("<html><body style='width: 540px; color: #e2e8f0; line-height: 1.4;'>" + body + "</body></html>");
        text.setFont(text.getFont().deriveFont(13f));

        card.add(title, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);
        return card;
    }
}
