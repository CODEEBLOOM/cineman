package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.TicketType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TicketRequest {

    private Long showTimeId;
    private TicketType ticketType;
    private Long seatId;
    private Long invoiceId;

}
