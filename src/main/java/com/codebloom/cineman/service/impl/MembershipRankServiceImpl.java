package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MembershipRankRequest;
import com.codebloom.cineman.controller.request.UserPointHistoryRequest;
import com.codebloom.cineman.controller.response.MembershipRankResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.MembershipRankEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.MembershipRankRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.MembershipRankService;
import com.codebloom.cineman.service.UserPointHistoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j(topic = "MEMBERSHIP_RANK_SERVICE")
@RequiredArgsConstructor
public class MembershipRankServiceImpl implements MembershipRankService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final MembershipRankRepository membershipRankRepository;
    private final UserRepository userRepository;
    private final UserPointHistoryService userPointHistoryService;

    /**
     * Create membership rank
     *
     * @param request MembershipRankRequest
     * @return MembershipRankResponse
     */
    @Override
    @Transactional
    public MembershipRankResponse create(MembershipRankRequest request) {
        log.info("Create membership rank with name: {}", request.getName());
        this.checkMembershipRankName(request.getName());
        MembershipRankEntity membershipRankEntity = MembershipRankEntity.builder()
                .name(request.getName().trim())
                .requiredPoint(request.getRequiredPoint())
                .returnPointsTicket(request.getReturnPointsTicket())
                .returnPointsSnack(request.getReturnPointsSnack())
                .priorityLevel(request.getPriorityLevel())
                .status(Boolean.TRUE)
                .build();
        membershipRankRepository.save(membershipRankEntity);

        log.info("Create membership rank success with id: {}, name: {}", membershipRankEntity.getId(), membershipRankEntity.getName());
        return convertToMembershipRankResponse(membershipRankEntity);
    }

    /**
     * Update membership rank
     * @param id id of membership rank
     * @param request MembershipRankRequest
     * @return MembershipRankResponse
     */
    @Override
    @Transactional
    public MembershipRankResponse update(Integer id, MembershipRankRequest request) {
        log.info("Update membership rank with id: {}", id);
        MembershipRankEntity membershipRankEntity = membershipRankRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Membership rank not found"));
        if (request.getName().equals(membershipRankEntity.getName())) {
            throw new DataExistingException("Membership rank name is existed");
        }
        membershipRankEntity.setName(request.getName().trim());
        membershipRankEntity.setRequiredPoint(request.getRequiredPoint());
        membershipRankEntity.setReturnPointsTicket(request.getReturnPointsTicket());
        membershipRankEntity.setReturnPointsSnack(request.getReturnPointsSnack());
        membershipRankEntity.setPriorityLevel(request.getPriorityLevel());
        membershipRankRepository.save(membershipRankEntity);
        return convertToMembershipRankResponse(membershipRankEntity);
    }

    @Override
    public void delete(Integer id) {
        MembershipRankEntity membershipRankEntity = membershipRankRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Membership rank not found with id: " + id));
        if (!membershipRankEntity.getUsers().isEmpty()) {
            throw new ConflictException("Cannot delete membership rank with id: " + id + " because it has users");
        }
        membershipRankRepository.delete(membershipRankEntity);
    }

    /**
     * Find membership rank by id
     * @param id id of membership rank
     * @return MembershipRankResponse
     */
    @Override
    public MembershipRankResponse findById(Integer id) {
        MembershipRankEntity membershipRankEntity = membershipRankRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Membership rank not found with id: " + id));
        return convertToMembershipRankResponse(membershipRankEntity);
    }

    /**
     * Find all membership rank
     * @return List<MembershipRankResponse>
     */
    @Override
    public List<MembershipRankResponse> findAll() {
        List<MembershipRankResponse> membershipRankResponses = membershipRankRepository.findAll()
                .stream()
                .map(this::convertToMembershipRankResponse)
                .toList();
        return membershipRankResponses.isEmpty() ? null : membershipRankResponses;
    }

    /**
     * Upgrade membership rank
     * @param userId id of user
     * @param membershipRankId id of membership rank
     * @return MembershipRankEntity
     */
    @Override
    public MembershipRankEntity upgradeMembershipRank(Long userId, Integer membershipRankId) {
        MembershipRankEntity membershipRankEntity = membershipRankRepository.findById(membershipRankId)
                .orElseThrow(() -> new DataNotFoundException("Membership rank not found with id: " + membershipRankId));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + userId));
        int newSavePoint = userEntity.getSavePoint() - membershipRankEntity.getRequiredPoint();
        if (newSavePoint < 0) {
            throw new ConflictException("Điểm tích lũy không đủ để quy đổi !");
        }
        userEntity.setMembershipRank(membershipRankEntity);
        userEntity.setSavePoint(newSavePoint);
        userRepository.save(userEntity);
        userPointHistoryService.createTransaction(
                UserPointHistoryRequest.builder()
                        .userId(userId)
                        .changePoint(membershipRankEntity.getRequiredPoint() * -1)
                        .reason("Nâng cấp thẻ thành viên: " + userEntity.getMembershipRank().getName() + " lên " + membershipRankEntity.getName())
                        .invoiceId(null)
                        .build()
        );

        return userEntity.getMembershipRank();
    }

    /**
     * Check membership rank name is existed
     * @param name Membership rank name
     */
    private void checkMembershipRankName(String name) {
        membershipRankRepository.findByName(name).ifPresent(membershipRank -> {
            throw new RuntimeException("Membership rank name is existed");
        });
    }

    /**
     * Convert MembershipRankEntity to MembershipRankResponse
     *
     * @param entity MembershipRankEntity
     * @return MembershipRankResponse
     */
    private MembershipRankResponse convertToMembershipRankResponse(MembershipRankEntity entity) {
        return MembershipRankResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .requiredPoint(entity.getRequiredPoint())
                .returnPointsTicket(entity.getReturnPointsTicket())
                .returnPointsSnack(entity.getReturnPointsSnack())
                .priorityLevel(entity.getPriorityLevel())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt() == null ? null : entity.getCreatedAt().format(DATE_TIME_FORMATTER))
                .updatedAt(entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().format(DATE_TIME_FORMATTER))
                .build();
    }
}
