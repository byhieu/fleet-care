package com.cartycoon.ui;

import com.cartycoon.model.Car;
import com.cartycoon.model.CarStatus;
import com.cartycoon.model.Customer;
import com.cartycoon.model.GameState;
import com.cartycoon.model.Rental;
import com.cartycoon.util.Format;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Khung "GARAGE CỦA TÔI": Toàn bộ đội xe, tình trạng tiến độ 8px, trạng thái dạng pill.
 * Thiết kế theo phong cách GitHub Dark hiện đại, tối giản.
 */
public class GaragePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ListTableModel<Car> model = new ListTableModel<>();
    private final UiUtil.ThemedTable table = new UiUtil.ThemedTable(model);
    private final JLabel countLabel = new JLabel("0 XE");
    private GameState state;
    private Customer customer;

    public GaragePanel() {
        super(new BorderLayout());
        setBackground(UiUtil.COLOR_BG);
        setMinimumSize(new Dimension(420, 200));

        // Khai báo 6 cột tinh gọn theo yêu cầu (gộp Chỗ vào Loại, gộp Vệ sinh + Trạng thái)
        model.column("Xe", c -> c)
                .column("Loại", this::typeAndSeats)
                .column("Giá/ngày", c -> Format.money(c.getDailyPrice()))
                .column("Tình trạng", Car::getCondition)
                .column("Đã chạy", c -> Format.number(c.getKm()) + " km")
                .column("Trạng thái", c -> c);

        // Renderers cho từng cột
        table.getColumnModel().getColumn(0).setCellRenderer(new UiUtil.CarModelRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new UiUtil.PillRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new UiUtil.TextCellRenderer(false, false));
        table.getColumnModel().getColumn(3).setCellRenderer(new UiUtil.ProgressRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new UiUtil.TextCellRenderer(true, false));
        table.getColumnModel().getColumn(5).setCellRenderer(new UiUtil.DualStatusRenderer());

        // Phân bổ preferredWidth theo tỉ lệ: Xe 20%, Loại 16%, Giá/ngày 14%, Tình trạng 20%, Đã chạy 12%, Trạng thái 18%
        // (tính trên cơ sở bảng ~800px)
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(0).setMinWidth(130);

        table.getColumnModel().getColumn(1).setPreferredWidth(128);
        table.getColumnModel().getColumn(1).setMinWidth(105);

        table.getColumnModel().getColumn(2).setPreferredWidth(112);
        table.getColumnModel().getColumn(2).setMinWidth(90);

        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setMinWidth(120);

        table.getColumnModel().getColumn(4).setPreferredWidth(96);
        table.getColumnModel().getColumn(4).setMinWidth(85);

        table.getColumnModel().getColumn(5).setPreferredWidth(144);
        table.getColumnModel().getColumn(5).setMinWidth(130);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Tooltip xe chi tiết
        table.setToolTipProvider(this::carToolTip);

        JScrollPane scroll = new JScrollPane(table);
        UiUtil.styleScrollPane(scroll);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(UiUtil.COLOR_PANEL);

        // Header khung với tiêu đề chữ hoa nhỏ màu MUTED và số lượng màu ACCENT
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UiUtil.COLOR_LINE),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JLabel titleLabel = new JLabel("GARAGE CỦA TÔI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(UiUtil.COLOR_MUTED);

        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(UiUtil.COLOR_ACCENT);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);

        // Container bo góc 10px nền PANEL viền LINE
        JPanel card = UiUtil.card(UiUtil.COLOR_PANEL, UiUtil.COLOR_LINE);
        card.setLayout(new BorderLayout());
        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    /** Gộp tên loại và số chỗ (vd: "SUV 7 chỗ", "Xe 16 chỗ", "Sedan 5 chỗ") */
    private String typeAndSeats(Car c) {
        if (c == null) return "";
        String type = c.getTypeName();
        return type.contains("chỗ") ? type : (type + " " + c.getSeats() + " chỗ");
    }

    private String carToolTip(int row, int col) {
        if (row < 0 || row >= model.getRowCount()) return null;
        Car car = model.getRow(row);
        if (car == null) return null;

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='width: 300px; padding: 4px; font-family: Segoe UI, sans-serif; color: #e6edf3;'>");
        sb.append("<b style='color: #2f81f7; font-size: 13px;'>")
                .append(car.getModel()).append(" (").append(car.getTypeName()).append(") - ")
                .append(car.getPlate()).append("</b><br/>");
        sb.append("<hr style='border: 0; border-top: 1px solid #30363d; margin: 4px 0;'/>");
        sb.append("<span style='color:#7d8590;'>Giá thuê:</span> ").append(Format.money(car.getDailyPrice())).append("/ngày<br/>");
        sb.append("<span style='color:#7d8590;'>Giá mua mới:</span> ").append(Format.money(car.getBuyPrice())).append("<br/>");
        sb.append("<span style='color:#7d8590;'>Giá bán lúc này:</span> ").append(Format.money(car.getSellPrice())).append("<br/>");
        sb.append("<span style='color:#7d8590;'>Chi phí sửa 100%:</span> ").append(Format.money(car.getRepairCost())).append("<br/>");
        sb.append("<span style='color:#7d8590;'>Tình trạng máy:</span> ").append(car.getCondition()).append("% (");
        if (car.getCondition() < Car.BROKEN_BELOW) {
            sb.append("<span style='color:#f85149; font-weight:bold;'>HỎNG NẶNG</span>");
        } else if (car.getCondition() < 60) {
            sb.append("<span style='color:#d29922;'>Hao mòn, nên sửa</span>");
        } else {
            sb.append("<span style='color:#3fb950;'>Rất tốt</span>");
        }
        sb.append(")<br/>");
        sb.append("<span style='color:#7d8590;'>Vệ sinh:</span> ")
                .append(car.isClean() ? "<span style='color:#3fb950;'>Sạch sẽ</span>" : "<span style='color:#d29922;'>Bẩn (cần rửa)</span>").append("<br/>");
        sb.append("<span style='color:#7d8590;'>Đã chạy:</span> ").append(Format.number(car.getKm())).append(" km<br/>");
        sb.append("<span style='color:#7d8590;'>Trạng thái:</span> ").append(statusText(car)).append("<br/>");

        if (customer != null) {
            sb.append("<hr style='border: 0; border-top: 1px solid #30363d; margin: 4px 0;'/>");
            sb.append("<span style='color:#7d8590;'>Với khách [").append(customer.getName()).append("]:</span> ");
            if (car.getStatus() != CarStatus.FREE) {
                sb.append("<span style='color:#7d8590;'>Xe không rảnh</span>");
            } else if (customer.accepts(car)) {
                sb.append("<span style='color:#3fb950; font-weight:bold;'>Phù hợp! Trả ")
                        .append(Format.money(customer.price(car))).append("</span>");
            } else {
                sb.append("<span style='color:#f85149;'>Không hợp yêu cầu</span>");
            }
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    private String statusText(Car car) {
        if (car.getStatus() != CarStatus.RENTED) return car.getStatus().toString();
        int days = state != null ? state.getRentals().stream()
                .filter(r -> r.getCar() == car)
                .mapToInt(Rental::getDaysLeft)
                .findFirst().orElse(0) : 0;
        return days > 0 ? "Đang thuê (" + days + " ngày)" : "Đang thuê";
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        table.repaint();
    }

    public void refresh(GameState newState) {
        this.state = newState;
        Car selected = getSelected();
        model.setRows(newState.getCars());
        countLabel.setText(newState.getCars().size() + " XE");
        int index = selected == null ? -1 : model.indexOf(selected);
        if (index >= 0 && index < model.getRowCount()) {
            table.setRowSelectionInterval(index, index);
        }
    }

    public Car getSelected() {
        int row = table.getSelectedRow();
        return row < 0 || row >= model.getRowCount() ? null : model.getRow(row);
    }

    // =========================================================================
    // LỚP TƯƠNG THÍCH NGƯỢC
    // =========================================================================

    public static class ConditionRenderer extends JComponent implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final UiUtil.ProgressRenderer delegate = new UiUtil.ProgressRenderer();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
            return delegate.getTableCellRendererComponent(table, value, selected, focus, row, col);
        }
    }

    public static class ColorRenderer extends JComponent implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final UiUtil.PillRenderer delegate = new UiUtil.PillRenderer();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
            return delegate.getTableCellRendererComponent(table, value, selected, focus, row, col);
        }
    }
}
