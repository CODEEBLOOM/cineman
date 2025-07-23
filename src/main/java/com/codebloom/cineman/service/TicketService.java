package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.TicketRequest;
import com.codebloom.cineman.controller.response.DummyTicket;
import com.codebloom.cineman.controller.response.TicketResponse;
import com.codebloom.cineman.model.TicketEntity;

import java.util.List;

public interface TicketService {

    TicketResponse findById(Integer ticketId);

    List<DummyTicket> findAllTicketsByShowTimeIdAndUserId(Long showTimeId, Long userId);

    TicketEntity create(TicketRequest request);

    TicketResponse update(Long ticketId, TicketRequest request);

    TicketEntity delete(Long ticketId);

    void clearTicketOutTimeLimit();

    List<TicketEntity> findByInvoiceId(Long invoiceId);

    Double getTotalMoneyOfTickets(Long invoiceId);

}
