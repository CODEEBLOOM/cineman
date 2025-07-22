package com.codebloom.cineman.message;

import com.codebloom.cineman.controller.request.TicketRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TicketMessage {

    @Builder.Default
    private MessageType type = MessageType.TICKET_CREATE;
    private TicketRequest content;
    private Long userId;


    public enum MessageType {
        TICKET_CREATE,
        TICKET_CANCEL,
        TICKET_PAID
    }

}
