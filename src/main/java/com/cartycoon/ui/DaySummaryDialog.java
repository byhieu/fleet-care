package com.cartycoon.ui;

import com.cartycoon.logic.DayReport;
import com.cartycoon.model.GameState;
import com.cartycoon.util.Format;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

/**
 * Hộp thoại tổng kết ngày theo phong cách GitHub Dark.
 * Hàng trên 3 ô thống kê (Thu màu OK, Chi màu BAD, Tiền hiện có),
 * bên dưới danh sách sự kiện với icon chấm tròn màu theo từng loại sự kiện.
 */
public final class DaySummaryDialog {

    private DaySummaryDialog() {
    }

    public static void show(Component parent, DayReport report, GameState state) {
        JFrame owner = parent instanceof JFrame f ? f : (JFrame) SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Tổng kết ngày " + report.day(), true);
        dialog.getContentPane().setBackground(UiUtil.COLOR_BG);

        // Header Tiêu đề
        JLabel title = new JLabel("TỔNG KẾT NGÀY " + report.day(), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UiUtil.COLOR_TEXT);

        JLabel subtitle = new JLabel(report.gameOver() ? "Trận đấu đã khép lại!" : "Chuẩn bị đón khách và vận hành bãi xe ngày tiếp theo", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UiUtil.COLOR_MUTED);

        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        header.add(title);
        header.add(subtitle);

        // Hàng trên: đúng 3 ô thống kê (Thu trong ngày, Chi trong ngày, Tiền hiện có)
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 12, 12));
        metricsPanel.setOpaque(false);

        metricsPanel.add(createMetricCard("THU TRONG NGÀY",
                "+" + Format.money(report.income()),
                UiUtil.COLOR_OK));

        metricsPanel.add(createMetricCard("CHI TRONG NGÀY",
                (report.costs() > 0 ? "-" : "") + Format.money(report.costs()),
                UiUtil.COLOR_BAD));

        metricsPanel.add(createMetricCard("TIỀN HIỆN CÓ",
                Format.money(state.getMoney()),
                UiUtil.COLOR_TEXT));

        // Khung chi tiết sự kiện
        JPanel activityCard = UiUtil.card(UiUtil.COLOR_PANEL, UiUtil.COLOR_LINE);
        activityCard.setLayout(new BorderLayout(8, 8));
        activityCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel actTitle = new JLabel("CHI TIẾT CÁC DIỄN BIẾN TRONG NGÀY");
        actTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        actTitle.setForeground(UiUtil.COLOR_MUTED);
        activityCard.add(actTitle, BorderLayout.NORTH);

        JPanel linesPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        linesPanel.setOpaque(false);

        if (report.lines().isEmpty()) {
            JLabel empty = new JLabel("Một ngày yên bình trôi qua, không có sự cố nào phát sinh.");
            empty.setForeground(UiUtil.COLOR_MUTED);
            linesPanel.add(empty);
        } else {
            for (String line : report.lines()) {
                linesPanel.add(createEventRow(line));
            }
        }

        JScrollPane scroll = new JScrollPane(linesPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(580, 220));
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        activityCard.add(scroll, BorderLayout.CENTER);

        // Nút bấm tiếp tục
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        String nextBtnText = report.gameOver() ? "Xem kết quả chung cuộc »" : ("Bắt đầu ngày " + (report.day() + 1) + " »");
        JButton nextBtn = UiUtil.button(nextBtnText, UiUtil.COLOR_OK, dialog::dispose);
        footer.add(nextBtn);

        // Ghép vào khung root
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(UiUtil.COLOR_BG);
        root.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        center.add(metricsPanel, BorderLayout.NORTH);
        center.add(activityCard, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static JPanel createMetricCard(String label, String value, Color textCol) {
        JPanel card = UiUtil.card(UiUtil.COLOR_PANEL, UiUtil.COLOR_LINE);
        card.setLayout(new GridLayout(2, 1, 4, 4));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(UiUtil.COLOR_MUTED);

        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("Segoe UI", Font.BOLD, 17));
        val.setForeground(textCol);

        card.add(lbl);
        card.add(val);
        return card;
    }

    /**
     * Mỗi dòng sự kiện có icon tròn nhỏ màu theo loại:
     * xanh lá = tốt, vàng = cảnh báo, đỏ = mất, xanh dương = thông tin.
     */
    private static JPanel createEventRow(String line) {
        Color dotColor = UiUtil.COLOR_ACCENT; // Mặc định xanh dương thông tin
        String lower = line.toLowerCase();

        if (lower.contains("bỏ đi") || lower.contains("mất uy tín") || lower.contains("phạt") || lower.contains("hỏng")) {
            dotColor = UiUtil.COLOR_BAD; // Đỏ: mất mát, lỗi
        } else if (lower.contains("bão") || lower.contains("trầy") || lower.contains("bẩn") || lower.contains("hao mòn")) {
            dotColor = UiUtil.COLOR_WARN; // Vàng: cảnh báo
        } else if (lower.contains("thuê") || lower.contains("trả xe") || lower.contains("tiền thuê")
                || lower.contains("sạch") || lower.contains("trúng thưởng") || lower.contains("giới thiệu")) {
            dotColor = UiUtil.COLOR_OK; // Xanh lá: tốt, thu nhập
        }

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(48, 54, 61, 100)),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        final Color finalDot = dotColor;
        JComponent dotComp = new JComponent() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(finalDot);
                g.fillOval(4, (getHeight() - 8) / 2, 8, 8);
                g.dispose();
            }
        };
        dotComp.setPreferredSize(new Dimension(18, 18));

        JLabel textLbl = new JLabel("<html><body style='color:#e6edf3; font-family: Segoe UI, sans-serif; font-size:13px; width:480px;'>"
                + line + "</body></html>");

        row.add(dotComp, BorderLayout.WEST);
        row.add(textLbl, BorderLayout.CENTER);
        return row;
    }
}
