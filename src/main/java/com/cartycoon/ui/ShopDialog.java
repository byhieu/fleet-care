package com.cartycoon.ui;

import com.cartycoon.logic.CarCatalog;
import com.cartycoon.logic.CarCatalog.CarOffer;
import com.cartycoon.logic.GameEngine;
import com.cartycoon.model.Car;
import com.cartycoon.model.Level;
import com.cartycoon.util.Format;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

/** The car shop with dark navy & gold styling, icons, and tooltips. */
public class ShopDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final transient GameEngine engine;
    private final ListTableModel<CarOffer> model = new ListTableModel<>();
    private final UiUtil.ThemedTable table = new UiUtil.ThemedTable(model);
    private final JLabel moneyLabel = new JLabel();

    public ShopDialog(JFrame owner, GameEngine engine) {
        super(owner, "Cửa hàng xe", true);
        this.engine = engine;
        getContentPane().setBackground(UiUtil.NAVY_BG);
        Level level = engine.getState().getLevel();

        model.column("Xe", o -> UiUtil.getCarIcon(o.preview().getTypeName()) + " " + o.model())
                .column("Loại", o -> o.preview().getTypeName())
                .column("Chỗ", o -> o.preview().getSeats())
                .column("Giá mua", o -> Format.money(o.preview().getBuyPrice()))
                .column("Cho thuê/ngày", o -> Format.money(o.preview().getDailyPrice()))
                .column("Hao mòn/ngày", o -> o.preview().getWearPerDay() + "%")
                .column("Mở khóa", o -> o.isUnlocked(level) ? "✔ Có thể mua" : "🔒 Cấp " + o.minLevel());

        model.setRows(CarCatalog.OFFERS);
        table.setDefaultRenderer(Object.class, new LockedRenderer(level));

        int[] widths = {140, 110, 50, 110, 120, 100, 120};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Rich Tooltips for shop offers
        table.setToolTipProvider(this::shopOfferToolTip);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        buttons.setOpaque(false);
        moneyLabel.setForeground(UiUtil.GOLD);
        moneyLabel.setFont(moneyLabel.getFont().deriveFont(Font.BOLD, 14f));
        buttons.add(moneyLabel);
        buttons.add(UiUtil.button("Mua xe này", UiUtil.GREEN, this::buy));
        buttons.add(UiUtil.button("Đóng", null, this::dispose));

        JLabel hint = new JLabel("💡 Mẹo: Xe đắt cho thuê được giá cao hơn, nhưng chi phí bảo dưỡng và sửa chữa cũng khác nhau.");
        hint.setForeground(UiUtil.TEXT_MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UiUtil.NAVY_BG);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtil.NAVY_BORDER, 1));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiUtil.NAVY_BG);
        root.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        root.add(hint, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        updateMoney();
        setSize(860, 420);
        setLocationRelativeTo(owner);
    }

    private String shopOfferToolTip(int row, int col) {
        if (row < 0 || row >= model.getRowCount()) return null;
        CarOffer offer = model.getRow(row);
        if (offer == null) return null;

        Car preview = offer.preview();
        boolean unlocked = offer.isUnlocked(engine.getState().getLevel());
        boolean canAfford = engine.getState().getMoney() >= preview.getBuyPrice();

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='width: 300px; padding: 4px; font-family: Segoe UI, sans-serif;'>");
        sb.append("<b style='color: #fde047; font-size: 13px;'>")
                .append(UiUtil.getCarIcon(preview.getTypeName())).append(" ")
                .append(offer.model()).append(" (").append(preview.getTypeName()).append(")</b><br/>");
        sb.append("<hr style='border: 0; border-top: 1px solid #2a3c60; margin: 4px 0;'/>");
        sb.append("<b>💵 Giá mua:</b> ").append(Format.money(preview.getBuyPrice())).append("<br/>");
        sb.append("<b>💰 Thu nhập cho thuê:</b> ").append(Format.money(preview.getDailyPrice())).append("/ngày<br/>");
        sb.append("<b>📉 Hao mòn mỗi ngày thuê:</b> ").append(preview.getWearPerDay()).append("%<br/>");
        sb.append("<b>👥 Số chỗ ngồi:</b> ").append(preview.getSeats()).append(" chỗ<br/>");
        sb.append("<hr style='border: 0; border-top: 1px solid #2a3c60; margin: 4px 0;'/>");
        if (!unlocked) {
            sb.append("<span style='color:#f87171;'>🔒 Yêu cầu Cửa hàng đạt Cấp ").append(offer.minLevel()).append(" để mở khóa.</span>");
        } else if (!canAfford) {
            sb.append("<span style='color:#facc15;'>⚠️ Bạn chưa đủ tiền mua (cần thêm ")
                    .append(Format.money(preview.getBuyPrice() - engine.getState().getMoney())).append(").</span>");
        } else {
            sb.append("<span style='color:#4ade80;'>✔ Đủ điều kiện mua ngay!</span>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    private void buy() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UiUtil.warn(this, "Hãy chọn một chiếc xe trong danh sách.");
            return;
        }
        CarOffer offer = model.getRow(row);
        UiUtil.run(this, () -> {
            Car car = engine.buy(offer);
            UiUtil.info(this, "Mua xe thành công", "Bạn đã mua " + car + ". Xe đã có trong garage!");
        });
        updateMoney();
    }

    private void updateMoney() {
        moneyLabel.setText("Tiền của bạn: " + Format.money(engine.getState().getMoney()) + "   ");
    }

    /** Themed renderer for locked and unlocked cars in dark navy palette. */
    private static class LockedRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final Level level;

        LockedRenderer(Level level) {
            this.level = level;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, col);
            CarOffer offer = ((ListTableModel<?>) table.getModel()).getRow(row) instanceof CarOffer o ? o : null;
            boolean unlocked = offer != null && offer.isUnlocked(level);

            if (selected) {
                label.setBackground(UiUtil.NAVY_SELECTED);
                label.setForeground(Color.WHITE);
                return label;
            }

            Color altBg = (row % 2 == 0) ? UiUtil.NAVY_BG : UiUtil.NAVY_PANEL_ALT;
            if (unlocked) {
                label.setBackground(altBg);
                if (col == 3 || col == 4) {
                    label.setForeground(UiUtil.TEXT_GOLD);
                } else if (col == 6) {
                    label.setForeground(UiUtil.STATUS_GREEN_TEXT);
                } else {
                    label.setForeground(UiUtil.TEXT_PRIMARY);
                }
            } else {
                label.setBackground(new Color(11, 16, 30));
                label.setForeground(new Color(100, 116, 139));
                if (col == 6) {
                    label.setForeground(new Color(248, 113, 113));
                }
            }
            return label;
        }
    }
}
