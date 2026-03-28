package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.TicketTypeRequest;
import com.codebloom.cineman.model.TicketTypeEntity;

import java.util.List;

public interface TicketTypeService {

    List<TicketTypeEntity> findAll();

    TicketTypeEntity findById(Integer id);

    TicketTypeEntity create(TicketTypeRequest request);

    TicketTypeEntity update(Integer id, TicketTypeRequest request);

    void delete(Integer id);
}
