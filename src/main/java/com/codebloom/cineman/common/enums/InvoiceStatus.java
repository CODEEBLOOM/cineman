package com.codebloom.cineman.common.enums;

public enum InvoiceStatus {
    /*Trạng thái chờ thanh toán*/
    PENDING,

    /* Trạng thái đã thanh toán */
    PAID,

    /* Trạng thái đã hủy */
    CANCELLED,

    /*Trạng thái đã hoàn tiền*/
    REFUNDED,

    /*Trạng thái đang xử lý*/
    PROCESSING
}
