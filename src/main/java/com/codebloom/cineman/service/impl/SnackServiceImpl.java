package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.SnackRequest;
import com.codebloom.cineman.controller.response.SnackResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.SnackEntity;
import com.codebloom.cineman.model.SnackTypeEntity;
import com.codebloom.cineman.repository.SnackRepository;
import com.codebloom.cineman.repository.SnackTypeRepository;
import com.codebloom.cineman.service.SnackService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SnackServiceImpl implements SnackService {

    private final SnackRepository snackRepository;
    private final SnackTypeRepository snackTypeRepository;
    private final ModelMapper mapper;

    @Transactional
    @Override
    public SnackResponse create(SnackRequest request) {
        SnackEntity snack = SnackEntity.builder()
                .snackName(request.getSnackName())
                .unitPrice(request.getUnitPrice())
                .image(request.getImage())
                .description(request.getDescription())
                .isActive(true)
                .build();
        SnackTypeEntity snackType = snackTypeRepository.findById(request.getSnackTypeId())
                .orElseThrow(() -> new DataNotFoundException("Snack Type not found"));
        snack.setSnackType(snackType);
        SnackEntity saved = snackRepository.save(snack);
        return mapper.map(saved, SnackResponse.class);
    }


    @Override
    public SnackResponse update(int id, SnackRequest request) {
        SnackEntity snack = snackRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Snack not found"));
        mapper.map(request, snack);
        SnackTypeEntity snackType = snackTypeRepository.findById(request.getSnackTypeId())
                .orElseThrow(() -> new DataNotFoundException("Snack Type not found"));
        snack.setSnackType(snackType);
        snack.setIsActive(true);
        SnackEntity updated = snackRepository.save(snack);
        return mapper.map(updated, SnackResponse.class);
    }

    @Override
    public void delete(int id) {
        SnackEntity snack = snackRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Snack not found"));
        snack.setIsActive(false);
        snackRepository.save(snack);
    }

    @Override
    public SnackResponse findById(int id) {
        SnackEntity snack = snackRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Snack not found or inactive"));
        return mapper.map(snack, SnackResponse.class);
    }

    @Override
    public List<SnackResponse> findAll() {
        return snackRepository.findByIsActive(true)
                .stream()
                .map(snack -> mapper.map(snack, SnackResponse.class))
                .toList();
    }
}





