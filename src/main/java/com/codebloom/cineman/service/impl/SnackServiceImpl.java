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

import java.util.Collections;
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
        SnackTypeEntity snackType = snackTypeRepository.findByIdAndIsActive(request.getSnackTypeId(), true)
                .orElseThrow(() -> new DataNotFoundException("Snack Type not found"));
        snack.setSnackType(snackType);
        SnackEntity saved = snackRepository.save(snack);
        return convert(saved);
    }


    @Override
    public SnackResponse update(int id, SnackRequest request) {
        SnackEntity snack = snackRepository.findByIdAndIsActive(id, true)
                .orElseThrow(() -> new DataNotFoundException("Snack not found"));
        SnackTypeEntity snackType = snackTypeRepository.findByIdAndIsActive(request.getSnackTypeId(), true)
                .orElseThrow(() -> new DataNotFoundException("Snack Type not found"));
        snack.setSnackName(request.getSnackName());
        snack.setUnitPrice(request.getUnitPrice());
        snack.setImage(request.getImage());
        snack.setDescription(request.getDescription());
        snack.setSnackType(snackType);
        SnackEntity updated = snackRepository.save(snack);
        return convert(updated);
    }

    @Transactional
    @Override
    public void delete(int id) {
        SnackEntity snack = snackRepository.findByIdAndIsActive(id, true)
                .orElseThrow(() -> new DataNotFoundException("Snack not found"));
        snack.setIsActive(false);
        snackRepository.save(snack);
    }

    @Override
    public SnackResponse findById(int id) {
        SnackEntity snack = snackRepository.findByIdAndIsActive(id, true)
                .orElseThrow(() -> new DataNotFoundException("Snack not found or inactive"));
        return convert(snack);
    }

    @Override
    public List<SnackResponse> findAll() {
        return snackRepository.findByIsActive(true)
                .stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public List<SnackResponse> findAllComboSnacks() {
        return snackTypeRepository.findByNameAndIsActive("Combo", true)
                .map(snackType -> snackRepository.findBySnackTypeAndIsActive(snackType, true).stream()
                        .map(this::convert)
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public List<SnackResponse> findAllSnacksByType(Integer snackTypeId) {
        SnackTypeEntity snackType = snackTypeRepository.findByIdAndIsActive(snackTypeId, true)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy snack type"));
        return snackRepository.findBySnackTypeAndIsActive(snackType, true).stream()
                .map(this::convert)
                .toList();

    }

    private SnackResponse convert(SnackEntity snack) {
        SnackResponse snackResponse = mapper.map(snack, SnackResponse.class);
        snackResponse.setSnackTypes(snack.getSnackType());
        return snackResponse;
    }
}





