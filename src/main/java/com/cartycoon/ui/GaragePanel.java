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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

/** "Garage của tôi": every car with its condition bar and a colored status. */
public class GaragePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ListTableModel<Car> model = new ListTableModel<>();
    private final UiUtil.ThemedTable table = new UiUtil.ThemedTable(model);
    private GameState state;
    private Customer customer; // the customer selected on the left, to show which cars fit

    public GaragePanel() {
        super(new BorderLayout());
        setBackground(UiUtil.NAVY_BG);

        model.column("Xe", Car::getModel)
                .column("Biển số", Car::getPlate)
                .column("Loại", c -> UiUtil.getCarIcon(c.getTypeName()) + " " + c.getTypeName())
                .column("Chỗ", Car::getSeats)
                .column("Giá/ngày", c -> Format.money(c.getDailyPrice()))
                .column("Tình trạng", Car::getCondition)
                .column("Vệ sinh", c -> c.isClean() ? "Sạch" : "Bẩn")
                .column("Km", c -> Format.number(c.getKm()))
                .column("Trạng thái", this::statusText)
                .column("Với khách chọn", this::fitText);

        int[] widths = {110, 105, 115, 45, 95, 95, 65, 65, 125, 140};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new ConditionRenderer());
        ColorRenderer colors = new ColorRenderer();
        table.getColumnModel().getColumn(6).setCellRenderer(colors);
        table.getColumnModel().getColumn(8).setCellRenderer(colors);
        table.getColumnModel().getColumn(9).setCellRenderer(colors);

        // Dynamic Rich Tooltip for Garage Table
        table.setToolTipProvider(this::carToolTip);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UiUtil.NAVY_BG);
        scroll.setBorder(BorderFactory.createLineBorder(UiUtil.NAVY_BORDER, 1));
        add(UiUtil.titled("Garage của tôi", scroll), BorderLayout.CENTER);
    }

    private String carToolTip(int row, int col) {
        if (row < 0 || row >= model.getRowCount()) return null;
        Car car = model.getRow(row);
        if (car == null) return null;

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='width: 320px; padding: 4px; font-family: Segoe UI, sans-serif;'>");
        sb.append("<b style='color: #fde047; font-size: 13px;'>")
                .append(UiUtil.getCarIcon(car.getTypeName())).append(" ")
                .append(car.getModel()).append(" (").append(car.getTypeName()).append(") - ")
                .append(car.getPlate()).append("</b><br/>");
        sb.append("<hr style='border: 0; border-top: 1px solid #2a3c60; margin: 4px 0;'/>");
        sb.append("<b>💰 Giá cho thuê:</b> ").append(Format.money(car.getDailyPrice())).append("/ngày<br/>");
        sb.append("<b>💵 Giá mua mới:</b> ").append(Format.money(car.getBuyPrice())).append("<br/>");
        sb.append("<b>🏷️ Giá thanh lý lúc này:</b> ").append(Format.money(car.getSellPrice())).append("<br/>");
        sb.append("<b>🔧 Chi phí sửa lên 100%:</b> ").append(Format.money(car.getRepairCost())).append("<br/>");
        sb.append("<b>📊 Tình trạng máy móc:</b> ").append(car.getCondition()).append("% (");
        if (car.getCondition() < Car.BROKEN_BELOW) {
            sb.append("<span style='color:#f87171;'>HỎNG NẶNG! Không thể cho thuê</span>");
        } else if (car.getCondition() < 60) {
            sb.append("<span style='color:#facc15;'>Hao mòn, nên sửa sớm</span>");
        } else {
            sb.append("<span style='color:#4ade80;'>Hoạt động rất tốt</span>");
        }
        sb.append(")<br/>");
        sb.append("<b>🫧 Vệ sinh:</b> ").append(car.isClean() ? "<span style='color:#4ade80;'>Sạch sẽ</span>" : "<span style='color:#facc15;'>Bẩn (Cần rửa -100k)</span>").append("<br/>");
        sb.append("<b>🛣️ Đã chạy:</b> ").append(Format.number(car.getKm())).append(" km<br/>");
        sb.append("<b>📍 Trạng thái:</b> ").append(statusText(car)).append("<br/>");

        if (customer != null) {
            sb.append("<hr style='border: 0; border-top: 1px solid #2a3c60; margin: 4px 0;'/>");
            sb.append("<b>👤 Với khách [").append(customer.getName()).append("]:</b> ");
            if (car.getStatus() != CarStatus.FREE) {
                sb.append("<span style='color:#94a3b8;'>Xe hiện không rảnh để cho thuê</span>");
            } else if (customer.accepts(car)) {
                sb.append("<span style='color:#4ade80;'>Rất phù hợp! Khách sẵn sàng trả ")
                        .append(Format.money(customer.price(car))).append("</span>");
            } else {
                sb.append("<span style='color:#f87171;'>Không hợp yêu cầu (kiểm tra loại xe, số chỗ, vệ sinh hoặc giá)</span>");
            }
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    private String statusText(Car car) {
        if (car.getStatus() != CarStatus.RENTED) return car.getStatus().toString();
        int days = state.getRentals().stream().filter(r -> r.getCar() == car).mapToInt(Rental::getDaysLeft).findFirst().orElse(0);
        return "Đang thuê (" + days + " ngày)";
    }

    /** What the selected customer would pay for this car, or why it does not fit. */
    private String fitText(Car car) {
        if (customer == null) return "";
        if (car.getStatus() != CarStatus.FREE) return "";
        if (!customer.accepts(car)) return "Không hợp";
        return "Hợp: " + Format.money(customer.price(car));
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        table.repaint();
    }

    public void refresh(GameState newState) {
        this.state = newState;
        Car selected = getSelected();
        model.setRows(newState.getCars());
        int index = selected == null ? -1 : model.indexOf(selected);
        if (index >= 0) table.setRowSelectionInterval(index, index);
    }

    public Car getSelected() {
        int row = table.getSelectedRow();
        return row < 0 ? null : model.getRow(row);
    }

    /** Condition as a sleek rounded pill bar: green when good, yellow when worn, red when broken. */
    public static class ConditionRenderer extends JComponent implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private int condition = 100;

        public ConditionRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
            this.condition = value instanceof Integer n ? n : 100;
            setBackground(selected ? UiUtil.NAVY_SELECTED : ((row % 2 == 0) ? UiUtil.NAVY_BG : UiUtil.NAVY_PANEL_ALT));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Fill cell background
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());

            int padX = 6;
            int padY = 5;
            int barW = getWidth() - padX * 2;
            int barH = getHeight() - padY * 2;
            if (barW <= 0 || barH <= 0) {
                g.dispose();
                return;
            }

            int arc = barH; // rounded pill

            // Track background
            g.setColor(new Color(25, 36, 60));
            g.fillRoundRect(padX, padY, barW, barH, arc, arc);

            // Progress bar colors
            Color fillStart;
            Color fillEnd;
            if (condition < Car.BROKEN_BELOW) {
                fillStart = new Color(220, 38, 38);
                fillEnd = new Color(248, 113, 113);
            } else if (condition < 60) {
                fillStart = new Color(217, 119, 6);
                fillEnd = new Color(250, 204, 21);
            } else {
                fillStart = new Color(16, 185, 129);
                fillEnd = new Color(52, 211, 153);
            }

            int fillW = (int) Math.round(barW * (Math.max(0, Math.min(100, condition)) / 100.0));
            if (fillW > 0) {
                Shape clip = new RoundRectangle2D.Float(padX, padY, barW, barH, arc, arc);
                g.setClip(clip);
                g.setPaint(new GradientPaint(padX, padY, fillStart, padX + barW, padY, fillEnd));
                g.fillRoundRect(padX, padY, Math.max(fillW, arc), barH, arc, arc);
                g.setClip(null);
            }

            // Subtle border
            g.setColor(new Color(45, 62, 98));
            g.drawRoundRect(padX, padY, barW - 1, barH - 1, arc, arc);

            // Percentage text
            String text = condition + "%";
            Font font = getFont().deriveFont(Font.BOLD, 12f);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int tx = padX + (barW - fm.stringWidth(text)) / 2;
            int ty = padY + (barH + fm.getAscent() - fm.getDescent()) / 2;

            g.setColor(new Color(0, 0, 0, 160));
            g.drawString(text, tx + 1, ty + 1);
            g.setColor(Color.WHITE);
            g.drawString(text, tx, ty);

            g.dispose();
        }
    }

    /** Themed badge renderer for words "Rảnh", "Hỏng", "Bẩn", "Hợp"... */
    public static class ColorRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, col);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));

            if (selected) {
                label.setBackground(UiUtil.NAVY_SELECTED);
                label.setForeground(Color.WHITE);
                return label;
            }

            String text = value == null ? "" : value.toString();
            if (text.equals("Rảnh") || text.startsWith("Hợp") || text.equals("Sạch") || text.equals("Có")) {
                label.setBackground(UiUtil.STATUS_GREEN_BG);
                label.setForeground(UiUtil.STATUS_GREEN_TEXT);
            } else if (text.startsWith("Đang thuê")) {
                label.setBackground(UiUtil.STATUS_BLUE_BG);
                label.setForeground(UiUtil.STATUS_BLUE_TEXT);
            } else if (text.equals("Hỏng") || text.equals("Không hợp")) {
                label.setBackground(UiUtil.STATUS_RED_BG);
                label.setForeground(UiUtil.STATUS_RED_TEXT);
            } else if (text.equals("Bẩn") || text.equals("Chưa")) {
                label.setBackground(UiUtil.STATUS_YELLOW_BG);
                label.setForeground(UiUtil.STATUS_YELLOW_TEXT);
            } else {
                label.setBackground((row % 2 == 0) ? UiUtil.NAVY_BG : UiUtil.NAVY_PANEL_ALT);
                label.setForeground(UiUtil.TEXT_PRIMARY);
            }
            return label;
        }
    }
}
