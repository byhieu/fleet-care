package com.cartycoon.ui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Generic table model: a list of objects plus one getter per column.
 * Example: new ListTableModel&lt;Car&gt;().column("Biển số", Car::getPlate)
 */
public class ListTableModel<T> extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final List<String> names = new ArrayList<>();
    private final List<Function<T, Object>> getters = new ArrayList<>();
    private List<T> rows = new ArrayList<>();

    public ListTableModel<T> column(String name, Function<T, Object> getter) {
        names.add(name);
        getters.add(getter);
        fireTableStructureChanged(); // lets a JTable that already uses this model create the new column
        return this;
    }

    public void setRows(List<T> newRows) {
        rows = new ArrayList<>(newRows);
        fireTableDataChanged();
    }

    public T getRow(int index) {
        return rows.get(index);
    }

    public int indexOf(T row) {
        return rows.indexOf(row);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return names.size();
    }

    @Override
    public String getColumnName(int column) {
        return names.get(column);
    }

    @Override
    public Object getValueAt(int row, int column) {
        return getters.get(column).apply(rows.get(row));
    }
}
