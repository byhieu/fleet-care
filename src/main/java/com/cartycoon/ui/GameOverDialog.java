package com.cartycoon.ui;

import com.cartycoon.logic.ScoreBoard;
import com.cartycoon.logic.ScoreBoard.HighScore;
import com.cartycoon.model.Car;
import com.cartycoon.model.GameState;
import com.cartycoon.util.Format;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

/** End of the game: score, rank and the top 5 table in dark navy & gold. */
public class GameOverDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public GameOverDialog(JFrame owner, GameState state, ScoreBoard board) {
        super(owner, "Kết thúc ván chơi", true);
        getContentPane().setBackground(UiUtil.NAVY_BG);

        long score = ScoreBoard.score(state);
        String rank = ScoreBoard.rank(score);
        List<HighScore> top = board.add(state.getPlayerName(), score, LocalDate.now());

        // Header: Reason
        JLabel reason = new JLabel(state.getGameOverReason(), SwingConstants.CENTER);
        reason.setFont(reason.getFont().deriveFont(Font.BOLD, 17f));
        reason.setForeground(UiUtil.TEXT_PRIMARY);

        // Rank Card
        JPanel rankCard = UiUtil.card(UiUtil.NAVY_CARD, UiUtil.NAVY_BORDER);
        rankCard.setLayout(new GridLayout(2, 1, 2, 2));
        rankCard.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        Color rankCol = switch (rank) {
            case "S" -> UiUtil.GOLD;
            case "A" -> new Color(52, 211, 153);
            case "B" -> new Color(96, 165, 250);
            default -> new Color(248, 113, 113);
        };

        JLabel rankLabel = new JLabel("HẠNG " + rank, SwingConstants.CENTER);
        rankLabel.setFont(rankLabel.getFont().deriveFont(Font.BOLD, 46f));
        rankLabel.setForeground(rankCol);

        JLabel rankText = new JLabel(ScoreBoard.rankText(rank), SwingConstants.CENTER);
        rankText.setFont(rankText.getFont().deriveFont(Font.BOLD, 15f));
        rankText.setForeground(UiUtil.TEXT_PRIMARY);

        rankCard.add(rankLabel);
        rankCard.add(rankText);

        JPanel head = new JPanel(new BorderLayout(8, 8));
        head.setOpaque(false);
        head.add(reason, BorderLayout.NORTH);
        head.add(rankCard, BorderLayout.CENTER);

        // Score Details Breakdown Cards
        long carValue = state.getCars().stream().mapToLong(Car::getSellPrice).sum();
        JPanel detailGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        detailGrid.setOpaque(false);
        detailGrid.setBorder(BorderFactory.createEmptyBorder(6, 4, 10, 4));

        detailGrid.add(createStatCard("💵 Tiền mặt cuối kỳ", Format.money(state.getMoney()), UiUtil.TEXT_PRIMARY));
        detailGrid.add(createStatCard("🚗 Giá trị " + state.getCars().size() + " xe", Format.money(carValue), UiUtil.TEXT_PRIMARY));
        detailGrid.add(createStatCard("⭐ Uy tín (" + state.getReputation() + " x 100k)", Format.money(state.getReputation() * 100_000L), UiUtil.TEXT_PRIMARY));
        detailGrid.add(createStatCard("🏆 TỔNG ĐIỂM", Format.money(score), UiUtil.GOLD));

        // High Scores Table
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
        UiUtil.ThemedTable table = new UiUtil.ThemedTable(model);
        int[] widths = {55, 140, 130, 50, 95};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UiUtil.NAVY_BG);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtil.NAVY_BORDER, 1));
        scroll.setPreferredSize(new Dimension(500, 160));

        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.setOpaque(false);
        center.add(detailGrid, BorderLayout.NORTH);
        center.add(UiUtil.titled("Bảng xếp hạng cao thủ", scroll), BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setOpaque(false);
        buttons.add(UiUtil.button("Về màn hình chính", UiUtil.GOLD, this::dispose));

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(UiUtil.NAVY_BG);
        root.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        root.add(head, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(owner);
    }

    private static JPanel createStatCard(String label, String value, Color valColor) {
        JPanel p = UiUtil.card(UiUtil.NAVY_CARD, UiUtil.NAVY_BORDER);
        p.setLayout(new GridLayout(2, 1, 2, 2));
        p.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel l = new JLabel(label);
        l.setFont(l.getFont().deriveFont(12f));
        l.setForeground(UiUtil.TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(v.getFont().deriveFont(Font.BOLD, 15f));
        v.setForeground(valColor);

        p.add(l);
        p.add(v);
        return p;
    }
}
