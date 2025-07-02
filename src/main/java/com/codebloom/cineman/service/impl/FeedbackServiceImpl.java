package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.common.enums.SatisfactionLevelUtil;
import com.codebloom.cineman.controller.request.FeedbackRequest;
import com.codebloom.cineman.controller.response.FeedbackResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.*;
import com.codebloom.cineman.service.FeedbackService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackTopicRepository topicRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    
    
    /**
     * Tìm phản hồi theo ID, chỉ lấy phản hồi đang hoạt động.
     *
     * @param id ID của phản hồi.
     * @return {@link FeedbackResponse} tương ứng với ID.
     * @throws DataNotFoundException nếu không tìm thấy hoặc phản hồi đã bị vô hiệu.
     */
    @Override
    public FeedbackResponse findById(Integer id) {
        FeedbackEntity feedback = feedbackRepository.findById(id)
                .filter(FeedbackEntity::getIsActive)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy feedback"));
        return mapToResponse(feedback);
    }

    /**
     * Lấy danh sách tất cả phản hồi đang hoạt động.
     *
     * @return Danh sách {@link FeedbackResponse}.
     */
    @Override
    public List<FeedbackResponse> findAll() {
        return feedbackRepository.findAll().stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsActive()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    
    /**
     * Lấy danh sách tất cả phản hồi bao gồm cả những phản hồi đã bị vô hiệu hóa.
     *
     * @return Danh sách {@link FeedbackResponse} (bao gồm inactive).
     */
    @Override
    public List<FeedbackResponse> findAllIncludeInactive() {
        return feedbackRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    
    /**
     * Lấy danh sách phản hồi của người dùng theo email, chỉ lấy phản hồi đang hoạt động.
     *
     * @param userEmail Email người dùng.
     * @return Danh sách {@link FeedbackResponse} thuộc về người dùng.
     */
    @Override
    public List<FeedbackResponse> findByUser(String userEmail) {
        return feedbackRepository.findByInvoice_Customer_Email(userEmail).stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsActive()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    
    /**
     * Tạo mới phản hồi từ người dùng cho một hóa đơn cụ thể.
     * <p>
     * Người dùng chỉ được phản hồi một lần duy nhất cho mỗi hóa đơn đã thanh toán.
     * Kiểm tra quyền sở hữu hóa đơn, trạng thái hóa đơn và đảm bảo rằng phản hồi chưa tồn tại.
     * </p>
     *
     * @param request   Dữ liệu yêu cầu bao gồm nội dung, mức độ hài lòng, chủ đề và lý do đánh giá.
     * @param userEmail Email người dùng hiện đang xác thực.
     * @return Đối tượng {@link FeedbackResponse} đại diện cho phản hồi đã lưu.
     * @throws DataNotFoundException nếu không tìm thấy người dùng, hóa đơn hoặc chủ đề phản hồi.
     * @throws RuntimeException nếu người dùng không sở hữu hóa đơn, hóa đơn chưa thanh toán,
     *                          hoặc phản hồi đã tồn tại cho hóa đơn này.
     * @throws IllegalArgumentException nếu lý do đánh giá bị bỏ trống.
     */
    @Override
    @Transactional
    public FeedbackResponse save(FeedbackRequest request, String userEmail) {

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new DataNotFoundException("Người dùng không tồn tại"));

        InvoiceEntity invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy hóa đơn"));

        if (!invoice.getCustomer().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Bạn không có quyền gửi feedback cho hóa đơn này");
        }

        if (invoice.getStatus() != InvoiceStatus.PAID) {
            throw new RuntimeException("Trạng thái hóa đơn không hợp lệ");
        }
        
        if (feedbackRepository.existsByInvoice_InvoiceId(invoice.getInvoiceId())) {
            throw new RuntimeException("Hóa đơn này đã được feedback trước đó");
        }

        FeedbackTopicEntity topic = topicRepository.findByTopicIdAndIsActiveTrue(request.getTopicId())
                .orElseThrow(() -> new DataNotFoundException("Chủ đề không tồn tại hoặc đã bị vô hiệu hóa"));

        if (request.getReasonForReview() == null || request.getReasonForReview().trim().isEmpty()) {
            throw new IllegalArgumentException("Lý do đánh giá không được để trống.");
        }

        FeedbackEntity feedback = FeedbackEntity.builder()
                .content(request.getContent()) 
                .satisfactionLevel(request.getSatisfactionLevel())
                .reasonForReview(request.getReasonForReview().trim())
                .topic(topic)
                .invoice(invoice)
                .user(user)
                .dateFeedback(new Date())
                .isActive(true)
                .build();

        return mapToResponse(feedbackRepository.save(feedback));
    }

    
    /**
     * Admin xóa phản hồi (chuyển trạng thái isActive = false).
     *
     * @param id ID của phản hồi cần xóa.
     * @throws DataNotFoundException nếu không tìm thấy phản hồi.
     * @throws RuntimeException nếu phản hồi đã bị xóa hoặc không còn hoạt động.
     */
    @Override
    @Transactional
    public void deleteByAdmin(Integer id) {
        FeedbackEntity feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy feedback"));

        if (!Boolean.TRUE.equals(feedback.getIsActive())) {
            throw new RuntimeException("Feedback đã bị xóa hoặc không còn hoạt động");
        }

        feedback.setIsActive(false);
        feedbackRepository.save(feedback);
    }

    
    /**
     * Lọc danh sách phản hồi theo mức độ hài lòng (satisfaction level), chỉ lấy phản hồi đang hoạt động.
     *
     * @param level Mức độ hài lòng cần lọc.
     * @return Danh sách {@link FeedbackResponse} thỏa điều kiện.
     */
    @Override
    public List<FeedbackResponse> findBySatisfactionLevel(SatisfactionLevel level) {
        return feedbackRepository.findBySatisfactionLevel(level).stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsActive()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    
    /**
     * Chuyển đổi một thực thể phản hồi ({@link FeedbackEntity}) sang đối tượng phản hồi phản hồi DTO ({@link FeedbackResponse}).
     * 
     * @param feedback Thực thể phản hồi từ cơ sở dữ liệu.
     * @return Đối tượng {@link FeedbackResponse} tương ứng với dữ liệu đầu vào.
     */
    private FeedbackResponse mapToResponse(FeedbackEntity feedback) {
        return FeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .content(feedback.getContent())
                .satisfactionLevel(feedback.getSatisfactionLevel())
                .satisfactionLabel(SatisfactionLevelUtil.getLabel(feedback.getSatisfactionLevel()))
                .reasonForReview(feedback.getReasonForReview())
                .dateFeedback(feedback.getDateFeedback())
                .topicName(feedback.getTopic().getTopicName())
                .userEmail(feedback.getUser().getEmail())
                .build();
    }

}
