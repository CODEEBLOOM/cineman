package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.common.utils.XStr;
import com.codebloom.cineman.controller.request.PromotionRequest;
import com.codebloom.cineman.controller.response.ApplyPromotionResponse;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.PromotionEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.PromotionRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j(topic = "PROMOTION_SERVICE")
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final XStr xStr;

    @Override
    @Transactional
    public PromotionResponse create(PromotionRequest request) {
        UserEntity staff = userRepository.findByUserIdAndStatus(request.getStaffId(), UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay nhan vien co id: " + request.getStaffId()));

        validatePromotionDateRange(request);

        PromotionEntity promotionEntity = PromotionEntity.builder()
                .name(request.getName())
                .content(request.getContent())
                .code(xStr.getKey())
                .startDay(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now())
                .endDay(request.getEndDate() != null ? request.getEndDate() : LocalDateTime.now())
                .discount(request.getDiscount())
                .quantity(request.getQuantity())
                .limitAmount(request.getLimitAmount())
                .staff(staff)
                .status(StatusPromotion.INACTIVE)
                .build();
        return toPromotionResponse(promotionRepository.save(promotionEntity));
    }

    @Override
    @Transactional
    public PromotionResponse update(Long id, PromotionRequest request) {
        PromotionEntity promotionEntity = findPromotionById(id);
        if (promotionEntity.getStatus() == StatusPromotion.ACTIVE) {
            throw new ConflictException("Khong the cap nhat thong tin cua giam gia da hoat dong");
        }

        validatePromotionDateRange(request);

        promotionEntity.setName(request.getName());
        promotionEntity.setContent(request.getContent());
        promotionEntity.setStartDay(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now());
        promotionEntity.setEndDay(request.getEndDate() != null ? request.getEndDate() : LocalDateTime.now());
        promotionEntity.setDiscount(request.getDiscount());
        promotionEntity.setQuantity(request.getQuantity());
        promotionEntity.setLimitAmount(request.getLimitAmount());
        return toPromotionResponse(promotionRepository.save(promotionEntity));
    }

    @Override
    public PromotionResponse findById(Long id) {
        return toPromotionResponse(findPromotionById(id));
    }

    @Override
    public List<PromotionResponse> findAll(StatusPromotion... statusPromotion) {
        StatusPromotion status = statusPromotion != null && statusPromotion.length > 0 ? statusPromotion[0] : null;
        if (status != null) {
            return promotionRepository.findAllByStatus(status).stream()
                    .map(this::toPromotionResponse)
                    .toList();
        }

        return promotionRepository.findAllByStatusNot(StatusPromotion.DELETED).stream()
                .map(this::toPromotionResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PromotionEntity promotionEntity = findPromotionById(id);
        if (invoiceRepository.existsByPromotionAndStatusIn(
                promotionEntity,
                List.of(InvoiceStatus.PENDING, InvoiceStatus.PROCESSING)
        )) {
            throw new ConflictException("Khuyen mai dang duoc gan voi hoa don dang xu ly, khong the xoa");
        }

        promotionEntity.setStatus(StatusPromotion.DELETED);
        promotionRepository.save(promotionEntity);
    }

    @Override
    public PromotionResponse activePromotion(Long id) {
        PromotionEntity promotionEntity = findPromotionById(id);
        if (promotionEntity.getEndDay().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Giam gia da ket thuc tu ngay " + promotionEntity.getEndDay());
        }

        promotionEntity.setStatus(StatusPromotion.ACTIVE);
        return toPromotionResponse(promotionRepository.save(promotionEntity));
    }

    @Override
    public ApplyPromotionResponse applyPromotion(String code, Double amount) {
        log.info("Apply promotion code: {} amount: {}", code, amount);
        long userId = 40;
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay nguoi dung"));

        PromotionEntity promotionEntity = promotionRepository.findByCodeAndStatus(code, StatusPromotion.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay giam gia voi code: " + code));
        log.info("find promotion: {}", promotionEntity);

        LocalDateTime now = LocalDateTime.now();
        if (promotionEntity.getStartDay().isAfter(now)) {
            throw new DataNotFoundException("Giam gia bat dau tu ngay: " + promotionEntity.getStartDay());
        }
        if (promotionEntity.getEndDay().isBefore(now)) {
            throw new DataNotFoundException("Giam gia da ket thuc");
        }
        if (promotionEntity.getQuantity() <= 0) {
            throw new DataNotFoundException("Giam gia da duoc su dung het");
        }
        if (promotionEntity.getLimitAmount() > amount) {
            throw new DataNotFoundException("Tong tien toi thieu la: " + promotionEntity.getLimitAmount() + " VND");
        }

        Double discount = promotionEntity.getDiscount() * amount;
        return ApplyPromotionResponse.builder()
                .id(promotionEntity.getId())
                .code(promotionEntity.getCode())
                .discount(discount)
                .build();
    }

    @Override
    public PromotionEntity validateToApplyPromotion(Long id) {
        PromotionEntity promotionEntity = promotionRepository.findByIdAndStatus(id, StatusPromotion.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay giam gia co id: " + id));

        LocalDateTime now = LocalDateTime.now();
        if (promotionEntity.getStartDay().isAfter(now)) {
            throw new ConflictException("Khuyen mai chua bat dau");
        }
        if (promotionEntity.getEndDay().isBefore(now)) {
            throw new ConflictException("Khuyen mai da het han");
        }
        if (promotionEntity.getQuantity() <= 0) {
            throw new ConflictException("Khuyen mai da het luot su dung");
        }

        return promotionEntity;
    }

    @Override
    public void cancelPromotion(Long id) {
        PromotionEntity promotionEntity = promotionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay giam gia co id: " + id));
        promotionEntity.setQuantity(promotionEntity.getQuantity() + 1);
        promotionRepository.save(promotionEntity);
    }

    @Override
    public Integer returnQuantityPromotion(String vnp_TxnRef) {
        InvoiceEntity invoiceEntity = invoiceRepository.findByVnTxnRef(vnp_TxnRef)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay hoa don voi vnp_TxnRef: " + vnp_TxnRef));

        if (invoiceEntity.getStatus() == InvoiceStatus.PAID || invoiceEntity.getStatus() == InvoiceStatus.CANCELLED) {
            throw new ConflictException("Hoa don da huy hoac da thanh toan");
        }

        PromotionEntity promotionEntity = promotionRepository.findById(invoiceEntity.getPromotion().getId())
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay giam gia co id: " + invoiceEntity.getPromotion().getId()));
        promotionEntity.setQuantity(promotionEntity.getQuantity() + 1);
        promotionRepository.save(promotionEntity);
        return promotionEntity.getQuantity();
    }

    @Override
    public List<PromotionResponse> findAllPromotionByUserId(Long userId, StatusPromotion status) {
        List<PromotionResponse> usedPromotions = promotionRepository.findAllPromotionByCustomerUsed(StatusPromotion.ACTIVE, userId)
                .stream()
                .map(promotionEntity -> PromotionResponse.builder()
                        .id(promotionEntity.getId())
                        .name(promotionEntity.getName())
                        .content(promotionEntity.getContent())
                        .code(promotionEntity.getCode())
                        .startDate(promotionEntity.getStartDay().toString())
                        .endDate(promotionEntity.getEndDay().toString())
                        .discount(promotionEntity.getDiscount())
                        .quantity(promotionEntity.getQuantity())
                        .limitAmount(promotionEntity.getLimitAmount())
                        .status(StatusPromotion.USED)
                        .build())
                .toList();

        List<PromotionResponse> availablePromotions = promotionRepository.findAllPromotionByCustomerNotUse(StatusPromotion.ACTIVE, userId)
                .stream()
                .map(promotionEntity -> PromotionResponse.builder()
                        .id(promotionEntity.getId())
                        .name(promotionEntity.getName())
                        .content(promotionEntity.getContent())
                        .code(promotionEntity.getCode())
                        .startDate(promotionEntity.getStartDay().toString())
                        .endDate(promotionEntity.getEndDay().toString())
                        .discount(promotionEntity.getDiscount())
                        .quantity(promotionEntity.getQuantity())
                        .limitAmount(promotionEntity.getLimitAmount())
                        .status(StatusPromotion.ACTIVE)
                        .build())
                .toList();

        if (status == null) {
            return Stream.concat(usedPromotions.stream(), availablePromotions.stream())
                    .collect(Collectors.toList());
        }

        if (status == StatusPromotion.USED) {
            return usedPromotions;
        }

        if (status == StatusPromotion.ACTIVE) {
            return availablePromotions;
        }

        return List.of();
    }

    private PromotionResponse toPromotionResponse(PromotionEntity promotionEntity) {
        return PromotionResponse.builder()
                .id(promotionEntity.getId())
                .name(promotionEntity.getName())
                .content(promotionEntity.getContent())
                .code(promotionEntity.getCode())
                .startDate(promotionEntity.getStartDay().toString())
                .endDate(promotionEntity.getEndDay().toString())
                .discount(promotionEntity.getDiscount())
                .quantity(promotionEntity.getQuantity())
                .limitAmount(promotionEntity.getLimitAmount())
                .status(promotionEntity.getStatus())
                .build();
    }

    private void validatePromotionDateRange(PromotionRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ConflictException("Ngay bat dau chuong trinh giam gia phai truoc ngay ket thuc");
        }
    }

    private PromotionEntity findPromotionById(Long id) {
        return promotionRepository.findByIdAndStatusNot(id, StatusPromotion.DELETED)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay giam gia co id: " + id));
    }
}
