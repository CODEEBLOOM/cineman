package com.codebloom.cineman.controller.websocket;

import com.codebloom.cineman.common.enums.TicketStatus;
import com.codebloom.cineman.controller.response.DummyTicket;
import com.codebloom.cineman.controller.response.SeatResponse;
import com.codebloom.cineman.message.TicketCancelResponse;
import com.codebloom.cineman.message.TicketMessage;
import com.codebloom.cineman.message.TicketResponseMessage;
import com.codebloom.cineman.model.TicketEntity;
import com.codebloom.cineman.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import static com.codebloom.cineman.message.TicketResponseMessage.MessageType.TICKET_CREATED;
import static com.codebloom.cineman.message.TicketResponseMessage.MessageType.TICKET_DELETED;

@Controller
@RequiredArgsConstructor
@Slf4j(topic = "CHOOSE_SEAT_WS_CONTROLLER")
public class ChooseSeatWSController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final TicketService ticketService;

    @MessageMapping("/seat/choose-seat") /* /cineman/app/seat/choose-seat*/
    public void selectSeat(@Payload TicketMessage message) {
        log.info("selectSeat(), message: {}", message);
        TicketEntity ticketEntity = ticketService.create(message.getContent());
        TicketResponseMessage ticketMessage = TicketResponseMessage.builder()
                .type(TICKET_CREATED)
                .content(
                        DummyTicket.builder()
                                .id(ticketEntity.getId())
                                .seat(
                                        SeatResponse.builder()
                                                .id(ticketEntity.getSeat().getId())
                                                .rowIndex(ticketEntity.getSeat().getRowIndex())
                                                .columnIndex(ticketEntity.getSeat().getColumnIndex())
                                                .label(ticketEntity.getSeat().getLabel())
                                                .seatType(ticketEntity.getSeat().getSeatType())
                                                .status(ticketEntity.getSeat().getStatus())
                                                .build()
                                )
                                .status(TicketStatus.SELECTED)
                                .build()
                )
                .totalMoney(ticketService.getTotalMoneyOfTickets(ticketEntity.getInvoice().getId()))
                .userId(message.getUserId())
                .build();
        messagingTemplate.convertAndSend(
                "/cineman/topic/seat-map/show-time/" + message.getContent().getShowTimeId(),
                ticketMessage
        );
    }

    @MessageMapping("/seat/cancel-seat") /* /cineman/app/seat/cancel-seat*/
    public void cancelSeat(@Payload TicketMessage message) {
        log.info("cancelSeat(), message: {}", message);
        TicketEntity ticketEntity = ticketService.delete(message.getTicketId());
        TicketCancelResponse ticketMessage = TicketCancelResponse.builder()
                .type(TICKET_DELETED)
                .ticketId(message.getTicketId())
                .userId(message.getUserId())
                .columnIndex(ticketEntity.getSeat().getColumnIndex())
                .rowIndex(ticketEntity.getSeat().getRowIndex())
                .totalMoney(ticketService.getTotalMoneyOfTickets(ticketEntity.getInvoice().getId()))
                .build();
        messagingTemplate.convertAndSend(
                "/cineman/topic/seat-map/show-time/" + message.getContent().getShowTimeId(),
                ticketMessage
        );
    }
}
