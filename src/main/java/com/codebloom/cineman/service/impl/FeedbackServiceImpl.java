package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
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
    
    // gán nội dung cho SatisfactionLevel
    private static final Map<SatisfactionLevel, String> VIETNAMESE_LABELS = Map.of(
        SatisfactionLevel.VERY_SATISFIED, "Rất hài lòng",
        SatisfactionLevel.SATISFIED, "Hài lòng",
        SatisfactionLevel.NEUTRAL, "Bình thường",
        SatisfactionLevel.DISSATISFIED, "Không hài lòng",
        SatisfactionLevel.VERY_DISSATISFIED, "Rất không hài lòng"
    );

    @Override
    public FeedbackResponse findById(Integer id) {
        FeedbackEntity feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy feedback"));
        return mapToResponse(feedback);
    }

    @Override
    public List<FeedbackResponse> findAll() {
        return feedbackRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> findByUser(String userEmail) {
        return feedbackRepository.findByUser_Email(userEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FeedbackResponse create(FeedbackRequest request, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng"));

        FeedbackTopicEntity topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy chủ đề"));

        FeedbackEntity feedback = FeedbackEntity.builder()
                .content(request.getContent())
                .satisfactionLevel(request.getSatisfactionLevel())
                .reasonForReview(request.getReasonForReview())
                .user(user)
                .topic(topic)
                .build();

        feedback = feedbackRepository.save(feedback);

        
        return mapToResponse(feedbackRepository.save(feedback));
    }

    @Override
    @Transactional
    public FeedbackResponse update(Integer id, FeedbackRequest request, String userEmail) {
        FeedbackEntity feedback = feedbackRepository.findById(id)
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

    @Override
    @Transactional
    public void delete(Integer id, String userEmail) {
        FeedbackEntity feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy feedback"));

        if (!feedback.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Bạn không có quyền xóa feedback này");
        }

        feedbackRepository.delete(feedback);
    }

    private FeedbackResponse mapToResponse(FeedbackEntity feedback) {
        return FeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .content(feedback.getContent())
                .satisfactionLevel(feedback.getSatisfactionLevel())
                .satisfactionLabel(VIETNAMESE_LABELS.get(feedback.getSatisfactionLevel()))
                .reasonForReview(feedback.getReasonForReview())
                .dateFeedback(feedback.getDateFeedback())
                .topicName(feedback.getTopic().getTopicName())
                .userEmail(feedback.getUser().getEmail())
                .build();
        
    }
    
    @Override
    public List<FeedbackResponse> findBySatisfactionLevel(SatisfactionLevel level) {
        List<FeedbackEntity> entities = feedbackRepository.findBySatisfactionLevel(level);
        return entities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    
}
