package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.request.UserPointHistoryRequest;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.model.MembershipRankEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.MembershipRankRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.UserPointHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipRankServiceImplTest {

    @Mock
    private MembershipRankRepository membershipRankRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPointHistoryService userPointHistoryService;

    @InjectMocks
    private MembershipRankServiceImpl membershipRankService;

    @Test
    void upgradeMembershipRankShouldUsePositivePointTransactionAndAssignTargetRank() {
        MembershipRankEntity currentRank = MembershipRankEntity.builder()
                .id(1)
                .name("Normal")
                .priorityLevel(1)
                .build();
        MembershipRankEntity targetRank = MembershipRankEntity.builder()
                .id(2)
                .name("Silver")
                .requiredPoint(100)
                .priorityLevel(2)
                .build();
        UserEntity beforeUpgrade = UserEntity.builder()
                .userId(1L)
                .status(UserStatus.ACTIVE)
                .membershipRank(currentRank)
                .savePoint(300)
                .build();
        UserEntity afterDeduction = UserEntity.builder()
                .userId(1L)
                .status(UserStatus.ACTIVE)
                .membershipRank(currentRank)
                .savePoint(200)
                .build();

        when(membershipRankRepository.findByIdAndStatus(2, Boolean.TRUE)).thenReturn(Optional.of(targetRank));
        when(userRepository.findByUserIdAndStatus(1L, UserStatus.ACTIVE))
                .thenReturn(Optional.of(beforeUpgrade))
                .thenReturn(Optional.of(afterDeduction));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MembershipRankEntity upgradedRank = membershipRankService.upgradeMembershipRank(1L, 2);

        ArgumentCaptor<UserPointHistoryRequest> requestCaptor = ArgumentCaptor.forClass(UserPointHistoryRequest.class);
        verify(userPointHistoryService).createTransaction(requestCaptor.capture());
        assertEquals(100, requestCaptor.getValue().getChangePoint());
        assertNull(requestCaptor.getValue().getInvoiceId());
        assertSame(targetRank, upgradedRank);
        assertSame(targetRank, afterDeduction.getMembershipRank());
        verify(userRepository, times(2)).findByUserIdAndStatus(1L, UserStatus.ACTIVE);
    }

    @Test
    void upgradeMembershipRankShouldRejectSameOrLowerPriorityRank() {
        MembershipRankEntity currentRank = MembershipRankEntity.builder()
                .id(2)
                .name("Silver")
                .priorityLevel(2)
                .build();
        MembershipRankEntity targetRank = MembershipRankEntity.builder()
                .id(1)
                .name("Normal")
                .priorityLevel(1)
                .requiredPoint(50)
                .build();
        UserEntity user = UserEntity.builder()
                .userId(1L)
                .status(UserStatus.ACTIVE)
                .membershipRank(currentRank)
                .build();

        when(membershipRankRepository.findByIdAndStatus(1, Boolean.TRUE)).thenReturn(Optional.of(targetRank));
        when(userRepository.findByUserIdAndStatus(1L, UserStatus.ACTIVE)).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> membershipRankService.upgradeMembershipRank(1L, 1));
    }
}
