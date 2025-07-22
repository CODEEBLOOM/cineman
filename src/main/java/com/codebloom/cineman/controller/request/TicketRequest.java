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
    @Builder.Default
    private TicketType ticketType = TicketType.ADULT;
    private Long seatId;
    private Long invoiceId;

}
