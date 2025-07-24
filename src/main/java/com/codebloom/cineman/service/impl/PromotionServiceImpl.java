package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.PromotionConditionType;
import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.controller.request.PromotionRequest;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.exception.*;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.PromotionEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.PromotionRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PROMOTION-SERVICE")
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
            throw new DataExistingException("Promotion code already exists");
        }
        if(request.getStartDay() == null){
            request.setStartDay(new Date());
        }
        log.info(String.valueOf(request.getStartDay()));

        // lấy ngày hôm qua để so sánh
        LocalDate yesterdayLocal = LocalDate.now().minusDays(1);
        Date yesterday = Date.from(yesterdayLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (request.getStartDay().before(yesterday)) {
            throw new InvalidDataException("The start date must be today or in the future");
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

        // kiểm tra từ ngày bắt đầu đến ngày kết thúc có thứ mà người dùng đã chọn hay không -> điều kiện ng dùng chọn DAY_OF_WEEK
        if ("DAY_OF_WEEK".equals(request.getConditionType())) {
            if (request.getConditionDay() == null) {
                throw new InvalidDataException("If you chose DAY_OF_WEEK conditionDate can't not be null");
            }

            LocalDate start = request.getStartDay().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = request.getEndDay().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int conditionDayOfWeek = request.getConditionDay();

            boolean hasMatch = false;
            while (!start.isAfter(end)) {
                if (start.getDayOfWeek().getValue() == conditionDayOfWeek) {
                    hasMatch = true;
                    break;
                }
                start = start.plusDays(1);
            }

            if (!hasMatch) {
                throw new InvalidDataException("There is no date within the selected time range that falls on the chosen");
            }else {
                promotion.setConditionValue(Double.valueOf(request.getConditionDay()));
            }

        }

        PromotionEntity saved = promotionRepository.save(promotion);
        PromotionResponse response = mapper.map(saved, PromotionResponse.class);

        if (saved.getConditionType() == PromotionConditionType.DAY_OF_WEEK) {
            response.setConditionDayOfWeek(convertDayOfWeekToVietnamese(request.getConditionDay()));
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

        // Nếu code mới khác code cũ thì kiểm tra trùng lặp
        if (!promotion.getCode().equals(request.getCode()) && request.getCode() != null) {
            if (promotionRepository.existsByCode(request.getCode())) {
                throw new DataExistingException("Promotion code already exists");
            }
            promotion.setCode(request.getCode());
        }

        if (request.getStartDay() == null) {
            request.setStartDay(new Date());
        }

        LocalDate yesterdayLocal = LocalDate.now().minusDays(1);
        Date yesterday = Date.from(yesterdayLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (request.getStartDay().before(yesterday)) {
            throw new InvalidDataException("The start date must be today or in the future");
        }

        // Gán thông tin
        promotion.setName(request.getName());
        promotion.setContent(request.getContent());
        promotion.setStartDay(request.getStartDay());
        promotion.setEndDay(request.getEndDay());
        promotion.setDiscount(request.getDiscount());
        promotion.setConditionType(PromotionConditionType.valueOf(request.getConditionType()));
        promotion.setStaff(staff);

        // Kiểm tra điều kiện dạng DAY_OF_WEEK
        if ("DAY_OF_WEEK".equals(request.getConditionType())) {
            if (request.getConditionDay() == null) {
                throw new InvalidDataException("If you chose DAY_OF_WEEK, conditionDay must not be null");
            }

            LocalDate start = request.getStartDay().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = request.getEndDay().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int conditionDayOfWeek = request.getConditionDay();

            boolean hasMatch = false;
            while (!start.isAfter(end)) {
                if (start.getDayOfWeek().getValue() == conditionDayOfWeek) {
                    hasMatch = true;
                    break;
                }
                start = start.plusDays(1);
            }

            if (!hasMatch) {
                throw new InvalidDataException("No date within the selected time range matches the chosen day of week");
            } else {
                promotion.setConditionValue(Double.valueOf(request.getConditionValue()));
            }
        } else {
            promotion.setConditionValue(request.getConditionValue());
        }

        updatePromotionStatus(promotion);
        PromotionEntity saved = promotionRepository.save(promotion);

        PromotionResponse response = mapper.map(saved, PromotionResponse.class);
        if (saved.getConditionType() == PromotionConditionType.DAY_OF_WEEK) {
            response.setConditionDayOfWeek(convertDayOfWeekToVietnamese(request.getConditionDay()));
        }

        return response;
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

    // //////////////////////////// PHẦN CỦA NGƯỜI DÙNG ////////////////////////////////////////////////////////

    @Transactional
    @Override
    public void applyVoucherToInvoice(String code, Long invoiceId) {
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new DataNotFoundException("Invoice not found"));

        if (invoice.getPromotion() != null ) {
            throw new DataExistingException("This invoice has applied a voucher");
        }

        if(!invoice.getStatus().equals(InvoiceStatus.PENDING)){
            throw new InvalidDataException("Invalid invoice status");
        }

        PromotionEntity promo = promotionRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("Voucher not found"));

        updatePromotionStatus(promo);

        if (promo.getStatus() != StatusPromotion.ACTIVE) {
            throw new DataNotFoundException("Voucher has expired or is not coming up");
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
                int conditionDay = promo.getConditionValue().intValue();
                int today = LocalDate.now().getDayOfWeek().getValue();
                if (today != conditionDay) {
                    throw new DataNotFoundException("Voucher is only valid on specific days: " + convertDayOfWeekToVietnamese(conditionDay));
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

    // tạo mã code promotion random nếu người dùng ko nhâp
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

    // chuyển từ dữ liệu front end gửi về từ int thành string thứ
    public String convertDayOfWeekToVietnamese(int day) {
        return switch (day) {
            case 1 -> "Thứ hai";
            case 2 -> "Thứ ba";
            case 3 -> "Thứ tư";
            case 4 -> "Thứ năm";
            case 5 -> "Thứ sáu";
            case 6 -> "Thứ bảy";
            case 7 -> "Chủ nhật";
            default -> "Không xác định";
        };
    }

    // cập nhật lại trạng thái giảm giá theo thời hạn
    private void updatePromotionStatus(PromotionEntity promo) {
        if (promo.getStatus() == StatusPromotion.DELETED) {
            return;
        }

        // lấy ngày hôm qua để so sánh
        LocalDate yesterdayLocal = LocalDate.now().minusDays(1);
        Date yesterday = Date.from(yesterdayLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Date now = new Date();
        if (yesterday.after(promo.getStartDay())) {
            promo.setStatus(StatusPromotion.UPCOMING);
        } else if (now.after(promo.getEndDay())) {
            promo.setStatus(StatusPromotion.EXPIRED);
        } else {
            promo.setStatus(StatusPromotion.ACTIVE);
        }
    }



}
