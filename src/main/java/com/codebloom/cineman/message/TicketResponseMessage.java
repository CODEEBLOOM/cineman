package com.codebloom.cineman.message;

import com.codebloom.cineman.controller.response.DummyTicket;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TicketResponseMessage {

    @Builder.Default
    private MessageType type = MessageType.TICKET_CREATED;
    private DummyTicket content;
    private Long userId;

    public enum MessageType {
        TICKET_CREATED,
        TICKET_DELETED,
        TICKET_UPDATED
    }

}
