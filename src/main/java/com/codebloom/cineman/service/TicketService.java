package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.TicketRequest;
import com.codebloom.cineman.controller.response.DummyTicket;
import com.codebloom.cineman.controller.response.TicketResponse;

import java.util.List;

public interface TicketService {

    TicketResponse findById(Integer ticketId);

    List<DummyTicket> findAllByShowTimeId(Long showTimeId);

    TicketResponse create(TicketRequest request);

    TicketResponse update(Integer ticketId, TicketRequest request);

    void delete(Integer ticketId);


}
