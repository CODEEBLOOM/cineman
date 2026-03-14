package com.codebloom.cineman.controller.util;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Component
public class NumberFormatter {

    private static final DecimalFormatSymbols VIETNAM_SYMBOLS;
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        // Cấu hình ký tự phân tách cho Việt Nam
        VIETNAM_SYMBOLS = new DecimalFormatSymbols(new Locale("vi", "VN"));
        VIETNAM_SYMBOLS.setGroupingSeparator('.');
        VIETNAM_SYMBOLS.setDecimalSeparator(',');

        // Mẫu định dạng: dấu . phân tách nghìn, , phân tách thập phân, luôn 1 số sau dấu phẩy
        DECIMAL_FORMAT = new DecimalFormat("#,##0.0", VIETNAM_SYMBOLS);
    }

    /**
     * Định dạng số kiểu double sang dạng 100.000,0
     * @param number số cần định dạng
     * @return chuỗi số đã định dạng
     */
    public static String format(double number) {
        return DECIMAL_FORMAT.format(number);
    }

    /**
     * Định dạng số nguyên sang dạng 100.000,0
     * @param number số nguyên cần định dạng
     * @return chuỗi số đã định dạng
     */
    public static String format(int number) {
        return DECIMAL_FORMAT.format(number);
    }

}

