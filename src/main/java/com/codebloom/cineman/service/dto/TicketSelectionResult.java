package com.codebloom.cineman.service.dto;

import com.codebloom.cineman.controller.response.SeatResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketSelectionResult {

    private final Long ticketId;
    private final Long invoiceId;
    private final Double price;
    private final SeatResponse seat;
}
