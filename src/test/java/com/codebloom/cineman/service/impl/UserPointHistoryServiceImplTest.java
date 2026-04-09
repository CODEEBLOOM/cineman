package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.request.UserPointHistoryRequest;
import com.codebloom.cineman.controller.response.UserPointHistoryResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.MembershipRankEntity;
import com.codebloom.cineman.model.TicketEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.model.UserPointHistoryEntity;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.MembershipRankRepository;
import com.codebloom.cineman.repository.UserPointHistoryRepository;
import com.codebloom.cineman.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPointHistoryServiceImplTest {

    @Mock
    private UserPointHistoryRepository userPointHistoryRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MembershipRankRepository membershipRankRepository;

    @InjectMocks
    private UserPointHistoryServiceImpl userPointHistoryService;

    @Test
    void createTransactionShouldAllowNullInvoiceForRankUpgrade() {
        UserEntity user = UserEntity.builder()
                .userId(1L)
                .savePoint(150)
                .status(UserStatus.ACTIVE)
                .build();
        UserPointHistoryRequest request = UserPointHistoryRequest.builder()
                .userId(1L)
                .invoiceId(null)
                .changePoint(100)
                .reason("Nang cap the thanh vien")
                .build();

        when(userRepository.findByUserIdAndStatus(1L, UserStatus.ACTIVE)).thenReturn(Optional.of(user));
        when(userPointHistoryRepository.save(any(UserPointHistoryEntity.class))).thenAnswer(invocation -> {
            UserPointHistoryEntity history = invocation.getArgument(0);
            history.setId(10);
            return history;
        });

        UserPointHistoryResponse response = userPointHistoryService.createTransaction(request);

        assertEquals(50, user.getSavePoint());
        assertEquals(-100, response.getChangePoint());
        assertNull(response.getInvoiceId());
        verify(invoiceRepository, never()).findById(any());
    }

    @Test
    void createTransactionShouldRejectWhenUserDoesNotHaveEnoughPoints() {
        UserEntity user = UserEntity.builder()
                .userId(1L)
                .savePoint(20)
                .status(UserStatus.ACTIVE)
                .build();
        UserPointHistoryRequest request = UserPointHistoryRequest.builder()
                .userId(1L)
                .changePoint(50)
                .reason("Doi diem")
                .build();

        when(userRepository.findByUserIdAndStatus(1L, UserStatus.ACTIVE)).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userPointHistoryService.createTransaction(request));
    }

    @Test
    void earnPointsShouldAssignDefaultRankWhenCustomerCreatedFromAdminHasNoRank() {
        MembershipRankEntity normalRank = MembershipRankEntity.builder()
                .id(1)
                .name("Normal")
                .requiredPoint(0)
                .returnPointsTicket(0.01)
                .returnPointsSnack(0.02)
                .priorityLevel(1)
                .status(Boolean.TRUE)
                .build();
        UserEntity customer = UserEntity.builder()
                .userId(5L)
                .savePoint(10)
                .status(UserStatus.ACTIVE)
                .membershipRank(null)
                .build();
        InvoiceEntity invoice = InvoiceEntity.builder()
                .id(9L)
                .customer(customer)
                .detailBookingSnacks(java.util.Collections.emptyList())
                .build();
        invoice.setTickets(java.util.List.of(TicketEntity.builder().id(1L).price(100_000.0).build()));

        when(membershipRankRepository.findByNameAndStatus("Normal", Boolean.TRUE)).thenReturn(Optional.of(normalRank));

        userPointHistoryService.earnPoints(invoice);

        assertEquals(normalRank, customer.getMembershipRank());
        assertEquals(1010, customer.getSavePoint());
        verify(userPointHistoryRepository).save(any(UserPointHistoryEntity.class));
        verify(userRepository).save(customer);
    }
}
