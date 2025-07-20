package com.codebloom.cineman.controller.response;


import com.codebloom.cineman.common.enums.TicketStatus;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.model.TicketTypeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class TicketResponse {

    private Long id;
    private Double price;
    private Date createBooking;
    private Integer limitTime;
    private TicketStatus status;
    private TicketTypeEntity ticketType;
    private ShowTimeEntity showTime;

}
