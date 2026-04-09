package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.controller.request.PromotionRequest;
import com.codebloom.cineman.controller.response.ApplyPromotionResponse;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.model.PromotionEntity;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface PromotionService {

    PromotionResponse create(PromotionRequest request);

    PromotionResponse update(Long id, PromotionRequest request);

    PromotionResponse findById(Long id);

    List<PromotionResponse> findAll(StatusPromotion... statusPromotion);

    void delete(Long id);

    PromotionResponse activePromotion(Long id);

    ApplyPromotionResponse applyPromotion(String code, Double amount);

    PromotionEntity validateToApplyPromotion(Long id);

    PromotionEntity validateToApplyPromotion(Long id, Long userId);

    void cancelPromotion(@NotNull(message = "Id giam gia khong duoc phep null !") Long id);

    Integer returnQuantityPromotion(String vnp_TxnRef);

    List<PromotionResponse> findAllPromotionByUserId(Long userId, StatusPromotion status, Long promotionTypeId, Boolean expiringSoon);
}
