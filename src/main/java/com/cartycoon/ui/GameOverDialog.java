package com.cartycoon.ui;

import com.cartycoon.logic.ScoreBoard;
import com.cartycoon.logic.ScoreBoard.HighScore;
import com.cartycoon.model.Car;
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
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.util.List;

/**
 * Hộp thoại kết thúc ván chơi theo phong cách GitHub Dark.
 * Hiển thị xếp hạng lớn, 4 ô thống kê chi tiết điểm số và bảng vàng cao thủ với chấm tròn số.
 */
public class GameOverDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public GameOverDialog(JFrame owner, GameState state, ScoreBoard board) {
        super(owner, "Kết thúc ván chơi", true);
        getContentPane().setBackground(UiUtil.COLOR_BG);

        long score = ScoreBoard.score(state);
        String rank = ScoreBoard.rank(score);
        List<HighScore> top = board.add(state.getPlayerName(), score, LocalDate.now());

        // Header lý do kết thúc
        JLabel reason = new JLabel(state.getGameOverReason(), SwingConstants.CENTER);
        reason.setFont(new Font("Segoe UI", Font.BOLD, 17));
        reason.setForeground(UiUtil.COLOR_TEXT);

        // Thẻ hiển thị Hạng chữ lớn
        JPanel rankCard = UiUtil.card(UiUtil.COLOR_PANEL, UiUtil.COLOR_LINE);
        rankCard.setLayout(new GridLayout(2, 1, 4, 4));
        rankCard.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        Color rankCol = switch (rank) {
            case "S" -> UiUtil.COLOR_WARN;
            case "A" -> UiUtil.COLOR_OK;
            case "B" -> UiUtil.COLOR_ACCENT;
            default -> UiUtil.COLOR_BAD;
        };

        JLabel rankLabel = new JLabel("HẠNG " + rank, SwingConstants.CENTER);
        rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 46));
        rankLabel.setForeground(rankCol);

        JLabel rankText = new JLabel(ScoreBoard.rankText(rank), SwingConstants.CENTER);
        rankText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        rankText.setForeground(UiUtil.COLOR_TEXT);

        rankCard.add(rankLabel);
        rankCard.add(rankText);

        JPanel head = new JPanel(new BorderLayout(8, 8));
        head.setOpaque(false);
        head.add(reason, BorderLayout.NORTH);
        head.add(rankCard, BorderLayout.CENTER);

        // 4 ô chi tiết điểm số
        long carValue = state.getCars().stream().mapToLong(Car::getSellPrice).sum();
        JPanel detailGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        detailGrid.setOpaque(false);
        detailGrid.setBorder(BorderFactory.createEmptyBorder(6, 4, 10, 4));

        detailGrid.add(createStatCard("Tiền mặt cuối kỳ", Format.money(state.getMoney()), UiUtil.COLOR_TEXT));
        detailGrid.add(createStatCard("Giá trị " + state.getCars().size() + " xe", Format.money(carValue), UiUtil.COLOR_TEXT));
        detailGrid.add(createStatCard("Uy tín (" + state.getReputation() + " × 100k)", Format.money(state.getReputation() * 100_000L), UiUtil.COLOR_TEXT));
        detailGrid.add(createStatCard("TỔNG ĐIỂM", Format.money(score), UiUtil.COLOR_OK));

        // Bảng xếp hạng cao thủ (dùng chấm tròn số cho thứ hạng 1-2-3)
        ListTableModel<HighScore> model = new ListTableModel<HighScore>()
                .column("Hạng", h -> top.indexOf(h) + 1)
                .column("Người chơi", HighScore::name)
                .column("Điểm", h -> Format.money(h.score()))
                .column("Xếp hạng", HighScore::rank)
                .column("Ngày", HighScore::date);
        model.setRows(top);
        UiUtil.ThemedTable table = new UiUtil.ThemedTable(model);

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
                        g.setColor(selected ? UiUtil.ROW_SELECTED_BG : ((row % 2 == 0) ? UiUtil.ROW_EVEN_BG : UiUtil.COLOR_PANEL));
                        g.fillRect(0, 0, getWidth(), getHeight());

                        int size = 24;
                        int x = (getWidth() - size) / 2;
                        int y = (getHeight() - size) / 2;

                        Color circleBg;
                        Color circleFg;
                        if (pos == 1) {
                            circleBg = UiUtil.COLOR_WARN;
                            circleFg = UiUtil.COLOR_BG;
                        } else if (pos == 2) {
                            circleBg = new Color(200, 210, 225);
                            circleFg = UiUtil.COLOR_BG;
                        } else if (pos == 3) {
                            circleBg = new Color(205, 127, 50);
                            circleFg = Color.WHITE;
                        } else {
                            circleBg = UiUtil.COLOR_LINE;
                            circleFg = UiUtil.COLOR_MUTED;
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

        int[] widths = {55, 140, 130, 70, 95};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UiUtil.COLOR_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtil.COLOR_LINE, 1));
        scroll.setPreferredSize(new Dimension(500, 160));

        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.setOpaque(false);
        center.add(detailGrid, BorderLayout.NORTH);
        center.add(UiUtil.createSectionCard("BẢNG XẾP HẠNG CAO THỦ", null, scroll), BorderLayout.CENTER);

        // Nút hành động
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setOpaque(false);
        JButton closeBtn = UiUtil.button("Về màn hình chính", UiUtil.COLOR_OK, this::dispose);
        buttons.add(closeBtn);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(UiUtil.COLOR_BG);
        root.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        root.add(head, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(owner);
    }

    private static JPanel createStatCard(String label, String value, Color valColor) {
        JPanel p = UiUtil.card(UiUtil.COLOR_PANEL, UiUtil.COLOR_LINE);
        p.setLayout(new GridLayout(2, 1, 2, 2));
        p.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(UiUtil.COLOR_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 15));
        v.setForeground(valColor);

        p.add(l);
        p.add(v);
        return p;
    }
}
