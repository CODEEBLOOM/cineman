package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.TicketTypeRequest;
import com.codebloom.cineman.model.TicketTypeEntity;

import java.util.List;

public interface TicketTypeService {

    TicketTypeEntity create(TicketTypeRequest request);
    TicketTypeEntity update(Integer id, TicketTypeRequest request);
    List<TicketTypeEntity> findAll();
    TicketTypeEntity findById(Integer id);
    void delete(Integer id);
}
