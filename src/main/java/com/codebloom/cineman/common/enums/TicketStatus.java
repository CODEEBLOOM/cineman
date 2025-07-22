package com.codebloom.cineman.common.enums;

public enum TicketStatus {
    EMPTY,
    SELECTED,
    HOLDED,
    SOLD,
    BOOKED,

    /* Trạng thái chờ thanh toán */
    PENDING,

    /* Vé đã có hóa đơn được thanh toán thành công*/
    USED
}
