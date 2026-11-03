package com.cartycoon.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/** Vietnamese number style: 12.500.000 đ */
public final class Format {
    private static final DecimalFormatSymbols VN = new DecimalFormatSymbols();

    static {
        VN.setGroupingSeparator('.');
        VN.setDecimalSeparator(',');
    }

    private Format() {
    }

    public static String number(long value) {
        return new DecimalFormat("#,##0", VN).format(value);
    }

    public static String money(long value) {
        return number(value) + " đ";
    }
}
