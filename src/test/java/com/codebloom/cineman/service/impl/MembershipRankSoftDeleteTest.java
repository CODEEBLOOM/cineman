package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.MembershipRankEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.MembershipRankRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.UserPointHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipRankSoftDeleteTest {

    @Mock
    private MembershipRankRepository membershipRankRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPointHistoryService userPointHistoryService;

    @InjectMocks
    private MembershipRankServiceImpl membershipRankService;

    @Test
    void deleteShouldSoftDeleteRankWhenNoUserIsUsingIt() {
        MembershipRankEntity rank = MembershipRankEntity.builder()
                .id(1)
                .name("Silver")
                .status(Boolean.TRUE)
                .users(Collections.emptyList())
                .build();

        when(membershipRankRepository.findByIdAndStatus(1, Boolean.TRUE)).thenReturn(Optional.of(rank));

        membershipRankService.delete(1);

        assertEquals(Boolean.FALSE, rank.getStatus());
        verify(membershipRankRepository).save(rank);
    }

    @Test
    void deleteShouldRejectWhenUsersAreUsingRank() {
        MembershipRankEntity rank = MembershipRankEntity.builder()
                .id(1)
                .name("Silver")
                .status(Boolean.TRUE)
                .users(List.of(UserEntity.builder().userId(2L).build()))
                .build();

        when(membershipRankRepository.findByIdAndStatus(1, Boolean.TRUE)).thenReturn(Optional.of(rank));

        assertThrows(ConflictException.class, () -> membershipRankService.delete(1));
        verify(membershipRankRepository, never()).save(rank);
    }

    @Test
    void findByIdShouldIgnoreSoftDeletedRank() {
        when(membershipRankRepository.findByIdAndStatus(1, Boolean.TRUE)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> membershipRankService.findById(1));
    }
}
