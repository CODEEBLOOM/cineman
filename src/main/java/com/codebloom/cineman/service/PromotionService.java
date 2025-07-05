package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.PromotionRequest;
import com.codebloom.cineman.controller.response.PromotionResponse;

import java.util.List;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionRequest request);
    PromotionResponse updatePromotion(Long id, PromotionRequest request);
    void deletePromotion(Long id);
    List<PromotionResponse> getAllPromotions();
    PromotionResponse getPromotionById(Long id);


    // Customer

    void applyVoucherToInvoice(String voucherCode, Long invoiceId);
    List<PromotionResponse> getAvailablePromotions();
    PromotionResponse validateVoucherCode(String code);
    String generateUniqueCode();
}
