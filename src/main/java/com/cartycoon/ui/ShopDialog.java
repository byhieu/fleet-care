package com.cartycoon.ui;

import com.cartycoon.logic.CarCatalog;
import com.cartycoon.logic.CarCatalog.CarOffer;
import com.cartycoon.logic.GameEngine;
import com.cartycoon.model.Car;
import com.cartycoon.model.Level;
import com.cartycoon.util.Format;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

/**
 * Cửa hàng xe theo phong cách GitHub Dark.
 * Bảng thông tin xe rõ ràng, hàng khóa làm mờ nhẹ, nút mua màu xanh lá.
 */
public class ShopDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final transient GameEngine engine;
    private final ListTableModel<CarOffer> model = new ListTableModel<>();
    private final UiUtil.ThemedTable table = new UiUtil.ThemedTable(model);
    private final JLabel moneyLabel = new JLabel();

    public ShopDialog(JFrame owner, GameEngine engine) {
        super(owner, "Cửa hàng xe", true);
        this.engine = engine;
        getContentPane().setBackground(UiUtil.COLOR_BG);
        Level level = engine.getState().getLevel();

        model.column("Xe", CarOffer::model)
                .column("Loại", o -> o.preview().getTypeName())
                .column("Chỗ", o -> o.preview().getSeats() + " chỗ")
                .column("Giá mua", o -> Format.money(o.preview().getBuyPrice()))
                .column("Cho thuê/ngày", o -> Format.money(o.preview().getDailyPrice()))
                .column("Hao mòn/ngày", o -> o.preview().getWearPerDay() + "%")
                .column("Mở khóa", o -> o.isUnlocked(level) ? "Có thể mua" : "Cấp " + o.minLevel());

        model.setRows(CarCatalog.OFFERS);

        // Renderers cho các cột
        table.getColumnModel().getColumn(0).setCellRenderer(new ShopCellRenderer(level, 0));
        table.getColumnModel().getColumn(1).setCellRenderer(new ShopCellRenderer(level, 1));
        table.getColumnModel().getColumn(2).setCellRenderer(new ShopCellRenderer(level, 2));
        table.getColumnModel().getColumn(3).setCellRenderer(new ShopCellRenderer(level, 3));
        table.getColumnModel().getColumn(4).setCellRenderer(new ShopCellRenderer(level, 4));
        table.getColumnModel().getColumn(5).setCellRenderer(new ShopCellRenderer(level, 5));
        table.getColumnModel().getColumn(6).setCellRenderer(new ShopCellRenderer(level, 6));

        int[] widths = {140, 110, 65, 120, 125, 95, 110};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.setToolTipProvider(this::shopOfferToolTip);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        buttons.setOpaque(false);

        moneyLabel.setForeground(UiUtil.COLOR_OK);
        moneyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        buttons.add(moneyLabel);

        JButton buyBtn = UiUtil.button("Mua xe này", UiUtil.COLOR_OK, this::buy);
        JButton closeBtn = UiUtil.button("Đóng", null, this::dispose);
        buttons.add(buyBtn);
        buttons.add(closeBtn);

        JLabel hint = new JLabel("Mẹo: Xe đắt tiền cho thuê được giá cao hơn nhưng chi phí bảo dưỡng và sửa chữa cũng tương ứng.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(UiUtil.COLOR_MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(6, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(table);
        UiUtil.styleScrollPane(scroll);
        scroll.getViewport().setBackground(UiUtil.COLOR_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtil.COLOR_LINE, 1));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiUtil.COLOR_BG);
        root.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        root.add(hint, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        updateMoney();
        setSize(880, 440);
        setLocationRelativeTo(owner);
    }

    private String shopOfferToolTip(int row, int col) {
        if (row < 0 || row >= model.getRowCount()) return null;
        CarOffer offer = model.getRow(row);
        if (offer == null) return null;

        Car preview = offer.preview();
        boolean unlocked = offer.isUnlocked(engine.getState().getLevel());
        boolean canAfford = engine.getState().getMoney() >= preview.getBuyPrice();

        return "<html><body style='width: 290px; padding: 4px; font-family: Segoe UI, sans-serif; color: #e6edf3;'>"
                + "<b style='color: #2f81f7; font-size: 13px;'>" + offer.model() + " (" + preview.getTypeName() + ")</b><br/>"
                + "<hr style='border: 0; border-top: 1px solid #30363d; margin: 4px 0;'/>"
                + "<span style='color:#7d8590;'>Giá mua:</span> " + Format.money(preview.getBuyPrice()) + "<br/>"
                + "<span style='color:#7d8590;'>Thu nhập cho thuê:</span> " + Format.money(preview.getDailyPrice()) + "/ngày<br/>"
                + "<span style='color:#7d8590;'>Hao mòn mỗi ngày:</span> " + preview.getWearPerDay() + "%<br/>"
                + "<span style='color:#7d8590;'>Số chỗ ngồi:</span> " + preview.getSeats() + " chỗ<br/>"
                + "<hr style='border: 0; border-top: 1px solid #30363d; margin: 4px 0;'/>"
                + (!unlocked ? "<span style='color:#f85149;'>Yêu cầu Cửa hàng đạt Cấp " + offer.minLevel() + " để mở khóa.</span>"
                : (!canAfford ? "<span style='color:#d29922;'>Bạn chưa đủ tiền (cần thêm " + Format.money(preview.getBuyPrice() - engine.getState().getMoney()) + ").</span>"
                : "<span style='color:#3fb950; font-weight:bold;'>Đủ điều kiện mua ngay!</span>"))
                + "</body></html>";
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

    /** Renderer riêng cho ShopDialog tuân thủ bảng màu GitHub Dark */
    private static class ShopCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final Level level;
        private final int colIndex;
        private final UiUtil.PillRenderer pill = new UiUtil.PillRenderer();

        ShopCellRenderer(Level level, int colIndex) {
            this.level = level;
            this.colIndex = colIndex;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
            CarOffer offer = ((ListTableModel<?>) table.getModel()).getRow(row) instanceof CarOffer o ? o : null;
            boolean unlocked = offer != null && offer.isUnlocked(level);

            if (colIndex == 1 || colIndex == 6) {
                return pill.getTableCellRendererComponent(table, value, selected, focus, row, col);
            }

            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, col);
            label.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

            if (selected) {
                label.setBackground(UiUtil.ROW_SELECTED_BG);
                label.setForeground(UiUtil.COLOR_TEXT);
                return label;
            }

            Color cellBg = (row % 2 == 0) ? UiUtil.ROW_EVEN_BG : UiUtil.COLOR_PANEL;
            label.setBackground(cellBg);

            if (unlocked) {
                label.setForeground(colIndex == 0 ? UiUtil.COLOR_TEXT : (colIndex == 3 || colIndex == 4 ? UiUtil.COLOR_OK : UiUtil.COLOR_TEXT));
                label.setFont(colIndex == 0 ? UiUtil.FONT_BODY_BOLD : UiUtil.FONT_BODY);
            } else {
                label.setForeground(UiUtil.COLOR_MUTED);
                label.setFont(UiUtil.FONT_BODY);
            }

            return label;
        }
    }
}
