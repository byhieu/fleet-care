package com.cartycoon.model;

public enum CarStatus {
    FREE("Rảnh"),
    RENTED("Đang cho thuê"),
    BROKEN("Hỏng");

    private final String label;

    CarStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
