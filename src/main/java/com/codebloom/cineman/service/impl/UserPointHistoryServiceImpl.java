package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.request.UserPointHistoryRequest;
import com.codebloom.cineman.controller.response.UserPointHistoryResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.DetailBookingSnackEntity;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.MembershipRankEntity;
import com.codebloom.cineman.model.TicketEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.model.UserPointHistoryEntity;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.repository.UserPointHistoryRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.UserPointHistoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-POINT-HISTORY-SERVICE")
public class UserPointHistoryServiceImpl implements UserPointHistoryService {

    private final UserPointHistoryRepository userPointHistoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserPointHistoryResponse createTransaction(UserPointHistoryRequest request) {
        log.info("Create transaction with request: invoice id:{} and user id:{}", request.getInvoiceId(), request.getUserId());

        InvoiceEntity invoice = null;
        if (request.getInvoiceId() != null) {
            invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new DataNotFoundException("Khong tim thay hoa don co id: " + request.getInvoiceId()));
        }

        UserEntity user = userRepository.findByUserIdAndStatus(request.getUserId(), UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay nguoi dung co id: " + request.getUserId()));

        UserPointHistoryEntity userPointHistory = UserPointHistoryEntity.builder()
                .changePoint(-request.getChangePoint())
                .reason(request.getReason())
                .user(user)
                .invoice(invoice)
                .build();
        userPointHistoryRepository.save(userPointHistory);
        log.info("Created transaction with id:{}", userPointHistory.getId());

        int newPoint = user.getSavePoint() - request.getChangePoint();
        if (newPoint < 0) {
            throw new ConflictException("Diem tich luy khong du de quy doi!");
        }
        user.setSavePoint(newPoint);
        userRepository.save(user);
        log.info("Updated user point with id:{} and new point: {}", user.getUserId(), user.getSavePoint());

        return mapToUserPointHistoryResponse(userPointHistory);
    }

    @Override
    @Transactional
    public UserPointHistoryResponse refundTransaction(UserPointHistoryRequest request, String vnTnxRef) {
        log.info("Update transaction with request: invoice id:{} and user id:{}", request.getInvoiceId(), request.getUserId());

        InvoiceEntity invoice = invoiceRepository.findByVnTxnRef(vnTnxRef)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay hoa don co vnTxnRef: " + vnTnxRef));

        UserEntity user = userRepository.findByUserIdAndStatus(request.getUserId(), UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay nguoi dung co id: " + request.getUserId()));

        UserPointHistoryEntity userPointHistory = UserPointHistoryEntity.builder()
                .changePoint(-request.getChangePoint())
                .reason(request.getReason())
                .user(user)
                .invoice(invoice)
                .build();
        userPointHistoryRepository.save(userPointHistory);
        log.info("Updated transaction with id:{} and new point: {}", userPointHistory.getId(), userPointHistory.getChangePoint());

        int newPoint = user.getSavePoint() + request.getChangePoint();
        user.setSavePoint(newPoint);
        userRepository.save(user);
        log.info("Updated user point success with user id:{} and new point: {}", user.getUserId(), user.getSavePoint());

        return mapToUserPointHistoryResponse(userPointHistory);
    }

    @Override
    public UserPointHistoryResponse earnPoints(InvoiceEntity invoice) {
        log.info("Earn points with invoice id:{}", invoice.getId());
        MembershipRankEntity membershipRank = invoice.getCustomer().getMembershipRank();

        Double totalMoneyTicket = invoice.getTickets().stream()
                .mapToDouble(TicketEntity::getPrice)
                .sum();

        Double totalMoneySnack = !invoice.getDetailBookingSnacks().isEmpty()
                ? invoice.getDetailBookingSnacks().stream()
                .mapToDouble(DetailBookingSnackEntity::getTotalMoney)
                .sum()
                : 0.0;

        int totalPoint = (int) (
                Math.ceil(totalMoneyTicket * membershipRank.getReturnPointsTicket())
                        + Math.ceil(totalMoneySnack * membershipRank.getReturnPointsSnack()));

        UserPointHistoryEntity userPointHistory = UserPointHistoryEntity.builder()
                .changePoint(totalPoint)
                .reason("Tich diem hoa don cho khach hang")
                .user(invoice.getCustomer())
                .invoice(invoice)
                .build();
        userPointHistoryRepository.save(userPointHistory);

        int newPoint = invoice.getCustomer().getSavePoint() + totalPoint;
        invoice.getCustomer().setSavePoint(newPoint);
        userRepository.save(invoice.getCustomer());
        log.info("Earn points success with invoice id:{} and point:{}", invoice.getId(), totalPoint);
        return null;
    }

    @Override
    public List<UserPointHistoryResponse> getAllHistory(long userId) {
        log.info("Get all history point with user id:{}", userId);
        UserEntity user = userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay nguoi dung co id: " + userId));
        List<UserPointHistoryEntity> userPointHistories = userPointHistoryRepository.findAllByUser(user);
        if (!userPointHistories.isEmpty()) {
            return userPointHistories.stream().map(this::mapToUserPointHistoryResponse).toList();
        }
        return null;
    }

    private UserPointHistoryResponse mapToUserPointHistoryResponse(UserPointHistoryEntity userPointHistory) {
        return UserPointHistoryResponse.builder()
                .id(userPointHistory.getId())
                .userId(userPointHistory.getUser().getUserId())
                .invoiceId(userPointHistory.getInvoice() == null ? null : userPointHistory.getInvoice().getId())
                .changePoint(userPointHistory.getChangePoint())
                .reason(userPointHistory.getReason())
                .createdAt(userPointHistory.getCreatedAt())
                .updatedAt(userPointHistory.getUpdatedAt())
                .build();
    }
}
