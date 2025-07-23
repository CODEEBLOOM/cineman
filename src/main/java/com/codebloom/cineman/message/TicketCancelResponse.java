package com.codebloom.cineman.message;

import com.codebloom.cineman.model.TicketEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TicketCancelResponse {

    @Builder.Default
    private TicketResponseMessage.MessageType type = TicketResponseMessage.MessageType.TICKET_DELETED;
    private Double totalMoney;
    private Long ticketId;
    private Long userId;
    private Integer columnIndex;
    private Integer rowIndex;
}
