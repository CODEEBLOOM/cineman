package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.UserStatus;
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

    @Override
    @Transactional
    public MembershipRankResponse create(MembershipRankRequest request) {
        log.info("Create membership rank with name: {}", request.getName());
        checkMembershipRankName(request.getName());

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

    @Override
    @Transactional
    public MembershipRankResponse update(Integer id, MembershipRankRequest request) {
        log.info("Update membership rank with id: {}", id);
        MembershipRankEntity membershipRankEntity = membershipRankRepository.findByIdAndStatus(id, Boolean.TRUE)
                .orElseThrow(() -> new DataNotFoundException("Membership rank not found"));

        membershipRankRepository.findByName(request.getName().trim())
                .filter(existingRank -> !existingRank.getId().equals(id))
                .ifPresent(existingRank -> {
                    throw new DataExistingException("Membership rank name is existed");
                });

        membershipRankEntity.setName(request.getName().trim());
        membershipRankEntity.setRequiredPoint(request.getRequiredPoint());
        membershipRankEntity.setReturnPointsTicket(request.getReturnPointsTicket());
        membershipRankEntity.setReturnPointsSnack(request.getReturnPointsSnack());
        membershipRankEntity.setPriorityLevel(request.getPriorityLevel());
        membershipRankRepository.save(membershipRankEntity);
        return convertToMembershipRankResponse(membershipRankEntity);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        MembershipRankEntity membershipRankEntity = membershipRankRepository.findByIdAndStatus(id, Boolean.TRUE)
                .orElseThrow(() -> new DataNotFoundException("Membership rank not found with id: " + id));
        if (!membershipRankEntity.getUsers().isEmpty()) {
            throw new ConflictException("Cannot delete membership rank with id: " + id + " because it has users");
        }
        membershipRankEntity.setStatus(Boolean.FALSE);
        membershipRankRepository.save(membershipRankEntity);
    }

    @Override
    public MembershipRankResponse findById(Integer id) {
        MembershipRankEntity membershipRankEntity = membershipRankRepository.findByIdAndStatus(id, Boolean.TRUE)
                .orElseThrow(() -> new DataNotFoundException("Membership rank not found with id: " + id));
        return convertToMembershipRankResponse(membershipRankEntity);
    }

    @Override
    public List<MembershipRankResponse> findAll() {
        List<MembershipRankResponse> membershipRankResponses = membershipRankRepository.findAllByStatusOrderByPriorityLevelAsc(Boolean.TRUE)
                .stream()
                .map(this::convertToMembershipRankResponse)
                .toList();
        return membershipRankResponses.isEmpty() ? null : membershipRankResponses;
    }

    @Override
    @Transactional
    public MembershipRankEntity upgradeMembershipRank(Long userId, Integer membershipRankId) {
        MembershipRankEntity targetRank = membershipRankRepository.findByIdAndStatus(membershipRankId, Boolean.TRUE)
                .orElseThrow(() -> new DataNotFoundException("Membership rank not found with id: " + membershipRankId));

        UserEntity userEntity = userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + userId));

        MembershipRankEntity currentRank = userEntity.getMembershipRank();
        if (currentRank != null) {
            if (currentRank.getId().equals(targetRank.getId())) {
                throw new ConflictException("Nguoi dung dang o hang thanh vien nay!");
            }
            if (targetRank.getPriorityLevel() <= currentRank.getPriorityLevel()) {
                throw new ConflictException("Chi co the nang len hang thanh vien cao hon!");
            }
        }

        String currentRankName = currentRank == null ? "Chua co hang" : currentRank.getName();
        userPointHistoryService.createTransaction(
                UserPointHistoryRequest.builder()
                        .userId(userId)
                        .changePoint(targetRank.getRequiredPoint())
                        .reason("Nang cap the thanh vien: " + currentRankName + " len " + targetRank.getName())
                        .invoiceId(null)
                        .build()
        );

        UserEntity upgradedUser = userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + userId));
        upgradedUser.setMembershipRank(targetRank);
        userRepository.save(upgradedUser);

        return upgradedUser.getMembershipRank();
    }

    private void checkMembershipRankName(String name) {
        membershipRankRepository.findByName(name).ifPresent(membershipRank -> {
            throw new DataExistingException("Membership rank name is existed");
        });
    }

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
