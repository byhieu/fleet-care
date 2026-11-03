package com.cartycoon.ui;

import com.cartycoon.logic.DayReport;
import com.cartycoon.model.GameState;
import com.cartycoon.util.Format;

import javax.swing.BorderFactory;
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
import java.awt.GridLayout;

/** The card-based popup after "Ngày tiếp theo": metrics cards, categorized events, dark navy & gold. */
public final class DaySummaryDialog {

    private DaySummaryDialog() {
    }

    public static void show(Component parent, DayReport report, GameState state) {
        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Tổng kết ngày " + report.day(), true);
        dialog.getContentPane().setBackground(UiUtil.NAVY_BG);

        // Header Title
        JLabel title = new JLabel("🌅 TỔNG KẾT NGÀY " + report.day(), SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setForeground(UiUtil.GOLD);

        JLabel subtitle = new JLabel(report.gameOver() ? "Trận đấu đã khép lại!" : "Chuẩn bị đón khách và vận hành bãi xe ngày tiếp theo", SwingConstants.CENTER);
        subtitle.setFont(subtitle.getFont().deriveFont(13f));
        subtitle.setForeground(UiUtil.TEXT_MUTED);

        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        header.add(title);
        header.add(subtitle);

        // 4 Summary Metric Cards (Income, Costs, Cash, Reputation)
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        metricsPanel.setOpaque(false);

        metricsPanel.add(createMetricCard("💰 Thu trong ngày",
                "+" + Format.money(report.income()),
                new Color(20, 60, 36), new Color(34, 197, 94), new Color(110, 231, 145)));

        metricsPanel.add(createMetricCard("💸 Chi trong ngày",
                (report.costs() > 0 ? "-" : "") + Format.money(report.costs()),
                new Color(77, 22, 22), new Color(239, 68, 68), new Color(252, 165, 165)));

        metricsPanel.add(createMetricCard("🏦 Tiền mặt hiện có",
                Format.money(state.getMoney()),
                new Color(69, 45, 12), UiUtil.GOLD, UiUtil.TEXT_GOLD));

        metricsPanel.add(createMetricCard("⭐ Uy tín cửa hàng",
                UiUtil.stars(state.getStars()) + " (" + state.getReputation() + ")",
                new Color(24, 49, 89), new Color(96, 165, 250), new Color(147, 197, 253)));

        // Details Activity List Card
        JPanel activityCard = UiUtil.card(UiUtil.NAVY_CARD, UiUtil.NAVY_BORDER);
        activityCard.setLayout(new BorderLayout(8, 8));
        activityCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel actTitle = new JLabel("📋 Chi tiết các diễn biến trong ngày:");
        actTitle.setFont(actTitle.getFont().deriveFont(Font.BOLD, 14f));
        actTitle.setForeground(UiUtil.GOLD);
        activityCard.add(actTitle, BorderLayout.NORTH);

        JPanel linesPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        linesPanel.setOpaque(false);

        if (report.lines().isEmpty()) {
            JLabel empty = new JLabel("Một ngày yên bình trôi qua, không có biến động bất ngờ nào.");
            empty.setForeground(UiUtil.TEXT_MUTED);
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
        scroll.setPreferredSize(new Dimension(580, 210));
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        activityCard.add(scroll, BorderLayout.CENTER);

        // Footer Action Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        String nextBtnText = report.gameOver() ? "Xem kết quả chung cuộc >>" : ("Bắt đầu ngày " + (report.day() + 1) + " >>");
        footer.add(UiUtil.button(nextBtnText, UiUtil.GOLD, dialog::dispose));

        // Root container
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(UiUtil.NAVY_BG);
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
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

    private static JPanel createMetricCard(String label, String value, Color bg, Color border, Color textCol) {
        JPanel card = UiUtil.card(bg, border);
        card.setLayout(new GridLayout(2, 1, 2, 2));
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 12f));
        lbl.setForeground(new Color(226, 232, 240));

        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(val.getFont().deriveFont(Font.BOLD, 15f));
        val.setForeground(textCol);

        card.add(lbl);
        card.add(val);
        return card;
    }

    private static JPanel createEventRow(String line) {
        String icon = "🔹";
        String lower = line.toLowerCase();
        if (lower.contains("thuê") || lower.contains("trả xe") || lower.contains("tiền thuê")) {
            icon = "🔑";
        } else if (lower.contains("rửa xe") || lower.contains("sạch")) {
            icon = "🫧";
        } else if (lower.contains("sửa")) {
            icon = "🔧";
        } else if (lower.contains("phí bãi") || lower.contains("bãi xe")) {
            icon = "🏢";
        } else if (lower.contains("bỏ đi") || lower.contains("mất uy tín")) {
            icon = "🚶";
        } else if (lower.contains("bão") || lower.contains("trầy") || lower.contains("phạt")) {
            icon = "⚠️";
        } else if (lower.contains("trúng thưởng") || lower.contains("giới thiệu")) {
            icon = "🎉";
        }

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(42, 60, 96, 120)),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(iconLbl.getFont().deriveFont(14f));

        JLabel textLbl = new JLabel("<html><body style='color:#f1f5f9; font-size:12px; width:480px;'>" + line + "</body></html>");

        row.add(iconLbl, BorderLayout.WEST);
        row.add(textLbl, BorderLayout.CENTER);
        return row;
    }
}
