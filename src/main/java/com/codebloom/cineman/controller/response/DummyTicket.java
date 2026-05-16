package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DummyTicket {

    private Long id;
    private Double price;
    private SeatResponse seat;
    private TicketStatus status;

}
