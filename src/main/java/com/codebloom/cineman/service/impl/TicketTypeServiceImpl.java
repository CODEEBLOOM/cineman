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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j(topic = "TICKET-TYPE-SERVICE")
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;

    @Override
    @Transactional
    public TicketTypeEntity create(TicketTypeRequest request) {
        TicketTypeEntity existingTicketType = ticketTypeRepository.findByName(request.getName()).orElse(null);
        if (existingTicketType != null) {
            if (Boolean.TRUE.equals(existingTicketType.getStatus())) {
                throw new DataExistingException("Ticket type already exists with name: " + request.getName());
            }
            existingTicketType.setDescription(request.getDescription());
            existingTicketType.setPrice(request.getPrice());
            existingTicketType.setStatus(true);
            return ticketTypeRepository.save(existingTicketType);
        }

        TicketTypeEntity ticketTypeEntity = TicketTypeEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .status(true)
                .build();
        return ticketTypeRepository.save(ticketTypeEntity);
    }

    @Override
    @Transactional
    public TicketTypeEntity update(Integer id, TicketTypeRequest request) {
        TicketTypeEntity ticketTypeEntity = findById(id);
        TicketTypeEntity existingTicketType = ticketTypeRepository.findByName(request.getName()).orElse(null);
        if (existingTicketType != null && !existingTicketType.getId().equals(id)) {
            throw new DataExistingException("Ticket type already exists with name: " + request.getName());
        }
        ticketTypeEntity.setName(request.getName());
        ticketTypeEntity.setDescription(request.getDescription());
        ticketTypeEntity.setPrice(request.getPrice());
        ticketTypeEntity.setStatus(true);
        return ticketTypeRepository.save(ticketTypeEntity);
    }

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
    @Transactional
    public void delete(Integer id) {
        TicketTypeEntity ticketTypeEntity = findById(id);
        ticketTypeEntity.setStatus(false);
        ticketTypeRepository.save(ticketTypeEntity);
    }
}
