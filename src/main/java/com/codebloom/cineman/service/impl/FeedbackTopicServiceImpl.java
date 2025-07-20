package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.FeedbackTopicRequest;
import com.codebloom.cineman.controller.response.FeedbackTopicResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.exception.InvalidDataException;
import com.codebloom.cineman.model.FeedbackTopicEntity;
import com.codebloom.cineman.repository.FeedbackTopicRepository;
import com.codebloom.cineman.service.FeedbackTopicService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
    public class FeedbackTopicServiceImpl implements FeedbackTopicService {

    private final FeedbackTopicRepository feedbackTopicRepository;
    private final ModelMapper modelMapper;

    /**
     * Xoá mềm một chủ đề feedback (set isActive = false).
     *
     * @param id ID của chủ đề cần xoá
     * @throws DataNotFoundException nếu không tìm thấy chủ đề
     */
    @Override
    public void delete(Integer id) {
        FeedbackTopicEntity entity = feedbackTopicRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Feedback topic not found"));
        
        entity.setIsActive(false);
        feedbackTopicRepository.save(entity);
    }

    /**
     * Lấy tất cả các chủ đề feedback đang hoạt động (isActive = true).
     *
     * @return Danh sách chủ đề feedback (FeedbackTopicResponse)
     */
    @Override
    public List<FeedbackTopicResponse> findAll() {
        return feedbackTopicRepository.findByIsActiveTrue()
                .stream()
                .map(entity -> modelMapper.map(entity, FeedbackTopicResponse.class))
                .toList();
    }

    /**
     * Tìm một chủ đề feedback theo ID, chỉ nếu đang hoạt động (isActive = true).
     *
     * @param id ID của chủ đề
     * @return Thông tin chủ đề feedback (FeedbackTopicResponse)
     * @throws DataNotFoundException nếu không tìm thấy hoặc chủ đề đã bị vô hiệu hóa
     */
    @Override
    public FeedbackTopicResponse findById(Integer id) {
        FeedbackTopicEntity entity = feedbackTopicRepository.findById(id)
                .filter(FeedbackTopicEntity::getIsActive) 
                .orElseThrow(() -> new DataNotFoundException("Feedback topic not found or inactive"));
        return modelMapper.map(entity, FeedbackTopicResponse.class);
    }

    /**
     * Tạo mới một chủ đề feedback.
     *
     * @param feedbackTopicDto Dữ liệu chủ đề mới
     * @return Thông tin chủ đề feedback sau khi tạo
     * @throws InvalidDataException nếu tên chủ đề đã tồn tại
     */
    @Override
    public FeedbackTopicResponse save(FeedbackTopicRequest feedbackTopicDto) {
        feedbackTopicRepository.findByTopicName(feedbackTopicDto.getTopicName())
                .ifPresent(existing -> {
                    throw new InvalidDataException("Chủ đề " + " / " + feedbackTopicDto.getTopicName() + " / " + "đã tồn tại!");
                });

        FeedbackTopicEntity entity = modelMapper.map(feedbackTopicDto, FeedbackTopicEntity.class);
        entity.setIsActive(true); 
        FeedbackTopicEntity saved = feedbackTopicRepository.save(entity);
        return modelMapper.map(saved, FeedbackTopicResponse.class);
    }

    /**
     * Cập nhật tên và mô tả của một chủ đề feedback.
     *
     * @param id ID của chủ đề cần cập nhật
     * @param request Dữ liệu cập nhật
     * @return Chủ đề feedback sau khi cập nhật
     * @throws DataNotFoundException nếu không tìm thấy hoặc chủ đề đã bị vô hiệu hóa
     */
    @Override
    public FeedbackTopicResponse update(Integer id, FeedbackTopicRequest request) {
        FeedbackTopicEntity entity = feedbackTopicRepository.findById(id)
                .filter(FeedbackTopicEntity::getIsActive) 
                .orElseThrow(() -> new DataNotFoundException("Feedback topic not found or inactive"));

        boolean exists = feedbackTopicRepository.isTopicNameDuplicated(request.getTopicName(), id);

        if (exists) {
            throw new InvalidDataException("Tên chủ đề đã tồn tại, vui lòng chọn tên khác.");
        }

        entity.setTopicName(request.getTopicName());
        entity.setDescription(request.getDescription());

        FeedbackTopicEntity updated = feedbackTopicRepository.save(entity);
        return modelMapper.map(updated, FeedbackTopicResponse.class);
    }

}
