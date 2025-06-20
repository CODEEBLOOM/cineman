package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.common.enums.SatisfactionLevelUtil;
import com.codebloom.cineman.common.enums.UserType;
import com.codebloom.cineman.controller.request.FeedbackRequest;
import com.codebloom.cineman.controller.response.FeedbackResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.FeedbackRepository;
import com.codebloom.cineman.repository.FeedbackTopicRepository;
import com.codebloom.cineman.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final FeedbackTopicRepository topicRepository;

    
    /**
     * Lấy một feedback cụ thể theo ID, chỉ nếu feedback đang hoạt động (isActive = true).
     *
     * @param id ID của feedback cần lấy
     * @return FeedbackResponse tương ứng với feedback
     * @throws DataNotFoundException nếu không tìm thấy hoặc feedback đã bị xoá mềm
     */
    @Override
    public FeedbackResponse findById(Integer id) {
        FeedbackEntity feedback = feedbackRepository.findById(id)
                .filter(FeedbackEntity::getIsActive)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy feedback"));
        return mapToResponse(feedback);
    }

    
    /**
     * Lấy tất cả feedback đang hoạt động (isActive = true).
     *
     * @return Danh sách feedback đang hoạt động
     */
    @Override
    public List<FeedbackResponse> findAll() {
        return feedbackRepository.findAll()
                .stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsActive()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy tất cả feedback bao gồm cả đã bị vô hiệu hoá (isActive = false).
     * Chức năng dành cho admin.
     *
     * @return Danh sách tất cả feedback (kể cả feedback đã bị xoá mềm)
     */
    @Override
    public List<FeedbackResponse> findAllIncludeInactive() {
        return feedbackRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy tất cả feedback thuộc về một người dùng cụ thể.
     *
     * @param userEmail Email của người dùng
     * @return Danh sách feedback của người dùng đó (isActive = true)
     */
    @Override
    public List<FeedbackResponse> findByUser(String userEmail) {
        return feedbackRepository.findByUser_Email(userEmail)
                .stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsActive()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Tạo mới một feedback.
     *
     * @param request Dữ liệu đầu vào từ người dùng
     * @param userEmail Email người dùng gửi feedback
     * @return FeedbackResponse sau khi lưu thành công
     * @throws DataNotFoundException nếu không tìm thấy user hoặc topic
     */
    @Override
    @Transactional
    public FeedbackResponse save(FeedbackRequest request, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng"));

        FeedbackTopicEntity topic = topicRepository.findByTopicIdAndIsActiveTrue(request.getTopicId())
                .orElseThrow(() -> new DataNotFoundException("Chủ đề không tồn tại hoặc đã bị vô hiệu hóa"));

        FeedbackEntity feedback = FeedbackEntity.builder()
                .content(request.getContent())
                .satisfactionLevel(request.getSatisfactionLevel())
                .reasonForReview(request.getReasonForReview())
                .user(user)
                .topic(topic)
                .isActive(true)
                .build();

        return mapToResponse(feedbackRepository.save(feedback));
    }

    
    /**
     * Cập nhật nội dung một feedback — chỉ người sở hữu mới có thể cập nhật.
     *
     * @param id ID của feedback
     * @param request Dữ liệu cần cập nhật
     * @param userEmail Email của người gửi yêu cầu
     * @return FeedbackResponse đã được cập nhật
     * @throws DataNotFoundException nếu feedback không tồn tại
     * @throws RuntimeException nếu không phải chủ sở hữu
     */
    @Override
    @Transactional
    public FeedbackResponse update(Integer id, FeedbackRequest request, String userEmail) {
        FeedbackEntity feedback = feedbackRepository.findById(id)
                .filter(FeedbackEntity::getIsActive)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy feedback"));

        if (!feedback.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Bạn không có quyền cập nhật feedback này");
        }

        FeedbackTopicEntity topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy chủ đề"));

        feedback.setContent(request.getContent());
        feedback.setSatisfactionLevel(request.getSatisfactionLevel());
        feedback.setReasonForReview(request.getReasonForReview());
        feedback.setTopic(topic);

        return mapToResponse(feedbackRepository.save(feedback));
    }

    /**
     * Xoá mềm feedback — chỉ người dùng sở hữu mới có thể xóa.
     *
     * @param id ID của feedback cần xoá
     * @param userEmail Email của người yêu cầu
     * @throws DataNotFoundException nếu feedback không tồn tại hoặc đã bị xoá
     * @throws RuntimeException nếu không phải chủ sở hữu
     */
    @Override
    @Transactional
    public void delete(Integer id, String userEmail) {
        FeedbackEntity feedback = feedbackRepository.findById(id)
                .filter(FeedbackEntity::getIsActive)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy feedback"));

        if (!feedback.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Bạn không có quyền xóa feedback này");
        }

        feedback.setIsActive(false);
        feedbackRepository.save(feedback);
    }

    /**
     * Xoá mềm feedback — chỉ dành cho ADMIN (xóa bất kỳ feedback nào).
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
     * Lọc feedback theo mức độ hài lòng (isActive = true).
     *
     * @param level Mức độ hài lòng (SatisfactionLevel)
     * @return Danh sách feedback phù hợp
     */
    @Override
    public List<FeedbackResponse> findBySatisfactionLevel(SatisfactionLevel level) {
        return feedbackRepository.findBySatisfactionLevel(level)
                .stream()
                .filter(fil -> Boolean.TRUE.equals(fil.getIsActive()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Chuyển đổi một FeedbackEntity sang FeedbackResponse.
     *
     * @param feedback Thực thể feedback trong database
     * @return DTO phản hồi hiển thị cho client
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
