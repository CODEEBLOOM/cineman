package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.PromotionConditionType;
import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.controller.request.PromotionRequest;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.PromotionEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.PromotionRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final ModelMapper mapper;

    @Transactional
    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {
        UserEntity staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new DataNotFoundException("Staff not found"));

        if (request.getCode() == null || request.getCode().isBlank()) {
            request.setCode(generateUniqueCode());
        } else if (promotionRepository.existsByCode(request.getCode())) {
            throw new DataNotFoundException("Mã khuyến mãi đã tồn tại");
        }

        PromotionEntity promotion = new PromotionEntity();
        promotion.setName(request.getName());
        promotion.setContent(request.getContent());
        promotion.setCode(request.getCode());
        promotion.setStartDay(request.getStartDay());
        promotion.setEndDay(request.getEndDay());
        promotion.setDiscount(request.getDiscount());
        promotion.setConditionType(PromotionConditionType.valueOf(request.getConditionType()));
        promotion.setConditionValue(request.getConditionValue());
        promotion.setStatus(request.getStatus());
        promotion.setStaff(staff);

        PromotionEntity saved = promotionRepository.save(promotion);
        return mapper.map(saved, PromotionResponse.class);
    }


    @Transactional
    @Override
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        PromotionEntity promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Promotion not found"));

        UserEntity staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new DataNotFoundException("Staff not found"));

        promotion.setName(request.getName());
        promotion.setContent(request.getContent());
        promotion.setCode(request.getCode());
        promotion.setStartDay(request.getStartDay());
        promotion.setEndDay(request.getEndDay());
        promotion.setDiscount(request.getDiscount());
        promotion.setConditionType(PromotionConditionType.valueOf(request.getConditionType()));
        promotion.setConditionValue(request.getConditionValue());
        promotion.setStatus(request.getStatus());
        promotion.setStaff(staff);

        PromotionEntity updated = promotionRepository.save(promotion);
        return mapper.map(updated, PromotionResponse.class);
    }


    @Override
    public void deletePromotion(Long id) {

    }

    @Transactional
    @Override
    public void applyVoucherToInvoice(String code, Long invoiceId) {
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));

        if (invoice.getPromotion() != null) {
            throw new DataNotFoundException("Hóa đơn này đã áp dụng voucher");
        }

        PromotionEntity promo = promotionRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("Voucher not found"));

        Date now = new Date();
        if (promo.getStatus() == StatusPromotion.EXPIRED ||
                now.before(promo.getStartDay()) || now.after(promo.getEndDay())) {
            throw new DataNotFoundException("Voucher hết hạn hoặc chưa hiệu lực");
        }

        // Kiểm tra điều kiện áp dụng
        validatePromotionCondition(promo, invoice);

        // Áp dụng giảm giá
        double discountAmount = invoice.getTotalPrice() * (promo.getDiscount() / 100);
        invoice.setTotalPrice(invoice.getTotalPrice() - discountAmount);
        invoice.setPromotion(promo);
        invoiceRepository.save(invoice);
    }

    private void validatePromotionCondition(PromotionEntity promo, InvoiceEntity invoice) {
        if (promo.getConditionType() == null) return;

        double conditionValue = promo.getConditionValue() != null ? promo.getConditionValue() : 0;

        switch (promo.getConditionType()) {
            case MIN_TOTAL_PRICE -> {
                if (invoice.getTotalPrice() < conditionValue) {
                    throw new DataNotFoundException(
                            String.format("Hóa đơn phải từ %.0f VNĐ trở lên để dùng voucher", conditionValue));
                }
            }
            case MIN_TOTAL_TICKET -> {
                if (invoice.getTotalTicket() < conditionValue) {
                    throw new DataNotFoundException(
                            String.format("Cần ít nhất %.0f vé để dùng voucher", conditionValue));
                }
            }
            case DAY_OF_WEEK -> {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                int today = cal.get(Calendar.DAY_OF_WEEK); // 1: CN, 2: T2, ...
                if ((int) conditionValue != today) {
                    throw new DataNotFoundException("Voucher chỉ áp dụng cho ngày cụ thể trong tuần");
                }
            }

        }
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll()
                .stream()
                .map(p -> mapper.map(p, PromotionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse getPromotionById(Long id) {
        return mapper.map(
                promotionRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Promotion not found")),
                PromotionResponse.class
        );
    }

    @Override
    public List<PromotionResponse> getAvailablePromotions() {
        Date now = new Date();
        List<PromotionEntity> allPromotions = promotionRepository.findAll();

        for (PromotionEntity promo : allPromotions) {
            if (promo.getStatus() == StatusPromotion.ACTIVE && now.after(promo.getEndDay())) {
                promo.setStatus(StatusPromotion.EXPIRED);
                promotionRepository.save(promo);
            }
        }

        return promotionRepository.findByStatusAndStartDayBeforeAndEndDayAfter(StatusPromotion.ACTIVE, now, now)
                .stream()
                .map(p -> mapper.map(p, PromotionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse validateVoucherCode(String code) {
        PromotionEntity promo = promotionRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("Invalid voucher code"));

        Date now = new Date();
        if (!promo.getStatus().equals(StatusPromotion.ACTIVE)
                || now.before(promo.getStartDay()) || now.after(promo.getEndDay())) {
            throw new DataNotFoundException("Voucher is expired or inactive");
        }

        return mapper.map(promo, PromotionResponse.class);
    }

    @Override
    public String generateUniqueCode() {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new SecureRandom();
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(characters.charAt(random.nextInt(characters.length())));
            }
            code = sb.toString();
        } while (promotionRepository.existsByCode(code));

        return code;
    }
}
