package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.common.utils.XStr;
import com.codebloom.cineman.controller.request.PromotionRequest;
import com.codebloom.cineman.controller.response.ApplyPromotionResponse;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataNotFoundException;
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

@Service
@Slf4j(topic = "PROMOTION_SERVICE")
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final XStr xStr;
    private final InvoiceRepository invoiceRepository;

    /**
     * Tạo một mới một giảm giá
     * @param request thống tin giảm giá
     * @return PromotionResponse
     */
    @Override
    @Transactional
    public PromotionResponse create(PromotionRequest request) {

        UserEntity staff = userRepository.findByUserIdAndStatus(request.getStaffId(), UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên có id: " + request.getStaffId()));

        if(request.getStartDate().isAfter(request.getEndDate())) {
            throw new ConflictException("Ngày bắt đầu chương trình giảm giá phải trước ngày kết thúc !");
        }

        String code = xStr.getKey();
        PromotionEntity promotionEntity = PromotionEntity.builder()
                .name(request.getName())
                .content(request.getContent())
                .code(code)
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
        PromotionEntity promotionEntity = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giảm giá có id: " + id));
        if(promotionEntity.getStatus() == StatusPromotion.ACTIVE) {
            throw new ConflictException("Không thể cập nhật thông tin của giảm giá đã hoạt động !");
        }
        if(promotionEntity.getStatus() == StatusPromotion.DELETED) {
            throw new ConflictException("Không thể cập nhật thông tin của giảm giá đã được xóa !");
        }

        promotionEntity.setName(request.getName());
        promotionEntity.setContent(request.getContent());
        promotionEntity.setStartDay(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now());
        promotionEntity.setEndDay(request.getEndDate() != null ? request.getEndDate() : LocalDateTime.now());
        promotionEntity.setDiscount(request.getDiscount());
        promotionEntity.setQuantity(request.getQuantity());
        promotionEntity.setLimitAmount(request.getLimitAmount());
        return toPromotionResponse(promotionRepository.save(promotionEntity));
    }

    /**
     * Lay thong tin mot giam gia
     * @param id id cua giam gia
     * @return PromotionResponse
     */
    @Override
    public PromotionResponse findById(Long id) {
        PromotionEntity promotionEntity = promotionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy giảm giá có id: " + id));
        if(promotionEntity.getStatus() == StatusPromotion.DELETED) {
            throw new DataNotFoundException("Không tìm thấy giảm giá có id: " + id);
        }
        return toPromotionResponse(promotionEntity);
    }

    @Override
    public List<PromotionResponse> findAll(StatusPromotion... statusPromotion) {
        StatusPromotion status = statusPromotion != null && statusPromotion.length > 0 ? statusPromotion[0] : null;
        if (status != null) {
            return promotionRepository.findAllByStatus(status).stream()
                    .map(this::toPromotionResponse)
                    .toList();
        }else {
            return promotionRepository.findAll().stream()
                    .map(this::toPromotionResponse)
                    .toList();
        }
    }

    /**
     * Xoa mot giam gia
     * @param id id cua giam gia
     */
    @Override
    public void delete(Long id) {
        PromotionEntity promotionEntity = promotionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy giảm giá có id: " + id));
        promotionEntity.setStatus(StatusPromotion.DELETED);
        promotionRepository.save(promotionEntity);
    }


    /**
     * Chuyển trạng thái giảm giá thành hoạt động
     * @param id id cua giam gia
     * @return PromotionResponses
     */
    @Override
    public PromotionResponse activePromotion(Long id) {
        PromotionEntity promotionEntity = promotionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy giảm giá có id: " + id));
        if(promotionEntity.getStatus() == StatusPromotion.DELETED) {
            throw new DataNotFoundException("Không tìm thấy giảm giá có id: " + id);
        }

        if(promotionEntity.getEndDay().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Giảm giá đã kết thúc từ ngày "+ promotionEntity.getEndDay());
        }

        promotionEntity.setStatus(StatusPromotion.ACTIVE);
        return toPromotionResponse(promotionRepository.save(promotionEntity));
    }

    /**
     * Kiểm tra một giảm giá của người dùng
     * @param code code giảm giá
     * @param amount tổng tiền hiện tại
     * @return ApplyPromotionResponse
     */
    @Override
    public ApplyPromotionResponse applyPromotion(String code, Double amount) {
        PromotionEntity promotionEntity = promotionRepository.findByCodeAndStatus(code, StatusPromotion.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy giảm giá với code: " + code));

        // Check ngày //
        LocalDateTime now = LocalDateTime.now();
        if(promotionEntity.getStartDay().isAfter(now)) {
            throw new DataNotFoundException("Giảm giá bắt đầu từ ngày: " + promotionEntity.getStartDay() );
        }

        if(promotionEntity.getEndDay().isBefore(now)){
            throw new DataNotFoundException("Giảm giá đã kết thúc !");
        }

        // Check số lượng //
        if(promotionEntity.getQuantity() <= 0) {
            throw new DataNotFoundException("Giảm giá đã được sử dụng hết !");
        }

        if(promotionEntity.getLimitAmount() > amount) {
            throw new DataNotFoundException("Tổng tiền tối thiểu là: " + promotionEntity.getLimitAmount() + " VND !");
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
        PromotionEntity promotionEntity = promotionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy giảm giá có id: " + id));
        this.applyPromotion(promotionEntity.getCode(), 0.0);
        return null;
    }

    /**
     * Hàm hủy sử dụng giảm giá
     * @param id id cua giam gia
     */
    @Override
    public void cancelPromotion(Long id) {
        PromotionEntity promotionEntity = promotionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy giảm giá có id: " + id));
        promotionEntity.setQuantity(promotionEntity.getQuantity() + 1);
        promotionRepository.save(promotionEntity);
    }


    /**
     * Chuyen PromotionEntity thanh PromotionResponse
     * @param promotionEntity Thông tin giảm giá
     * @return PromotionResponse
     */
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
}
