package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.TicketTypeRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.TicketTypeEntity;
import com.codebloom.cineman.repository.TicketTypeRepository;
import com.codebloom.cineman.service.TicketTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TICKET-TYPE-SERVICE")
public class TicketTypeServiceImpl implements TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;

    @Override
    public List<TicketTypeEntity> findAll() {
        return ticketTypeRepository.findAllByStatus(true);
    }

    @Override
    public TicketTypeEntity findById(Integer id) {
        return ticketTypeRepository.findByIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Ticket type not found with id: " + id));
    }

    @Override
    public TicketTypeEntity create(TicketTypeRequest request) {
        ticketTypeRepository.findByNameAndStatus(request.getName(), true)
                .ifPresent(ticketType -> {
                    throw new DataExistingException("Ticket type already exists with name: " + request.getName());
                });

        TicketTypeEntity ticketType = TicketTypeEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .status(true)
                .build();
        return ticketTypeRepository.save(ticketType);
    }

    @Override
    public TicketTypeEntity update(Integer id, TicketTypeRequest request) {
        TicketTypeEntity ticketType = ticketTypeRepository.findByIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Ticket type not found with id: " + id));

        ticketTypeRepository.findByNameAndStatusAndIdNot(request.getName(), true, id)
                .ifPresent(existingTicketType -> {
                    throw new DataExistingException("Ticket type already exists with name: " + request.getName());
                });

        ticketType.setName(request.getName());
        ticketType.setDescription(request.getDescription());
        ticketType.setPrice(request.getPrice());
        ticketType.setStatus(true);
        return ticketTypeRepository.save(ticketType);
    }

    @Override
    public void delete(Integer id) {
        TicketTypeEntity ticketType = ticketTypeRepository.findByIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Ticket type not found with id: " + id));
        ticketType.setStatus(false);
        ticketTypeRepository.save(ticketType);
    }
}
