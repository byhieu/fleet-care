package com.cartycoon.ui;

import com.cartycoon.logic.GameException;
import com.cartycoon.logic.ScoreBoard.HighScore;
import com.cartycoon.model.Car;
import com.cartycoon.model.Customer;
import com.cartycoon.util.Format;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Tiện ích giao diện cho Car Tycoon theo phong cách GitHub Dark.
 * Toàn bộ bảng màu, font chữ, renderers, nút bấm và hộp thoại tự vẽ được tập trung tại đây.
 */
public final class UiUtil {

    // =========================================================================
    // BẢNG MÀU GITHUB DARK
    // =========================================================================
    public static final Color COLOR_BG = new Color(0x0d, 0x11, 0x17);       // #0d1117 Nền cửa sổ
    public static final Color COLOR_PANEL = new Color(0x16, 0x1b, 0x22);    // #161b22 Khung bảng, thanh trên
    public static final Color COLOR_LINE = new Color(0x30, 0x36, 0x3d);     // #30363d Viền, đường kẻ
    public static final Color COLOR_TEXT = new Color(0xe6, 0xed, 0xf3);     // #e6edf3 Chữ chính
    public static final Color COLOR_MUTED = new Color(0x7d, 0x85, 0x90);    // #7d8590 Chữ phụ, nhãn nhỏ
    public static final Color COLOR_ACCENT = new Color(0x2f, 0x81, 0xf7);   // #2f81f7 Xanh dương điểm nhấn
    public static final Color COLOR_OK = new Color(0x3f, 0xb9, 0x50);       // #3fb950 Xanh lá thành công
    public static final Color COLOR_WARN = new Color(0xd2, 0x99, 0x22);     // #d29922 Vàng cảnh báo
    public static final Color COLOR_BAD = new Color(0xf8, 0x51, 0x49);      // #f85149 Đỏ lỗi / nguy hiểm

    // Màu nút bấm
    public static final Color BTN_BG = new Color(0x21, 0x26, 0x2d);         // #21262d Nền nút thường
    public static final Color BTN_BORDER = new Color(0x30, 0x36, 0x3d);     // #30363d Viền nút thường
    public static final Color BTN_HOVER = new Color(0x30, 0x36, 0x3d);      // Hover sáng nhẹ
    public static final Color BTN_PRESSED = new Color(0x16, 0x1b, 0x22);    // Nhấn xuống tối hơn

    public static final Color BTN_PRIMARY_BG = new Color(0x23, 0x86, 0x36); // #238636 Nền nút chính
    public static final Color BTN_PRIMARY_BORDER = new Color(0x2e, 0xa0, 0x43);
    public static final Color BTN_PRIMARY_HOVER = new Color(0x2e, 0xa0, 0x43);
    public static final Color BTN_PRIMARY_PRESSED = new Color(0x1e, 0x7e, 0x34);

    // Bảng dữ liệu
    public static final Color ROW_SELECTED_BG = new Color(47, 129, 247, 51); // #2f81f7 20%
    public static final Color ROW_EVEN_BG = new Color(255, 255, 255, 5);    // 2% trắng sọc nhẹ

    // Tương thích ngược với các tên màu cũ
    public static final Color NAVY_DARKEST = COLOR_BG;
    public static final Color NAVY_BG = COLOR_BG;
    public static final Color NAVY_PANEL = COLOR_PANEL;
    public static final Color NAVY_PANEL_ALT = COLOR_PANEL;
    public static final Color NAVY_CARD = COLOR_PANEL;
    public static final Color NAVY_BORDER = COLOR_LINE;
    public static final Color NAVY_SELECTED = ROW_SELECTED_BG;
    public static final Color NAVY_HOVER = BTN_HOVER;

    public static final Color GOLD = COLOR_WARN;
    public static final Color GOLD_LIGHT = COLOR_WARN;
    public static final Color GOLD_DARK = COLOR_WARN;
    public static final Color GOLD_HOVER = COLOR_WARN;

    public static final Color TEXT_PRIMARY = COLOR_TEXT;
    public static final Color TEXT_MUTED = COLOR_MUTED;
    public static final Color TEXT_GOLD = COLOR_WARN;

    public static final Color STATUS_GREEN_BG = new Color(63, 185, 80, 40);
    public static final Color STATUS_GREEN_TEXT = COLOR_OK;
    public static final Color STATUS_BLUE_BG = new Color(47, 129, 247, 40);
    public static final Color STATUS_BLUE_TEXT = COLOR_ACCENT;
    public static final Color STATUS_RED_BG = new Color(248, 81, 73, 40);
    public static final Color STATUS_RED_TEXT = COLOR_BAD;
    public static final Color STATUS_YELLOW_BG = new Color(210, 153, 34, 40);
    public static final Color STATUS_YELLOW_TEXT = COLOR_WARN;

    public static final Color GREEN = COLOR_OK;
    public static final Color BLUE = COLOR_ACCENT;
    public static final Color YELLOW = COLOR_WARN;
    public static final Color RED = COLOR_BAD;
    public static final Color DARK = COLOR_BG;

