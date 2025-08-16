package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.request.UserPointHistoryRequest;
import com.codebloom.cineman.controller.response.UserPointHistoryResponse;
import com.codebloom.cineman.Exception.ConflictException;
import com.codebloom.cineman.Exception.DataNotFoundException;
import com.codebloom.cineman.model.*;
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
@Slf4j( topic = "USER-POINT-HISTORY-SERVICE" )
public class UserPointHistoryServiceImpl implements UserPointHistoryService {

    private final UserPointHistoryRepository userPointHistoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;


    /**
     * Tạo mới một lịch sử đổi điểm cho người dùng
     * @param request thống tin người dùng trên request
     * @return UserPointHistoryResponse
     */
    @Override
    @Transactional
    public UserPointHistoryResponse createTransaction(UserPointHistoryRequest request) {
        log.info("Create transaction with request: invoice id:{} and user id:{}", request.getInvoiceId(), request.getUserId());
        InvoiceEntity invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy hóa đơn có id: " + request.getInvoiceId()));

        UserEntity user = userRepository.findByUserIdAndStatus(request.getUserId(), UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng có id: " + request.getUserId()));

        // Thêm thông tin đổi điểm vào lịch sử //
        UserPointHistoryEntity userPointHistory = UserPointHistoryEntity.builder()
                .changePoint(-request.getChangePoint())
                .reason(request.getReason())
                .user(user)
                .invoice(invoice)
                .build();
        userPointHistoryRepository.save(userPointHistory);
        log.info("Created transaction with id:{}", userPointHistory.getId());

        // Trừ điểm tích lũy người dùng //
        int newPoint = user.getSavePoint() - request.getChangePoint();
        if (newPoint < 0) {
            throw new ConflictException("Điểm tích lũy không đủ để quy đổi !");
        }
        user.setSavePoint(newPoint);
        userRepository.save(user);
        log.info("Updated user point with id:{} and new point: {}", user.getUserId(), user.getSavePoint());

        return mapToUserPointHistoryResponse(userPointHistory);
    }


    /**
     * Update transaction - trường hợp hoàn trả lại điểm cho người dùng khi thanh toán thất bại hoặc tích lũy điểm cho người dùng
     * @param request thống tin người dùng trên request
     * @param vnTnxRef thông tin hóa đơn VNPay trả về
     * @return UserPointHistoryResponse
     */
    @Override
    @Transactional
    public UserPointHistoryResponse refundTransaction(UserPointHistoryRequest request, String vnTnxRef) {
        log.info("Update transaction with request: invoice id:{} and user id:{}", request.getInvoiceId(), request.getUserId());

        InvoiceEntity invoice = invoiceRepository.findByVnTxnRef(vnTnxRef)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy hóa đơn có vnTxnRef: " + vnTnxRef));

        UserEntity user = userRepository.findByUserIdAndStatus(request.getUserId(), UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng có id: " + request.getUserId()));

        // Thêm thông tin đổi điểm vào lịch sử //
        UserPointHistoryEntity userPointHistory = UserPointHistoryEntity.builder()
                .changePoint(-request.getChangePoint())
                .reason(request.getReason())
                .user(user)
                .invoice(invoice)
                .build();
        userPointHistoryRepository.save(userPointHistory);
        log.info("Updated transaction with id:{} and new point: {}", userPointHistory.getId(), userPointHistory.getChangePoint());

        // Cộng điểm tích lũy người dùng //
        int newPoint = user.getSavePoint() + request.getChangePoint();
        user.setSavePoint(newPoint);
        userRepository.save(user);
        log.info("Updated user point success with user id:{} and new point: {}", user.getUserId(), user.getSavePoint());

        return mapToUserPointHistoryResponse(userPointHistory);
    }


    /**
     * Tích điểm cho người dùng khi thanh toán hóa đơn thành công
     * @param invoice hóa đơn thành toán thành công
     * @return UserPointHistoryResponse
     */
    @Override
    public UserPointHistoryResponse earnPoints(InvoiceEntity invoice) {
        log.info("Earn points with invoice id:{}", invoice.getId());
        MembershipRankEntity membershipRank = invoice.getCustomer().getMembershipRank();

        // Lấy tổng tiền vé của hóa đơn //
        Double totalMoneyTicket = invoice.getTickets().stream()
                .mapToDouble(TicketEntity::getPrice)
                .sum();

        Double totalMoneySnack = !invoice.getDetailBookingSnacks().isEmpty() ?
                invoice.getDetailBookingSnacks().stream()
                        .mapToDouble(DetailBookingSnackEntity::getTotalMoney)
                        .sum() : 0.0;

        // Tiến hành tính điểm cộng lại cho khách hàng  //
        int totalPoint = (int) (
                Math.ceil(totalMoneyTicket * membershipRank.getReturnPointsTicket())
                        + Math.ceil(totalMoneySnack * membershipRank.getReturnPointsSnack()));

        UserPointHistoryEntity userPointHistory = UserPointHistoryEntity.builder()
                .changePoint(totalPoint)
                .reason("Tích điểm hóa đơn cho khách hàng")
                .user(invoice.getCustomer())
                .invoice(invoice)
                .build();
        userPointHistoryRepository.save(userPointHistory);

        // Cộng điểm tích lũy người dùng //
        int newPoint = invoice.getCustomer().getSavePoint() + totalPoint;
        invoice.getCustomer().setSavePoint(newPoint);
        userRepository.save(invoice.getCustomer());
        log.info("Earn points success with invoice id:{} and point:{}", invoice.getId(), totalPoint);
        return null;
    }

    /***
     * Lấy lịch sử thay đổi điểm của người dùng
     * @param userId id người dùng
     * @return List<UserPointHistoryResponse>
     */
    @Override
    public List<UserPointHistoryResponse> getAllHistory(long userId) {
        log.info("Get all history point with user id:{}", userId);
        UserEntity user = userRepository.findByUserIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng có id: " + userId));
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
                .invoiceId(userPointHistory.getInvoice().getId())
                .changePoint(userPointHistory.getChangePoint())
                .reason(userPointHistory.getReason())
                .createdAt(userPointHistory.getCreatedAt())
                .updatedAt(userPointHistory.getUpdatedAt())
                .build();
    }
}
