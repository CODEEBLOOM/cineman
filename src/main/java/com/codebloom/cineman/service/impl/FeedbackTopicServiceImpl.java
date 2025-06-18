package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.FeedbackTopicRequest;
import com.codebloom.cineman.controller.response.FeedbackTopicResponse;
import com.codebloom.cineman.model.FeedbackTopicEntity;
import com.codebloom.cineman.repository.FeedbackTopicRepository;
import com.codebloom.cineman.service.FeedbackTopicService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.config.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackTopicServiceImpl implements FeedbackTopicService {

    private final FeedbackTopicRepository feedbackTopicRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<FeedbackTopicResponse> findAll() {
        List<FeedbackTopicEntity> entities = feedbackTopicRepository.findAll();
        List<FeedbackTopicResponse> responses = new ArrayList<>();

        for (FeedbackTopicEntity entity : entities) {
            FeedbackTopicResponse response = new FeedbackTopicResponse();
            response.setTopicId(entity.getTopicId());
            response.setTopicName(entity.getTopicName());
            response.setDescription(entity.getDescription());
            responses.add(response);
        }
        return responses;
    }


    @Override
    public FeedbackTopicResponse findById(Integer id) {
        FeedbackTopicEntity topic = feedbackTopicRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy chủ đề feedback với ID: " + id));
        return modelMapper.map(topic, FeedbackTopicResponse.class);
    }

//    @Override
//    public FeedbackTopicResponse create(FeedbackTopicRequest request) {
//        FeedbackTopicEntity topic = modelMapper.map(request, FeedbackTopicEntity.class);
//        FeedbackTopicEntity saved = feedbackTopicRepository.save(topic);
//        return modelMapper.map(saved, FeedbackTopicResponse.class);
//    }
    
    @Override
    public FeedbackTopicResponse create(FeedbackTopicRequest request) {

        Optional<FeedbackTopicEntity> existing = feedbackTopicRepository.findByTopicName(request.getTopicName());
        if (existing.isPresent()) {
            throw new DataExistingException("Chủ đề đã tồn tại");
        }

        FeedbackTopicEntity entity = FeedbackTopicEntity.builder()
            .topicName(request.getTopicName())
            .description(request.getDescription())
            .build();

        feedbackTopicRepository.save(entity);

        return modelMapper.map(entity, FeedbackTopicResponse.class);
    }

    @Override
    public FeedbackTopicResponse update(Integer id, FeedbackTopicRequest request) {
        FeedbackTopicEntity existing = feedbackTopicRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy chủ đề để cập nhật"));

        existing.setTopicName(request.getTopicName());
        existing.setDescription(request.getDescription());

        FeedbackTopicEntity updated = feedbackTopicRepository.save(existing);
        return modelMapper.map(updated, FeedbackTopicResponse.class);
    }

    @Override
    public void delete(Integer id) {
        FeedbackTopicEntity existing = feedbackTopicRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy chủ đề để xóa"));
        feedbackTopicRepository.delete(existing);
    }
}
