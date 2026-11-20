package com.cartycoon.ui;

import com.cartycoon.model.CarStatus;
import com.cartycoon.model.Customer;
import com.cartycoon.model.GameState;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;

/**
 * Khung "KHÁCH HÔM NAY": Danh sách khách đang chờ thuê xe sáng nay.
 * Phong cách GitHub Dark với avatar tròn, nhãn pill và khung bo góc 10px.
 */
public class CustomerPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ListTableModel<Customer> model = new ListTableModel<>();
    private final UiUtil.ThemedTable table = new UiUtil.ThemedTable(model);
    private final JLabel countLabel = new JLabel("0 KHÁCH");
    private GameState state;

    public CustomerPanel() {
        super(new BorderLayout());
        setBackground(UiUtil.COLOR_BG);
        setMinimumSize(new Dimension(420, 200));

        // Khai báo các cột theo đúng chuẩn GitHub Dark
        model.column("Khách", Customer::getName)
                .column("Loại", Customer::getTypeName)
                .column("Yêu cầu", Customer::getRequest)
                .column("Số ngày", Customer::getDays)
                .column("Xe", c -> hasCarFor(c) ? "Có xe" : "Chưa có");

        // Gán các Renderer riêng biệt
        table.getColumnModel().getColumn(0).setCellRenderer(new UiUtil.AvatarRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new UiUtil.PillRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new UiUtil.TextCellRenderer(false, false));

        // Cột Số ngày căn giữa lề đẹp
        var numRenderer = new UiUtil.TextCellRenderer(false, false);
        numRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(numRenderer);

        table.getColumnModel().getColumn(4).setCellRenderer(new UiUtil.PillRenderer());

        // Cấu hình kích thước cột: Số ngày (min 70px), Xe (min 80px), Yêu cầu co giãn chính
        table.getColumnModel().getColumn(0).setPreferredWidth(130);
        table.getColumnModel().getColumn(0).setMinWidth(115);

        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setMinWidth(100);

        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setMinWidth(120);

        table.getColumnModel().getColumn(3).setPreferredWidth(75);
        table.getColumnModel().getColumn(3).setMinWidth(70);

        table.getColumnModel().getColumn(4).setPreferredWidth(85);
        table.getColumnModel().getColumn(4).setMinWidth(80);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Tooltip phong cách GitHub Dark
        table.setToolTipProvider(this::customerToolTip);

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

        JLabel titleLabel = new JLabel("KHÁCH HÔM NAY");
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

    private String customerToolTip(int row, int col) {
        if (row < 0 || row >= model.getRowCount()) return null;
        Customer c = model.getRow(row);
        if (c == null) return null;

        // Tooltip xem đầy đủ yêu cầu khi rê vào cột Yêu cầu
        if (col == 2) {
            return "<html><body style='padding: 4px; font-family: Segoe UI, sans-serif; color: #e6edf3;'>"
                    + "<b style='color: #2f81f7;'>Yêu cầu của " + c.getName() + ":</b> " + c.getRequest()
                    + "</body></html>";
        }

        boolean available = hasCarFor(c);
        String typeDesc = switch (c.getTypeName()) {
            case "Sinh viên" -> "Chỉ nhận xe tối đa 900k/ngày, trả 80% giá thuê, không chê xe bẩn.";
            case "Gia đình" -> "Cần xe 7 chỗ trở lên, yêu cầu xe phải sạch sẽ và còn tốt.";
            case "Dân chơi" -> "Chỉ nhận xe thể thao! Sẵn sàng trả x1.5 giá thuê nhưng chạy hao mòn gấp đôi.";
            case "Sếp lớn" -> "Chỉ nhận Sedan hoặc Xe sang, yêu cầu xe phải sạch và tình trạng ≥ 80%. Thưởng thêm 10% tip!";
            case "Đoàn du lịch" -> "Cần xe 16 chỗ để chở đoàn, yêu cầu xe phải sạch sẽ.";
            default -> "Khách hàng tiềm năng của cửa hàng.";
        };

        return "<html><body style='width: 280px; padding: 4px; font-family: Segoe UI, sans-serif; color: #e6edf3;'>"
                + "<b style='color: #2f81f7; font-size: 13px;'>" + c.getName() + " (" + c.getTypeName() + ")</b><br/>"
                + "<hr style='border: 0; border-top: 1px solid #30363d; margin: 4px 0;'/>"
                + "<span style='color: #7d8590;'>Thời gian thuê:</span> " + c.getDays() + " ngày<br/>"
                + "<span style='color: #7d8590;'>Yêu cầu:</span> " + c.getRequest() + "<br/>"
                + "<span style='color: #7d8590;'>Đặc điểm:</span> " + typeDesc + "<br/>"
                + "<span style='color: #7d8590;'>Tình trạng bãi:</span> "
                + (available ? "<span style='color:#3fb950; font-weight:bold;'>Có xe phù hợp sẵn sàng!</span>"
                : "<span style='color:#f85149; font-weight:bold;'>Chưa có xe phù hợp rảnh</span>")
                + "</body></html>";
    }

    private boolean hasCarFor(Customer c) {
        return state != null && state.getCars().stream()
                .anyMatch(car -> car.getStatus() == CarStatus.FREE && c.accepts(car));
    }

    public void refresh(GameState newState) {
        this.state = newState;
        Customer selected = getSelected();
        model.setRows(newState.getCustomers());
        countLabel.setText(newState.getCustomers().size() + " KHÁCH");
        int index = selected == null ? (newState.getCustomers().isEmpty() ? -1 : 0) : model.indexOf(selected);
        if (index >= 0 && index < model.getRowCount()) {
            table.setRowSelectionInterval(index, index);
        }
    }

    public Customer getSelected() {
        int row = table.getSelectedRow();
        return row < 0 || row >= model.getRowCount() ? null : model.getRow(row);
    }

    /** Đăng ký bộ lắng nghe khi người chơi chọn khách */
    public void onSelect(Consumer<Customer> listener) {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) listener.accept(getSelected());
        });
    }
}
