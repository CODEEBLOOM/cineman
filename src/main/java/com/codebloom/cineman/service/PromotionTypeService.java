package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.PromotionTypeRequest;
import com.codebloom.cineman.controller.response.PromotionTypeResponse;
import com.codebloom.cineman.model.PromotionTypeEntity;

import java.util.List;

public interface PromotionTypeService {

    PromotionTypeResponse create(PromotionTypeRequest request);

    PromotionTypeResponse update(Long id, PromotionTypeRequest request);

    PromotionTypeResponse findById(Long id);

    List<PromotionTypeResponse> findAll();

    void delete(Long id);

    PromotionTypeEntity getActiveEntity(Long id);
}
