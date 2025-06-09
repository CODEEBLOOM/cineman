package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.SnackCreationRequest;
import com.codebloom.cineman.controller.request.SnackUpdateRequest;
import com.codebloom.cineman.controller.response.SnackResponse;
import com.codebloom.cineman.model.DetailBookingSnackEntity;
import com.codebloom.cineman.model.SnackEntity;

import com.codebloom.cineman.repository.DetailSnackBookingRepository;
import com.codebloom.cineman.repository.SnackRepository;
import com.codebloom.cineman.service.SnackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j(topic = "SNACK-SERVICE")
@RequiredArgsConstructor
public class SnackServiceImpl implements SnackService {
    
    private final SnackRepository snackRepository;


    @Override
    public List<SnackEntity> findAll() {
        return List.of();
    }

    @Override
    public SnackResponse findByName(String name) {
        return null;
    }
// tạo mới snack
    @Override
    public Integer save(SnackCreationRequest request) {
        SnackEntity snack = new SnackEntity();
        snack.setSnackName(request.getName());
        snack.setSnackType(request.getSnack_type_id());
        snack.setImage(request.getImage());
        snack.setUnitPrice(request.getUnit_price());
        snack.setIsActive(true);
        snack.setDescription(request.getDescription());

        // tạo snack ko cần tạo detailbookingsnack -> có field trong enity nhưng để thể hiện mối quan hệ
        //snack.setDetailBookingSnacks();

        snackRepository.save(snack);
// trả về id vừa tạo -> restfull
        return snack.getId();
    }

    @Override
    public void update(SnackUpdateRequest snack) {

    }

    @Override
    public void delete(Long userId) {

    }
}
