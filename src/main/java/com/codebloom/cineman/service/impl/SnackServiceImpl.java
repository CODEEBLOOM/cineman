package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.SnackRequest;
import com.codebloom.cineman.controller.response.SnackResponse;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.SnackEntity;
import com.codebloom.cineman.model.SnackTypeEntity;
import com.codebloom.cineman.repository.SnackRepository;
import com.codebloom.cineman.repository.SnackTypeRepository;
import com.codebloom.cineman.service.SnackService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Service
@RequiredArgsConstructor
public class SnackServiceImpl implements SnackService {

    private final SnackRepository snackRepository;
    private final SnackTypeRepository snackTypeRepository;
    private final ModelMapper mapper;

    @Override
    public SnackResponse create(SnackRequest request) {
        SnackEntity snack = new SnackEntity();
        snack.setSnackName(request.getSnackName());
        snack.setUnitPrice(request.getUnitPrice());
        snack.setImage(request.getImage());
        snack.setDescription(request.getDescription());
        SnackTypeEntity snackType = snackTypeRepository.findById(request.getSnackTypeId())
                .orElseThrow(() -> new DataNotFoundException("Snack Type not found"));
        snack.setSnackType(snackType);
        snack.setIsActive(true);
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
        return snackRepository.findAllByIsActiveTrue()
                .stream()
                .map(s -> mapper.map(s, SnackResponse.class))
                .toList();
    }
}





