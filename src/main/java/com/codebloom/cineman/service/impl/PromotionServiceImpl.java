package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.PromotionConditionType;
import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.controller.request.PromotionRequest;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.Exception.*;
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
import java.util.*;
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

        if (request.getStartDay() == null) {
            throw new DataNotFoundException("Ngày bắt đầu không được để trống");
        } else {
            // Gộp giờ hiện tại vào ngày bắt đầu
            Calendar inputCal = Calendar.getInstance();
            inputCal.setTime(request.getStartDay());

            Calendar now = Calendar.getInstance();
            inputCal.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
            inputCal.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
            inputCal.set(Calendar.SECOND, now.get(Calendar.SECOND));
            inputCal.set(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND));

            Date fullStartDay = inputCal.getTime();
            if (fullStartDay.before(new Date())) {
                throw new DataNotFoundException("Ngày bắt đầu phải là hôm nay hoặc tương lai");
            }
            request.setStartDay(fullStartDay);
        }

        if ("DAY_OF_WEEK".equals(request.getConditionType())) {
            Date conditionDate = request.getConditionDate();
            if (conditionDate == null) {
                conditionDate = new Date();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(conditionDate);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            request.setConditionValue((double) dayOfWeek);
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
        promotion.setStaff(staff);
        updatePromotionStatus(promotion);

        PromotionEntity saved = promotionRepository.save(promotion);
        PromotionResponse response = mapper.map(saved, PromotionResponse.class);

        if (saved.getConditionType() == PromotionConditionType.DAY_OF_WEEK) {
            response.setConditionDayOfWeek(convertDayOfWeekToVietnamese(saved.getConditionValue().intValue()));
        }

        return response;
    }


    @Transactional
    @Override
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        PromotionEntity promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Promotion not found"));

        UserEntity staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new DataNotFoundException("Staff not found"));

        if ("DAY_OF_WEEK".equals(request.getConditionType())) {
            Date conditionDate = request.getConditionDate();
            if (conditionDate == null) {
                conditionDate = new Date();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(conditionDate);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            request.setConditionValue((double) dayOfWeek);
        }

        promotion.setName(request.getName());
        promotion.setContent(request.getContent());
        promotion.setCode(request.getCode());
        if (request.getStartDay() == null) {
            throw new DataNotFoundException("Ngày bắt đầu không được để trống");
        }
        if (request.getEndDay() == null) {
            throw new DataNotFoundException("Ngày kết thúc không được để trống");
        }
        promotion.setDiscount(request.getDiscount());
        promotion.setConditionType(PromotionConditionType.valueOf(request.getConditionType()));
        promotion.setConditionValue(request.getConditionValue());
        promotion.setStaff(staff);
        updatePromotionStatus(promotion);

        PromotionEntity updated = promotionRepository.save(promotion);
        PromotionResponse response = mapper.map(updated, PromotionResponse.class);

        if (updated.getConditionType() == PromotionConditionType.DAY_OF_WEEK) {
            response.setConditionDayOfWeek(convertDayOfWeekToVietnamese(updated.getConditionValue().intValue()));
        }

        return response;
    }

    private String convertDayOfWeekToVietnamese(int dayOfWeek) {
        return switch (dayOfWeek) {
            case Calendar.SUNDAY -> "Chủ nhật";
            case Calendar.MONDAY -> "Thứ 2";
            case Calendar.TUESDAY -> "Thứ 3";
            case Calendar.WEDNESDAY -> "Thứ 4";
            case Calendar.THURSDAY -> "Thứ 5";
            case Calendar.FRIDAY -> "Thứ 6";
            case Calendar.SATURDAY -> "Thứ 7";
            default -> "Không xác định";
        };
    }

    private void updatePromotionStatus(PromotionEntity promo) {
        if (promo.getStatus() == StatusPromotion.DELETED) {
            return;
        }

        Date now = new Date();
        if (now.before(promo.getStartDay())) {
            promo.setStatus(StatusPromotion.UPCOMING);
        } else if (now.after(promo.getEndDay())) {
            promo.setStatus(StatusPromotion.EXPIRED);
        } else {
            promo.setStatus(StatusPromotion.ACTIVE);
        }
    }


    @Override
    public void deletePromotion(Long id) {
        PromotionEntity promo = promotionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Promotion not found"));

        // Nếu đã áp dụng cho hóa đơn rồi thì không cho xóa
        boolean isApplied = invoiceRepository.existsByPromotion(promo);
        if (isApplied) {
            throw new DataNotFoundException("Voucher đã được áp dụng cho hóa đơn, không thể xóa");
        }

        // Xóa mềm
        promo.setStatus(StatusPromotion.DELETED);
        promotionRepository.save(promo);
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

        updatePromotionStatus(promo);

        if (promo.getStatus() != StatusPromotion.ACTIVE) {
            throw new DataNotFoundException("Voucher hết hạn hoặc chưa hiệu lực");
        }

        validatePromotionCondition(promo, invoice);

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
                    throw new DataNotFoundException("Voucher chỉ áp dụng cho " + convertDayOfWeekToVietnamese((int) conditionValue));
                }
            }
        }
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() != StatusPromotion.DELETED) // Nếu muốn ẩn voucher đã xoá
                .peek(this::updatePromotionStatus)
                .map(p -> {
                    PromotionResponse response = mapper.map(p, PromotionResponse.class);
                    if (p.getConditionType() == PromotionConditionType.DAY_OF_WEEK
                            && p.getConditionValue() != null) {
                        response.setConditionDayOfWeek(
                                convertDayOfWeekToVietnamese(p.getConditionValue().intValue())
                        );
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }


    @Override
    public PromotionResponse getPromotionById(Long id) {
        PromotionEntity promo = promotionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Promotion not found"));
        updatePromotionStatus(promo);
        PromotionResponse response = mapper.map(promo, PromotionResponse.class);

        if (promo.getConditionType() == PromotionConditionType.DAY_OF_WEEK
                && promo.getConditionValue() != null) {
            response.setConditionDayOfWeek(
                    convertDayOfWeekToVietnamese(promo.getConditionValue().intValue())
            );
        }

        return response;
    }


    @Override
    public List<PromotionResponse> getAvailablePromotions() {
        Date now = new Date();
        List<PromotionEntity> allPromotions = promotionRepository.findAll();

        for (PromotionEntity promo : allPromotions) {
            updatePromotionStatus(promo);
        }

        return promotionRepository.findByStatusAndStartDayBeforeAndEndDayAfter(StatusPromotion.ACTIVE, now, now)
                .stream()
                .map(p -> {
                    PromotionResponse response = mapper.map(p, PromotionResponse.class);
                    if (p.getConditionType() == PromotionConditionType.DAY_OF_WEEK) {
                        response.setConditionDayOfWeek(convertDayOfWeekToVietnamese(p.getConditionValue().intValue()));
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse validateVoucherCode(String code) {
        PromotionEntity promo = promotionRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("Invalid voucher code"));

        updatePromotionStatus(promo);

        if (promo.getStatus() != StatusPromotion.ACTIVE) {
            throw new DataNotFoundException("Voucher is expired or inactive");
        }

        PromotionResponse response = mapper.map(promo, PromotionResponse.class);
        if (promo.getConditionType() == PromotionConditionType.DAY_OF_WEEK) {
            response.setConditionDayOfWeek(convertDayOfWeekToVietnamese(promo.getConditionValue().intValue()));
        }
        return response;
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
