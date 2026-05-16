package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.common.utils.XStr;
import com.codebloom.cineman.component.PromotionActivationNotifier;
import com.codebloom.cineman.controller.request.PromotionRequest;
import com.codebloom.cineman.controller.response.ApplyPromotionResponse;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.controller.response.PromotionTypeResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.MembershipRankEntity;
import com.codebloom.cineman.model.PromotionEntity;
import com.codebloom.cineman.model.PromotionTypeEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.model.UserPrincipal;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.MembershipRankRepository;
import com.codebloom.cineman.repository.PromotionRepository;
import com.codebloom.cineman.repository.PromotionTypeRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j(topic = "PROMOTION_SERVICE")
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final long EXPIRING_SOON_DAYS = 2L;

    private final PromotionRepository promotionRepository;
    private final PromotionTypeRepository promotionTypeRepository;
    private final MembershipRankRepository membershipRankRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final XStr xStr;
    private final PromotionActivationNotifier promotionActivationNotifier;

    @Override
    @Transactional
    public PromotionResponse create(PromotionRequest request) {
        UserEntity staff = findActiveUser(request.getStaffId());
        PromotionTypeEntity promotionType = findActivePromotionType(request.getPromotionTypeId());
        List<MembershipRankEntity> membershipRanks = findMembershipRanks(request.getMembershipRankIds());

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
                .promotionType(promotionType)
                .membershipRanks(new ArrayList<>(membershipRanks))
                .staff(staff)
                .status(StatusPromotion.INACTIVE)
                .build();
        return toPromotionResponse(promotionRepository.save(promotionEntity), promotionEntity.getStatus());
    }

    @Override
    @Transactional
    public PromotionResponse update(Long id, PromotionRequest request) {
        PromotionEntity promotionEntity = findPromotionById(id);
        if (promotionEntity.getStatus() == StatusPromotion.ACTIVE) {
            throw new ConflictException("Khong the cap nhat thong tin cua giam gia da hoat dong");
        }

        PromotionTypeEntity promotionType = findActivePromotionType(request.getPromotionTypeId());
        List<MembershipRankEntity> membershipRanks = findMembershipRanks(request.getMembershipRankIds());
        validatePromotionDateRange(request);

        promotionEntity.setName(request.getName());
        promotionEntity.setContent(request.getContent());
        promotionEntity.setStartDay(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now());
        promotionEntity.setEndDay(request.getEndDate() != null ? request.getEndDate() : LocalDateTime.now());
        promotionEntity.setDiscount(request.getDiscount());
        promotionEntity.setQuantity(request.getQuantity());
        promotionEntity.setLimitAmount(request.getLimitAmount());
        promotionEntity.setPromotionType(promotionType);
        promotionEntity.setMembershipRanks(new ArrayList<>(membershipRanks));
        return toPromotionResponse(promotionRepository.save(promotionEntity), promotionEntity.getStatus());
    }

    @Override
    public PromotionResponse findById(Long id) {
        return toPromotionResponse(findPromotionById(id), null);
    }

    @Override
    public List<PromotionResponse> findAll(StatusPromotion... statusPromotion) {
        StatusPromotion status = statusPromotion != null && statusPromotion.length > 0 ? statusPromotion[0] : null;
        if (status != null) {
            return promotionRepository.findAllByStatus(status).stream()
                    .map(promotionEntity -> toPromotionResponse(promotionEntity, status))
                    .toList();
        }

        return promotionRepository.findAllByStatusNot(StatusPromotion.DELETED).stream()
                .map(promotionEntity -> toPromotionResponse(promotionEntity, null))
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
        if (promotionEntity.getStatus() == StatusPromotion.ACTIVE) {
            return toPromotionResponse(promotionEntity, promotionEntity.getStatus());
        }

        promotionEntity.setStatus(StatusPromotion.ACTIVE);
        PromotionEntity savedPromotion = promotionRepository.save(promotionEntity);
        PromotionResponse promotionResponse = toPromotionResponse(savedPromotion, savedPromotion.getStatus());
        promotionActivationNotifier.notifyEligibleUsers(savedPromotion, promotionResponse);
        return promotionResponse;
    }

    @Override
    public ApplyPromotionResponse applyPromotion(String code, Double amount) {
        log.info("Apply promotion code: {} amount: {}", code, amount);
        UserEntity currentUser = getCurrentAuthenticatedUser();
        PromotionEntity promotionEntity = promotionRepository.findByCodeAndStatus(code, StatusPromotion.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay giam gia voi code: " + code));

        validatePromotionAvailability(promotionEntity, currentUser, amount, true);

        Double discount = promotionEntity.getDiscount() * amount;
        return ApplyPromotionResponse.builder()
                .id(promotionEntity.getId())
                .code(promotionEntity.getCode())
                .discount(discount)
                .build();
    }

    @Override
    public PromotionEntity validateToApplyPromotion(Long id) {
        UserEntity currentUser = getCurrentAuthenticatedUser();
        return validateToApplyPromotion(id, currentUser.getUserId());
    }

    @Override
    public PromotionEntity validateToApplyPromotion(Long id, Long userId) {
        PromotionEntity promotionEntity = promotionRepository.findByIdAndStatus(id, StatusPromotion.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay giam gia co id: " + id));

        UserEntity userEntity = userId == null ? null : findActiveUser(userId);
        validatePromotionAvailability(promotionEntity, userEntity, null, false);
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
    public List<PromotionResponse> findAllPromotionByUserId(Long userId, StatusPromotion status, Long promotionTypeId, Boolean expiringSoon) {
        UserEntity userEntity = findActiveUser(userId);

        // status=USED: tra ve lich su voucher da xai (khong loc kha dung)
        if (status == StatusPromotion.USED) {
            return promotionRepository.findAllPromotionByCustomerUsed(StatusPromotion.ACTIVE, userId)
                    .stream()
                    .filter(promotionEntity -> matchesPromotionType(promotionEntity, promotionTypeId))
                    .filter(promotionEntity -> matchesExpiringSoon(promotionEntity, expiringSoon))
                    .map(promotionEntity -> toPromotionResponse(promotionEntity, StatusPromotion.USED))
                    .toList();
        }

        // Default (null) va status=ACTIVE: chi tra voucher user thuc su dung duoc
        return promotionRepository.findAllPromotionByCustomerNotUse(StatusPromotion.ACTIVE, userId)
                .stream()
                .filter(this::isPromotionUsableNow)
                .filter(promotionEntity -> isPromotionApplicableForUser(promotionEntity, userEntity))
                .filter(promotionEntity -> matchesPromotionType(promotionEntity, promotionTypeId))
                .filter(promotionEntity -> matchesExpiringSoon(promotionEntity, expiringSoon))
                .map(promotionEntity -> toPromotionResponse(promotionEntity, StatusPromotion.ACTIVE))
                .toList();
    }

    private boolean isPromotionUsableNow(PromotionEntity promotionEntity) {
        LocalDateTime now = LocalDateTime.now();
        if (promotionEntity.getStartDay() != null && promotionEntity.getStartDay().isAfter(now)) {
            return false;
        }
        if (promotionEntity.getEndDay() != null && promotionEntity.getEndDay().isBefore(now)) {
            return false;
        }
        return promotionEntity.getQuantity() != null && promotionEntity.getQuantity() > 0;
    }

    private PromotionResponse toPromotionResponse(PromotionEntity promotionEntity, StatusPromotion responseStatus) {
        List<MembershipRankEntity> membershipRanks = promotionEntity.getMembershipRanks() == null
                ? List.of()
                : promotionEntity.getMembershipRanks();
        return PromotionResponse.builder()
                .id(promotionEntity.getId())
                .name(promotionEntity.getName())
                .content(promotionEntity.getContent())
                .code(promotionEntity.getCode())
                .startDate(promotionEntity.getStartDay() == null ? null : promotionEntity.getStartDay().format(DATE_TIME_FORMATTER))
                .endDate(promotionEntity.getEndDay() == null ? null : promotionEntity.getEndDay().format(DATE_TIME_FORMATTER))
                .discount(promotionEntity.getDiscount())
                .quantity(promotionEntity.getQuantity())
                .limitAmount(promotionEntity.getLimitAmount())
                .status(responseStatus == null ? promotionEntity.getStatus() : responseStatus)
                .promotionType(toPromotionTypeResponse(promotionEntity.getPromotionType()))
                .applicableForAllRanks(membershipRanks.isEmpty())
                .membershipRankIds(membershipRanks.stream().map(MembershipRankEntity::getId).toList())
                .membershipRankNames(membershipRanks.stream().map(MembershipRankEntity::getName).toList())
                .build();
    }

    private PromotionTypeResponse toPromotionTypeResponse(PromotionTypeEntity promotionTypeEntity) {
        if (promotionTypeEntity == null) {
            return null;
        }

        return PromotionTypeResponse.builder()
                .id(promotionTypeEntity.getId())
                .code(promotionTypeEntity.getCode())
                .name(promotionTypeEntity.getName())
                .description(promotionTypeEntity.getDescription())
                .status(promotionTypeEntity.getStatus())
                .createdAt(promotionTypeEntity.getCreatedAt() == null ? null : promotionTypeEntity.getCreatedAt().format(DATE_TIME_FORMATTER))
                .updatedAt(promotionTypeEntity.getUpdatedAt() == null ? null : promotionTypeEntity.getUpdatedAt().format(DATE_TIME_FORMATTER))
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

    private UserEntity findActiveUser(Long userId) {
        return userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay nguoi dung co id: " + userId));
    }

    private PromotionTypeEntity findActivePromotionType(Long promotionTypeId) {
        return promotionTypeRepository.findByIdAndStatus(promotionTypeId, Boolean.TRUE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay loai khuyen mai co id: " + promotionTypeId));
    }

    private List<MembershipRankEntity> findMembershipRanks(List<Integer> membershipRankIds) {
        if (membershipRankIds == null || membershipRankIds.isEmpty()) {
            return List.of();
        }

        Set<Integer> uniqueIds = new LinkedHashSet<>(membershipRankIds);
        return uniqueIds.stream()
                .map(id -> membershipRankRepository.findByIdAndStatus(id, Boolean.TRUE)
                        .orElseThrow(() -> new DataNotFoundException("Khong tim thay membership rank co id: " + id)))
                .toList();
    }

    private UserEntity getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ConflictException("Nguoi dung chua dang nhap");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return findActiveUser(userPrincipal.getUserId());
        }
        if (Objects.equals(principal, "anonymousUser")) {
            throw new ConflictException("Nguoi dung chua dang nhap");
        }
        if (principal instanceof String username && !Objects.equals(username, "anonymousUser")) {
            return userRepository.findByEmailAndStatus(username, UserStatus.ACTIVE)
                    .orElseThrow(() -> new DataNotFoundException("Khong tim thay nguoi dung dang dang nhap"));
        }

        throw new ConflictException("Khong xac dinh duoc nguoi dung dang nhap");
    }

    private void validatePromotionAvailability(PromotionEntity promotionEntity, UserEntity userEntity, Double amount, boolean checkUsage) {
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
        if (amount != null && promotionEntity.getLimitAmount() > amount) {
            throw new ConflictException("Tong tien toi thieu la: " + promotionEntity.getLimitAmount() + " VND");
        }
        if (!isPromotionApplicableForUser(promotionEntity, userEntity)) {
            throw new ConflictException("Hang thanh vien hien tai khong duoc su dung khuyen mai nay");
        }
        if (checkUsage && userEntity != null
                && invoiceRepository.findByUserIdAndPromotionId(userEntity.getUserId(), promotionEntity.getId()).isPresent()) {
            throw new ConflictException("Khuyen mai da duoc ban su dung");
        }
    }

    private boolean isPromotionApplicableForUser(PromotionEntity promotionEntity, UserEntity userEntity) {
        List<MembershipRankEntity> membershipRanks = promotionEntity.getMembershipRanks();
        if (membershipRanks == null || membershipRanks.isEmpty()) {
            return true;
        }
        if (userEntity == null || userEntity.getMembershipRank() == null) {
            return false;
        }
        Integer userRankId = userEntity.getMembershipRank().getId();
        return membershipRanks.stream()
                .map(MembershipRankEntity::getId)
                .anyMatch(rankId -> Objects.equals(rankId, userRankId));
    }

    private boolean matchesPromotionType(PromotionEntity promotionEntity, Long promotionTypeId) {
        if (promotionTypeId == null) {
            return true;
        }

        return promotionEntity.getPromotionType() != null
                && Objects.equals(promotionEntity.getPromotionType().getId(), promotionTypeId);
    }

    private boolean matchesExpiringSoon(PromotionEntity promotionEntity, Boolean expiringSoon) {
        if (expiringSoon == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDay = promotionEntity.getEndDay();
        boolean willExpireWithinTwoDays = !endDay.isBefore(now) && !endDay.isAfter(now.plusDays(EXPIRING_SOON_DAYS));
        return expiringSoon ? willExpireWithinTwoDays : endDay.isAfter(now.plusDays(EXPIRING_SOON_DAYS));
    }
}
