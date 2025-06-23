package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.SnackTypeRequest;
import com.codebloom.cineman.controller.response.SnackTypeResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.SnackTypeEntity;
import com.codebloom.cineman.repository.SnackTypeRepository;
import com.codebloom.cineman.service.SnackTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SnackTypeServiceImpl implements SnackTypeService {
    private final SnackTypeRepository snackTypeRepository;
    private final ModelMapper modelMapper;

    @Override
    public void delete(Integer id) {
        SnackTypeEntity entity = snackTypeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Snack Type not found"));
        entity.setIsActive(false);
        snackTypeRepository.save(entity);
    }

    @Override
    public List<SnackTypeResponse> findAll() {
        List<SnackTypeEntity> entities = snackTypeRepository.findAll()
                .stream()
                .filter(SnackTypeEntity::getIsActive)
                .toList();

        return entities.stream()
                .map(entity -> modelMapper.map(entity, SnackTypeResponse.class))
                .toList();
    }

    @Override
    public SnackTypeResponse findById(Integer id) {
        SnackTypeEntity entity = snackTypeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Snack Type not found"));
        return modelMapper.map(entity, SnackTypeResponse.class);
    }

    @Override
    public SnackTypeResponse save(SnackTypeRequest snackTypeDto) {
        SnackTypeEntity entity = modelMapper.map(snackTypeDto, SnackTypeEntity.class);
        SnackTypeEntity saved = snackTypeRepository.save(entity);
        return modelMapper.map(saved, SnackTypeResponse.class);
    }

    @Override
    public SnackTypeResponse update(Integer id, SnackTypeRequest request) {
        SnackTypeEntity entity = snackTypeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Snack Type not found"));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        SnackTypeEntity updated = snackTypeRepository.save(entity);
        return modelMapper.map(updated, SnackTypeResponse.class);
    }
}
