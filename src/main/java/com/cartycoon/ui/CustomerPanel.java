package com.cartycoon.ui;

import com.cartycoon.model.CarStatus;
import com.cartycoon.model.Customer;
import com.cartycoon.model.GameState;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.util.function.Consumer;

/** "Khách hôm nay": the customers waiting this morning. */
public class CustomerPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ListTableModel<Customer> model = new ListTableModel<>();
    private final UiUtil.ThemedTable table = new UiUtil.ThemedTable(model);
    private GameState state;

    public CustomerPanel() {
        super(new BorderLayout());
        setBackground(UiUtil.NAVY_BG);

        model.column("Khách", Customer::getName)
                .column("Loại khách", c -> UiUtil.getCustomerIcon(c.getTypeName()) + " " + c.getTypeName())
                .column("Yêu cầu", Customer::getRequest)
                .column("Ngày", Customer::getDays)
                .column("Có xe?", c -> hasCarFor(c) ? "Có" : "Chưa");

        int[] widths = {85, 120, 200, 45, 60};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Use ColorRenderer for "Có xe?" badge
        table.getColumnModel().getColumn(4).setCellRenderer(new GaragePanel.ColorRenderer());

        // Rich Tooltips for customer table
        table.setToolTipProvider(this::customerToolTip);

        JLabel hint = new JLabel("💡 Mẹo: Chọn 1 khách ở đây, rồi chọn 1 xe bên phải và bấm \"Cho thuê →\".");
        hint.setForeground(UiUtil.TEXT_MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UiUtil.NAVY_BG);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtil.NAVY_BORDER, 1));
        add(UiUtil.titled("Khách hôm nay", scroll), BorderLayout.CENTER);
        add(hint, BorderLayout.SOUTH);
    }

    private String customerToolTip(int row, int col) {
        if (row < 0 || row >= model.getRowCount()) return null;
        Customer c = model.getRow(row);
        if (c == null) return null;

        boolean available = hasCarFor(c);
        String typeDesc = switch (c.getTypeName()) {
            case "Sinh viên" -> "Chỉ nhận xe tối đa 900k/ngày, trả 80% giá thuê niêm yết, không chê xe bẩn.";
            case "Gia đình" -> "Cần xe 7 chỗ trở lên, yêu cầu xe phải sạch sẽ và còn tốt.";
            case "Dân chơi" -> "Chỉ nhận xe thể thao! Sẵn sàng trả x1.5 giá thuê nhưng chạy hao mòn gấp đôi.";
            case "Sếp lớn" -> "Chỉ nhận Sedan hoặc Xe sang, yêu cầu xe phải sạch và tình trạng ≥ 80%. Thưởng thêm 10% tip!";
            case "Đoàn du lịch" -> "Cần xe 16 chỗ để chở đoàn, yêu cầu xe phải sạch sẽ.";
            default -> "Khách hàng tiềm năng của cửa hàng.";
        };

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='width: 300px; padding: 4px; font-family: Segoe UI, sans-serif;'>");
        sb.append("<b style='color: #fde047; font-size: 13px;'>")
                .append(UiUtil.getCustomerIcon(c.getTypeName())).append(" ")
                .append(c.getName()).append(" (").append(c.getTypeName()).append(")</b><br/>");
        sb.append("<hr style='border: 0; border-top: 1px solid #2a3c60; margin: 4px 0;'/>");
        sb.append("<b>⏱ Thời gian thuê:</b> ").append(c.getDays()).append(" ngày<br/>");
        sb.append("<b>📋 Yêu cầu:</b> ").append(c.getRequest()).append("<br/>");
        sb.append("<b>💡 Đặc điểm:</b> ").append(typeDesc).append("<br/>");
        sb.append("<b>🚗 Tình trạng bãi xe:</b> ")
                .append(available ? "<span style='color:#4ade80;'>Đã có xe sẵn sàng phục vụ!</span>"
                        : "<span style='color:#f87171;'>Chưa có xe phù hợp rảnh lúc này</span>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private boolean hasCarFor(Customer c) {
        return state != null && state.getCars().stream()
                .anyMatch(car -> car.getStatus() == CarStatus.FREE && c.accepts(car));
    }

    public void refresh(GameState newState) {
        this.state = newState;
        Customer selected = getSelected();
        model.setRows(newState.getCustomers());
        int index = selected == null ? -1 : model.indexOf(selected);
        if (index >= 0) table.setRowSelectionInterval(index, index);
    }

    public Customer getSelected() {
        int row = table.getSelectedRow();
        return row < 0 ? null : model.getRow(row);
    }

    /** Called with the newly selected customer (or null). */
    public void onSelect(Consumer<Customer> listener) {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) listener.accept(getSelected());
        });
    }
}