    // =========================================================================
    // FONT
    // =========================================================================
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TITLE_LARGE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 13);

    private UiUtil() {
    }

    /** Cấu hình giao diện và đặt font, màu mặc định cho toàn bộ ứng dụng qua UIManager. */
    public static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Sử dụng look mặc định nếu không load được hệ thống
        }

        UIManager.put("Panel.background", COLOR_BG);
        UIManager.put("Panel.foreground", COLOR_TEXT);
        UIManager.put("Panel.font", FONT_BODY);

        UIManager.put("Label.font", FONT_BODY);
        UIManager.put("Label.foreground", COLOR_TEXT);

        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));

        UIManager.put("Table.background", COLOR_PANEL);
        UIManager.put("Table.foreground", COLOR_TEXT);
        UIManager.put("Table.gridColor", COLOR_LINE);
        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("Table.selectionBackground", ROW_SELECTED_BG);
        UIManager.put("Table.selectionForeground", COLOR_TEXT);

        UIManager.put("TableHeader.background", COLOR_PANEL);
        UIManager.put("TableHeader.foreground", COLOR_MUTED);
        UIManager.put("TableHeader.font", FONT_HEADER);

        UIManager.put("ScrollPane.background", COLOR_PANEL);
        UIManager.put("Viewport.background", COLOR_PANEL);

        UIManager.put("SplitPane.background", COLOR_BG);
        UIManager.put("SplitPane.dividerSize", 8);

        UIManager.put("ToolTip.background", COLOR_PANEL);
        UIManager.put("ToolTip.foreground", COLOR_TEXT);
        UIManager.put("ToolTip.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("ToolTip.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_LINE, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        UIManager.put("OptionPane.background", COLOR_BG);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXT);

        for (Object key : Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(key) instanceof FontUIResource f && f.getSize() < 14) {
                UIManager.put(key, new FontUIResource(new Font("Segoe UI", f.getStyle(), Math.max(13, f.getSize()))));
            }
        }
    }

    /** Icon thay thế không dùng emoji thô */
    public static String getCarIcon(String carType) {
        return "";
    }

    public static String getCustomerIcon(String customerType) {
        return "";
    }

    /** Chạy tác vụ người chơi; GameException được hiển thị qua hộp thoại đẹp mắt */
    public static void run(Component parent, Runnable action) {
        try {
            action.run();
        } catch (GameException e) {
            warn(parent, e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            warn(parent, "Lỗi không mong muốn: " + e.getMessage());
        }
    }

    // =========================================================================
    // HỘP THOẠI TỰ VẼ PHONG CÁCH GITHUB DARK (thay thế JOptionPane)
    // =========================================================================

    public static void warn(Component parent, String message) {
        showCustomDialog(parent, "CẢNH BÁO", message, COLOR_BAD, false);
    }

    public static void info(Component parent, String title, String message) {
        showCustomDialog(parent, title != null ? title.toUpperCase() : "THÔNG BÁO", message, COLOR_ACCENT, false);
    }

    public static boolean confirm(Component parent, String message) {
        return showCustomDialog(parent, "XÁC NHẬN", message, COLOR_WARN, true);
    }

    private static boolean showCustomDialog(Component parent, String title, String message, Color accent, boolean isConfirm) {
        JFrame owner = parent instanceof JFrame f ? f : (JFrame) SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, true);
        dialog.setUndecorated(true);

        boolean[] result = new boolean[]{false};

        JPanel root = new JPanel(new BorderLayout(14, 14)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(COLOR_LINE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // Thanh tiêu đề
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dot.setForeground(accent);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(COLOR_TEXT);

        top.add(dot);
        top.add(titleLbl);
        root.add(top, BorderLayout.NORTH);

        // Nội dung tin nhắn
        String cleanMsg = message.replace("\n", "<br/>");
        JLabel msgLbl = new JLabel("<html><body style='width: 380px; color: #e6edf3; font-family: Segoe UI, sans-serif; font-size: 13px; line-height: 1.5;'>"
                + cleanMsg + "</body></html>");
        msgLbl.setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));
        root.add(msgLbl, BorderLayout.CENTER);

        // Các nút bấm bên phải
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        if (isConfirm) {
            JButton cancelBtn = button("Hủy", null, dialog::dispose);
            JButton okBtn = button("Đồng ý", COLOR_OK, () -> {
                result[0] = true;
                dialog.dispose();
            });
            bottom.add(cancelBtn);
            bottom.add(okBtn);
        } else {
            JButton closeBtn = button("Đóng", accent.equals(COLOR_BAD) ? null : COLOR_OK, dialog::dispose);
            bottom.add(closeBtn);
        }
        root.add(bottom, BorderLayout.SOUTH);

        // Bấm Escape để đóng
        dialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dialog.dispose();
                }
            }
        });

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return result[0];
    }

    // =========================================================================
    // NÚT BẤM GITHUB DARK
    // =========================================================================

    /**
     * Nút bo góc 8px, viền 1px, hover sáng lên, nhấn xuống tối hơn, disabled mờ 40%.
     * accentColor = COLOR_OK hoặc BTN_PRIMARY_BG tạo nút chính xanh lá.
     */
    public static JButton button(String text, Color accentColor, Runnable action) {
        boolean isPrimary = (accentColor != null && (accentColor.equals(COLOR_OK) || accentColor.equals(BTN_PRIMARY_BG) || accentColor.equals(GREEN)));

        Color normalBg = isPrimary ? BTN_PRIMARY_BG : BTN_BG;
        Color normalBorder = isPrimary ? BTN_PRIMARY_BORDER : BTN_BORDER;
        Color hoverBg = isPrimary ? BTN_PRIMARY_HOVER : BTN_HOVER;
        Color pressedBg = isPrimary ? BTN_PRIMARY_PRESSED : BTN_PRESSED;
        Color fg = isPrimary ? Color.WHITE : COLOR_TEXT;

        JButton b = new JButton(text) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                Color bg = normalBg;
                if (!isEnabled()) {
                    bg = new Color(normalBg.getRed(), normalBg.getGreen(), normalBg.getBlue(), 100);
                } else if (getModel().isPressed()) {
                    bg = pressedBg;
                } else if (getModel().isRollover()) {
                    bg = hoverBg;
                }

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, 8, 8);

                Color border = isEnabled() ? normalBorder : new Color(normalBorder.getRed(), normalBorder.getGreen(), normalBorder.getBlue(), 80);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);

                // Chữ
                Font font = getFont();
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;

                Color textCol = isEnabled() ? fg : COLOR_MUTED;
                g2.setColor(textCol);
                g2.drawString(getText(), tx, ty);

                g2.dispose();
            }
        };

        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        b.addActionListener(e -> {
            if (action != null) action.run();
        });

        return b;
    }

    // =========================================================================
    // CÁC KHUNG VÀ CARD BO GÓC
    // =========================================================================

    /** Tạo một JPanel bo góc 10px với viền 1px LINE theo phong cách GitHub Dark. */
    public static JPanel card(Color bg, Color border) {
        JPanel p = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg != null ? bg : COLOR_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (border != null) {
                    g2.setColor(border);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    /** Khung bo góc 10px có tiêu đề chữ hoa nhỏ màu MUTED và số lượng màu ACCENT bên phải */
    public static JPanel createSectionCard(String title, String countText, JComponent content) {
        JPanel card = card(COLOR_PANEL, COLOR_LINE);
        card.setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(COLOR_MUTED);
        header.add(titleLbl, BorderLayout.WEST);

        if (countText != null && !countText.isEmpty()) {
            JLabel countLbl = new JLabel(countText.toUpperCase());
            countLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            countLbl.setForeground(COLOR_ACCENT);
            header.add(countLbl, BorderLayout.EAST);
        }

        card.add(header, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /** Tạo khung titled giữ tương thích ngược */
    public static JPanel titled(String title, JComponent content) {
        return createSectionCard(title, null, content);
    }

    /** "★★★★★" */
    public static String stars(int count) {
        return "★".repeat(Math.max(0, Math.min(5, count))) + "☆".repeat(Math.max(0, 5 - count));
    }

    // =========================================================================
    // BẢNG DỮ LIỆU VÀ CELL RENDERERS
    // =========================================================================

    /** Cấu hình thuộc tính chuẩn cho bảng ThemedTable */
    public static void styleTable(JTable table) {
        table.setRowHeight(48);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(COLOR_PANEL);
        table.setForeground(COLOR_TEXT);
        table.setGridColor(COLOR_LINE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(ROW_SELECTED_BG);
        table.setSelectionForeground(COLOR_TEXT);

        var header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(COLOR_PANEL);
        header.setForeground(COLOR_MUTED);
        header.setFont(FONT_HEADER);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                l.setBackground(COLOR_PANEL);
                l.setForeground(COLOR_MUTED);
                l.setFont(FONT_HEADER);
                l.setHorizontalAlignment(SwingConstants.LEFT);
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE),
                        BorderFactory.createEmptyBorder(0, 14, 0, 14)));
                return l;
            }
        });
    }

    /** Tùy biến thanh cuộn JScrollPane theo phong cách thanh cuộn tối giản GitHub Dark */
    public static void styleScrollPane(JScrollPane scroll) {
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // Thanh cuộn dọc
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setUI(new GitHubScrollBarUI());

        // Thanh cuộn ngang
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
        scroll.getHorizontalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUI(new GitHubScrollBarUI());
    }

    private static class GitHubScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(48, 54, 61);
            this.trackColor = new Color(22, 27, 34);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        private JButton createInvisibleButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(48, 54, 61));
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 6, 6);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(new Color(22, 27, 34));
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
    }

    /**
     * UI tùy biến cho JSplitPane theo chuẩn GitHub Dark:
     * - Thanh chia mảnh 1px màu #30363d, vùng kéo 8px, không viền, không mũi tên.
     * - Rê chuột / kéo sáng thành màu ACCENT #2f81f7.
     */
    public static class GitHubSplitPaneUI extends BasicSplitPaneUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            if (splitPane != null) {
                splitPane.setBorder(null);
            }
        }

        @Override
        public BasicSplitPaneDivider createDefaultDivider() {
            return new BasicSplitPaneDivider(this) {
                private static final long serialVersionUID = 1L;
                private boolean isHover = false;
                private boolean isPressed = false;

                {
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            isHover = true;
                            repaint();
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            isHover = false;
                            repaint();
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            isPressed = true;
                            repaint();
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            isPressed = false;
                            repaint();
                        }
                    });
                }

                @Override
                public void setBorder(Border b) {
                    // Không viền
                }

                @Override
                protected JButton createLeftOneTouchButton() {
                    return null; // Không nút mũi tên
                }

                @Override
                protected JButton createRightOneTouchButton() {
                    return null; // Không nút mũi tên
                }

                @Override
                public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    // Nền vùng kéo 8px đồng màu nền BG
                    g2.setColor(COLOR_BG);
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    // Thanh mảnh 1px ở giữa, sáng màu ACCENT khi hover/kéo
                    Color lineColor = (isHover || isPressed) ? COLOR_ACCENT : COLOR_LINE;
                    g2.setColor(lineColor);
                    int lineX = (getWidth() - 1) / 2;
                    g2.fillRect(lineX, 0, 1, getHeight());

                    g2.dispose();
                }
            };
        }
    }

    /** ThemedTable có hỗ trợ vạch xanh 3px bên trái dòng được chọn và kẻ sọc chẵn nhẹ */
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
        public boolean getScrollableTracksViewportWidth() {
            return getAutoResizeMode() != AUTO_RESIZE_OFF || getParent() == null || getParent().getWidth() >= getPreferredSize().width;
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
            if (isRowSelected(row)) {
                c.setBackground(ROW_SELECTED_BG);
            } else {
                c.setBackground((row % 2 == 0) ? ROW_EVEN_BG : COLOR_PANEL);
            }
            return c;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            int sel = getSelectedRow();
            if (sel >= 0) {
                Rectangle rect = getCellRect(sel, 0, false);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(COLOR_ACCENT);
                g2.fillRect(0, rect.y, 3, rect.height);
                g2.dispose();
            }
        }
    }

    /** Renderer vẽ chữ dạng nhãn bo tròn (pill), nền LINE hoặc màu trạng thái */
    public static class PillRenderer extends JComponent implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private String text = "";
        private boolean isSelected = false;
        private int row = 0;

        public PillRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
            this.text = value == null ? "" : value.toString();
            this.isSelected = selected;
            this.row = row;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Nền cell
            Color cellBg = isSelected ? ROW_SELECTED_BG : ((row % 2 == 0) ? ROW_EVEN_BG : COLOR_PANEL);
            g.setColor(cellBg);
            g.fillRect(0, 0, w, h);

            if (text.isEmpty()) {
                g.dispose();
                return;
            }

            Color pillBg;
            Color pillBorder;
            Color pillFg;

            if (text.equals("Có xe") || text.equals("Sạch") || text.equals("Rảnh") || text.startsWith("Có") || text.startsWith("✔")) {
                pillBg = new Color(63, 185, 80, 35);
                pillBorder = new Color(63, 185, 80, 90);
                pillFg = COLOR_OK;
            } else if (text.equals("Bẩn") || text.startsWith("Chờ")) {
                pillBg = new Color(210, 153, 34, 35);
                pillBorder = new Color(210, 153, 34, 90);
                pillFg = COLOR_WARN;
            } else if (text.equals("Chưa có") || text.equals("Hỏng") || text.startsWith("Chưa") || text.startsWith("Không")) {
                pillBg = new Color(248, 81, 73, 35);
                pillBorder = new Color(248, 81, 73, 90);
                pillFg = COLOR_BAD;
            } else if (text.startsWith("Đang thuê")) {
                pillBg = new Color(47, 129, 247, 35);
                pillBorder = new Color(47, 129, 247, 90);
                pillFg = COLOR_ACCENT;
            } else {
                // Nhãn loại xe / loại khách thông thường
                pillBg = new Color(48, 54, 61, 140);
                pillBorder = COLOR_LINE;
                pillFg = COLOR_TEXT;
            }

            Font font = new Font("Segoe UI", Font.BOLD, 12);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int textW = fm.stringWidth(text);
            int pad = (w >= textW + 18) ? 14 : Math.max(6, w - textW - 4);
            int pillW = textW + pad;
            int pillH = 22;
            int pillX = Math.max(4, Math.min(8, (w - pillW) / 2));
            int pillY = (h - pillH) / 2;

            g.setColor(pillBg);
            g.fillRoundRect(pillX, pillY, pillW, pillH, 10, 10);
            g.setColor(pillBorder);
            g.drawRoundRect(pillX, pillY, pillW - 1, pillH - 1, 10, 10);

            int tx = pillX + (pillW - textW) / 2;
            int ty = pillY + (pillH + fm.getAscent() - fm.getDescent()) / 2;
            g.setColor(pillFg);
            g.drawString(text, tx, ty);

            g.dispose();
        }
    }

    /** Renderer vẽ 2 nhãn pill cạnh nhau cho cột Trạng thái (Vệ sinh + Trạng thái vận hành) */
    public static class DualStatusRenderer extends JComponent implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private Car car;
        private boolean isSelected;
        private int row;

        public DualStatusRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
            this.car = value instanceof Car c ? c : null;
            this.isSelected = selected;
            this.row = row;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color cellBg = isSelected ? ROW_SELECTED_BG : ((row % 2 == 0) ? ROW_EVEN_BG : COLOR_PANEL);
            g.setColor(cellBg);
            g.fillRect(0, 0, w, h);

            if (car == null) {
                g.dispose();
                return;
            }

            // Pill 1: Vệ sinh
            String t1 = car.isClean() ? "Sạch" : "Bẩn";
            Color bg1 = car.isClean() ? new Color(63, 185, 80, 35) : new Color(210, 153, 34, 35);
            Color bd1 = car.isClean() ? new Color(63, 185, 80, 90) : new Color(210, 153, 34, 90);
            Color fg1 = car.isClean() ? COLOR_OK : COLOR_WARN;

            // Pill 2: Trạng thái
            String t2;
            Color bg2;
            Color bd2;
            Color fg2;
            if (car.getStatus() == com.cartycoon.model.CarStatus.FREE) {
                t2 = "Rảnh";
                bg2 = new Color(63, 185, 80, 35);
                bd2 = new Color(63, 185, 80, 90);
                fg2 = COLOR_OK;
            } else if (car.getStatus() == com.cartycoon.model.CarStatus.RENTED) {
                t2 = "Đang thuê";
                bg2 = new Color(47, 129, 247, 35);
                bd2 = new Color(47, 129, 247, 90);
                fg2 = COLOR_ACCENT;
            } else {
                t2 = "Hỏng";
                bg2 = new Color(248, 81, 73, 35);
                bd2 = new Color(248, 81, 73, 90);
                fg2 = COLOR_BAD;
            }

            Font font = new Font("Segoe UI", Font.BOLD, 12);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();

            int textW1 = fm.stringWidth(t1);
            int textW2 = fm.stringWidth(t2);

            int pad1 = 14;
            int pad2 = 14;
            int gap = 6;
            if (8 + (textW1 + pad1) + gap + (textW2 + pad2) > w) {
                pad1 = 10;
                pad2 = 10;
                gap = 4;
            }
            if (8 + (textW1 + pad1) + gap + (textW2 + pad2) > w) {
                pad1 = 6;
                pad2 = 6;
                gap = 3;
            }

            int pillW1 = textW1 + pad1;
            int pillW2 = textW2 + pad2;
            int pillH = 22;
            int pillY = (h - pillH) / 2;

            // Vẽ Pill 1 (Vệ sinh)
            int x1 = 8;
            g.setColor(bg1);
            g.fillRoundRect(x1, pillY, pillW1, pillH, 10, 10);
            g.setColor(bd1);
            g.drawRoundRect(x1, pillY, pillW1 - 1, pillH - 1, 10, 10);
            int ty = pillY + (pillH + fm.getAscent() - fm.getDescent()) / 2;
            g.setColor(fg1);
            g.drawString(t1, x1 + (pillW1 - textW1) / 2, ty);

            // Vẽ Pill 2 (Trạng thái)
            int x2 = x1 + pillW1 + gap;
            g.setColor(bg2);
            g.fillRoundRect(x2, pillY, pillW2, pillH, 10, 10);
            g.setColor(bd2);
            g.drawRoundRect(x2, pillY, pillW2 - 1, pillH - 1, 10, 10);
            g.setColor(fg2);
            g.drawString(t2, x2 + (pillW2 - textW2) / 2, ty);

            g.dispose();
        }
    }

    /** Renderer vẽ thanh tiến độ 8px bo tròn và hiển thị số % bên cạnh */
    public static class ProgressRenderer extends JComponent implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private int percent = 100;
        private boolean isSelected = false;
        private int row = 0;

        public ProgressRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
            this.percent = value instanceof Integer n ? n : 100;
            this.isSelected = selected;
            this.row = row;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color cellBg = isSelected ? ROW_SELECTED_BG : ((row % 2 == 0) ? ROW_EVEN_BG : COLOR_PANEL);
            g.setColor(cellBg);
            g.fillRect(0, 0, w, h);

            int padX = 8;
            int barW = 55;
            int barH = 8;
            int barY = (h - barH) / 2;

            // Track nền
            g.setColor(new Color(33, 38, 45));
            g.fillRoundRect(padX, barY, barW, barH, barH, barH);

            // Màu thanh tiến độ: >= 70% xanh lá, 50-69% vàng, < 50% đỏ
            Color fillColor;
            if (percent >= 70) {
                fillColor = COLOR_OK;
            } else if (percent >= 50) {
                fillColor = COLOR_WARN;
            } else {
                fillColor = COLOR_BAD;
            }

            int fillW = (int) Math.round(barW * (Math.max(0, Math.min(100, percent)) / 100.0));
            if (fillW > 0) {
                g.setColor(fillColor);
                g.fillRoundRect(padX, barY, Math.max(fillW, barH), barH, barH, barH);
            }

            // Phần trăm chữ bên cạnh
            String text = percent + "%";
            g.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g.setColor(COLOR_TEXT);
            FontMetrics fm = g.getFontMetrics();
            int textX = padX + barW + 8;
            int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
            g.drawString(text, textX, textY);

            g.dispose();
        }
    }

    /**
     * Renderer vẽ avatar tròn 30px (chữ cái đầu tên riêng in hoa, màu riêng theo hash tên khách)
     * kèm tên khách font cỡ 14 semibold màu #e6edf3.
     */
    public static class AvatarRenderer extends JComponent implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        // Bảng 6 màu avatar chuẩn theo yêu cầu:
        // #2f81f7, #3fb950, #d29922, #a371f7, #f778ba, #39c5cf
        private static final Color[] PALETTE = {
                new Color(0x2f81f7), // #2f81f7
                new Color(0x3fb950), // #3fb950
                new Color(0xd29922), // #d29922
                new Color(0xa371f7), // #a371f7
                new Color(0xf778ba), // #f778ba
                new Color(0x39c5cf)  // #39c5cf
        };

        private static final Font FONT_INITIAL = new Font("Segoe UI", Font.BOLD, 13);
        private static final Font FONT_NAME;

        static {
            Font f = new Font("Segoe UI Semibold", Font.PLAIN, 14);
            if (f.getFamily().toLowerCase().contains("dialog")) {
                f = new Font("Segoe UI", Font.PLAIN, 14).deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD));
            }
            FONT_NAME = f;
        }

        private String name = "";
        private boolean isSelected = false;
        private int row = 0;

        public AvatarRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
            this.name = value == null ? "" : value.toString();
            this.isSelected = selected;
            this.row = row;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color cellBg = isSelected ? ROW_SELECTED_BG : ((row % 2 == 0) ? ROW_EVEN_BG : COLOR_PANEL);
            g.setColor(cellBg);
            g.fillRect(0, 0, w, h);

            int avatarSize = 30;
            int avatarX = 8;
            int avatarY = (h - avatarSize) / 2;

            // Màu chủ đạo theo hash của tên (cùng một tên thì luôn ra cùng một màu)
            Color base = getBaseColor(name);
            Color bgCol = new Color(base.getRed(), base.getGreen(), base.getBlue(), 64);      // Nền độ trong suốt 25% (64/255)
            Color borderCol = new Color(base.getRed(), base.getGreen(), base.getBlue(), 153); // Viền độ trong suốt 60% (153/255)
            Color textCol = new Color(
                    Math.min(255, (int) (base.getRed() * 0.8 + 255 * 0.2)),
                    Math.min(255, (int) (base.getGreen() * 0.8 + 255 * 0.2)),
                    Math.min(255, (int) (base.getBlue() * 0.8 + 255 * 0.2))
            ); // Chữ sáng hơn một chút

            // Vẽ nền hình tròn avatar 30px
            g.setColor(bgCol);
            g.fillOval(avatarX, avatarY, avatarSize, avatarSize);

            // Vẽ viền 1px cùng màu
            g.setColor(borderCol);
            g.drawOval(avatarX, avatarY, avatarSize - 1, avatarSize - 1);

            // Chữ cái đầu của tên riêng (viết HOA, in đậm, cỡ 13)
            String initial = extractInitial(name);
            g.setFont(FONT_INITIAL);
            g.setColor(textCol);
            FontMetrics fm = g.getFontMetrics();
            int ix = avatarX + (avatarSize - fm.stringWidth(initial)) / 2;
            int iy = avatarY + (avatarSize + fm.getAscent() - fm.getDescent()) / 2;
            g.drawString(initial, ix, iy);

            // Tên khách hàng: cách avatar 10px, font cỡ 14 semibold, màu #e6edf3
            int gap = 10;
            int nameX = avatarX + avatarSize + gap;
            g.setFont(FONT_NAME);
            g.setColor(COLOR_TEXT);
            FontMetrics fmName = g.getFontMetrics();
            int nameY = (h + fmName.getAscent() - fmName.getDescent()) / 2;
            g.drawString(name, nameX, nameY);

            g.dispose();
        }

        private static Color getBaseColor(String name) {
            if (name == null || name.isEmpty()) return PALETTE[0];
            int hash = name.hashCode();
            int idx = (hash & 0x7fffffff) % PALETTE.length;
            return PALETTE[idx];
        }

        private String extractInitial(String fullName) {
            if (fullName == null || fullName.trim().isEmpty()) return "?";
            String trimmed = fullName.trim();
            // Lấy chữ cái đầu của tên riêng (từ cuối cùng ví dụ: "Cô Mai" -> "M", "Bác Hùng" -> "H")
            String[] parts = trimmed.split("\\s+");
            String lastWord = parts[parts.length - 1];
            char firstChar = lastWord.charAt(0);
            return String.valueOf(Character.toUpperCase(firstChar));
        }
    }

    /** Renderer vẽ tên xe in đậm ở trên và biển số xe chữ nhỏ MUTED ở dưới */
    public static class CarModelRenderer extends JComponent implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private String model = "";
        private String plate = "";
        private boolean isSelected = false;
        private int row = 0;

        public CarModelRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
            if (value instanceof Car car) {
                this.model = car.getModel();
                this.plate = car.getPlate();
            } else if (value != null) {
                this.model = value.toString();
                this.plate = "";
            } else {
                this.model = "";
                this.plate = "";
            }
            this.isSelected = selected;
            this.row = row;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color cellBg = isSelected ? ROW_SELECTED_BG : ((row % 2 == 0) ? ROW_EVEN_BG : COLOR_PANEL);
            g.setColor(cellBg);
            g.fillRect(0, 0, w, h);

            int x = 8;

            // Dòng 1: Tên model xe đậm
            g.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g.setColor(COLOR_TEXT);
            g.drawString(model, x, 21);

            // Dòng 2: Biển số xe chữ nhỏ MUTED
            if (!plate.isEmpty()) {
                g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g.setColor(COLOR_MUTED);
                g.drawString(plate, x, 38);
            }

            g.dispose();
        }
    }

    /** Renderer chuẩn có lề trái 8px và màu text phù hợp */
    public static class TextCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final boolean muted;
        private final boolean bold;

        public TextCellRenderer(boolean muted, boolean bold) {
            this.muted = muted;
            this.bold = bold;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFoc, int row, int col) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, val, isSel, hasFoc, row, col);
            l.setBackground(isSel ? ROW_SELECTED_BG : ((row % 2 == 0) ? ROW_EVEN_BG : COLOR_PANEL));
            l.setForeground(muted ? COLOR_MUTED : COLOR_TEXT);
            l.setFont(bold ? FONT_BODY_BOLD : FONT_BODY);
            l.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return l;
        }
    }

    // =========================================================================
    // HỘP THOẠI BẢNG XẾP HẠNG & HƯỚNG DẪN CHƠI
    // =========================================================================

    /** Hộp thoại Bảng xếp hạng: thứ hạng 1-2-3 hiển thị bằng chấm tròn số */
    public static void showHighScoresDialog(Component parent, List<HighScore> top) {
        JFrame owner = parent instanceof JFrame f ? f : (JFrame) SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Bảng xếp hạng", true);
        dialog.getContentPane().setBackground(COLOR_BG);

        JLabel title = new JLabel("BẢNG XẾP HẠNG CAO THỦ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(COLOR_TEXT);

        JPanel content = new JPanel(new BorderLayout(14, 14));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        content.add(title, BorderLayout.NORTH);

        if (top.isEmpty()) {
            JLabel empty = new JLabel("<html><center style='color:#7d8590; font-size:14px;'>"
                    + "Chưa có ai hoàn thành 30 ngày.<br/>Hãy chơi và ghi tên bạn vào bảng vàng đầu tiên!</center></html>", SwingConstants.CENTER);
            empty.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
            content.add(empty, BorderLayout.CENTER);
        } else {
            ListTableModel<HighScore> model = new ListTableModel<HighScore>()
                    .column("Hạng", h -> top.indexOf(h) + 1)
                    .column("Người chơi", HighScore::name)
                    .column("Điểm", h -> Format.money(h.score()))
                    .column("Xếp hạng", HighScore::rank)
                    .column("Ngày", HighScore::date);
            model.setRows(top);
            ThemedTable table = new ThemedTable(model);

            // Cột thứ hạng hiển thị chấm tròn số
            table.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable tbl, Object value, boolean selected, boolean focus, int row, int col) {
                    int pos = value instanceof Integer i ? i : row + 1;
                    return new JComponent() {
                        private static final long serialVersionUID = 1L;
                        @Override
                        protected void paintComponent(Graphics g0) {
                            Graphics2D g = (Graphics2D) g0.create();
                            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g.setColor(selected ? ROW_SELECTED_BG : ((row % 2 == 0) ? ROW_EVEN_BG : COLOR_PANEL));
                            g.fillRect(0, 0, getWidth(), getHeight());

                            int size = 24;
                            int x = (getWidth() - size) / 2;
                            int y = (getHeight() - size) / 2;

                            Color circleBg;
                            Color circleFg;
                            if (pos == 1) {
                                circleBg = COLOR_WARN;
                                circleFg = COLOR_BG;
                            } else if (pos == 2) {
                                circleBg = new Color(200, 210, 225);
                                circleFg = COLOR_BG;
                            } else if (pos == 3) {
                                circleBg = new Color(205, 127, 50);
                                circleFg = Color.WHITE;
                            } else {
                                circleBg = COLOR_LINE;
                                circleFg = COLOR_MUTED;
                            }

                            g.setColor(circleBg);
                            g.fillOval(x, y, size, size);

                            g.setFont(new Font("Segoe UI", Font.BOLD, 12));
                            g.setColor(circleFg);
                            FontMetrics fm = g.getFontMetrics();
                            String str = String.valueOf(pos);
                            int tx = x + (size - fm.stringWidth(str)) / 2;
                            int ty = y + (size + fm.getAscent() - fm.getDescent()) / 2;
                            g.drawString(str, tx, ty);
                            g.dispose();
                        }
                    };
                }
            });

            int[] widths = {60, 140, 140, 70, 100};
            for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(530, 240));
            scroll.setBorder(BorderFactory.createLineBorder(COLOR_LINE, 1));
            content.add(scroll, BorderLayout.CENTER);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.add(button("Đóng", null, dialog::dispose));
        content.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /** Hướng dẫn chơi dạng các bước đánh số trong vòng tròn ACCENT, không để lộ thẻ HTML thô */
    public static void showHelpDialog(Component parent) {
        JFrame owner = parent instanceof JFrame f ? f : (JFrame) SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Hướng dẫn chơi Car Tycoon", true);
        dialog.getContentPane().setBackground(COLOR_BG);

        JLabel title = new JLabel("HƯỚNG DẪN CHƠI CAR TYCOON", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(COLOR_TEXT);

        JLabel subtitle = new JLabel("Khởi nghiệp với 15 triệu đồng & 2 chiếc xe cũ - Mục tiêu trở thành Ông trùm trong 30 ngày!", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(COLOR_MUTED);

        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setOpaque(false);
        header.add(title);
        header.add(subtitle);

        JPanel stepsContainer = new JPanel(new GridLayout(0, 1, 10, 10));
        stepsContainer.setOpaque(false);
        stepsContainer.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        stepsContainer.add(createStepCard(1, "Mục tiêu tối thượng",
                "Bạn có 30 ngày để kinh doanh, mua bán, cho thuê xe. Kết thúc ngày 30, điểm số của bạn được tính bằng tổng tiền mặt + giá trị đội xe + uy tín tích lũy. Hãy làm giàu thật nhanh!"));

        stepsContainer.add(createStepCard(2, "Đón khách & Cho thuê xe",
                "Mỗi sáng có nhóm khách đợi thuê. Chọn 1 khách bên trái, 1 xe bên phải rồi bấm Cho thuê →. Khách sẽ trả tiền ngay. Giữ xe phù hợp với yêu cầu của khách để tránh từ chối."));

        stepsContainer.add(createStepCard(3, "Vệ sinh & Bảo dưỡng định kỳ",
                "Khách trả xe xong xe sẽ bị bẩn và hao mòn. Bấm Rửa xe (100.000 đ) và Sửa xe khi cần. Xe dưới 30% tình trạng sẽ bị HỎNG và không thể cho thuê. Giữ xe luôn sạch sẽ tăng uy tín!"));

        stepsContainer.add(createStepCard(4, "Nâng cấp & Mở rộng đội xe",
                "Dùng tiền kiếm được vào Cửa hàng xe mua thêm xe mới. Khi đạt doanh thu yêu cầu bạn sẽ LÊN CẤP, mở khóa các dòng xe xịn (SUV, Van 16 chỗ, Xe sang, Thể thao) và các khách VIP trả nhiều tiền hơn."));

        stepsContainer.add(createStepCard(5, "Chuyển ngày & Tránh phá sản",
                "Bấm Ngày tiếp theo » để sang ngày mới. Khách chưa phục vụ sẽ bỏ đi (mỗi khách -1 uy tín). Mỗi xe ở bãi tốn phí 100.000 đ/ngày. CẢNH BÁO: Nếu tiền âm vào cuối ngày, bạn sẽ PHÁ SẢN ngay!"));

        stepsContainer.add(createStepCard(6, "Bảng xếp hạng vinh danh",
                "Điểm = Tiền mặt + Giá trị xe + Uy tín × 100.000 đ. Xếp hạng: Hạng S (≥ 100 triệu) - Ông trùm thực thụ; Hạng A (≥ 70 triệu); Hạng B (≥ 45 triệu); Hạng C (< 45 triệu)."));

        JScrollPane scroll = new JScrollPane(stepsContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(640, 440));
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_LINE, 1));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.add(button("Đã hiểu, vào game ngay!", COLOR_OK, dialog::dispose));

        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(COLOR_BG);
        root.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static JPanel createStepCard(int stepNum, String title, String body) {
        JPanel card = card(COLOR_PANEL, COLOR_LINE);
        card.setLayout(new BorderLayout(12, 6));
        card.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Vòng tròn ACCENT chứa số bước
        JPanel stepCircle = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(COLOR_ACCENT);
                g.fillOval(0, 0, 26, 26);
                g.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g.setColor(Color.WHITE);
                FontMetrics fm = g.getFontMetrics();
                String s = String.valueOf(stepNum);
                int tx = (26 - fm.stringWidth(s)) / 2;
                int ty = (26 + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(s, tx, ty);
                g.dispose();
            }
        };
        stepCircle.setOpaque(false);
        stepCircle.setPreferredSize(new Dimension(28, 28));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(stepCircle);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(COLOR_TEXT);
        titlePanel.add(titleLbl);

        JLabel text = new JLabel("<html><body style='width: 530px; color: #7d8590; font-family: Segoe UI, sans-serif; font-size: 13px; line-height: 1.5;'>"
                + body + "</body></html>");
        text.setBorder(BorderFactory.createEmptyBorder(4, 36, 4, 4));

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);
        return card;
    }
}
