package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.FeedbackTopicRequest;
import com.codebloom.cineman.controller.response.FeedbackTopicResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.FeedbackTopicEntity;
import com.codebloom.cineman.repository.FeedbackTopicRepository;
import com.codebloom.cineman.service.FeedbackTopicService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FeedbackTopicServiceImpl implements FeedbackTopicService {
	
	private final FeedbackTopicRepository feedbackTopicRepository;
    private final ModelMapper modelMapper;
	
    // xóa hẳn khỏi db
    @Override
    public void delete(Integer id) {
        FeedbackTopicEntity entity = feedbackTopicRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Feedback topic not found"));

        feedbackTopicRepository.delete(entity);
    }
    
    @Override
    public List<FeedbackTopicResponse> findAll() {
        return feedbackTopicRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, FeedbackTopicResponse.class))
                .toList();
    }

    @Override
    public FeedbackTopicResponse findById(Integer id) {
        FeedbackTopicEntity entity = feedbackTopicRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Feedback topic not found"));
        return modelMapper.map(entity, FeedbackTopicResponse.class);
    }

    @Override
    public FeedbackTopicResponse save(FeedbackTopicRequest feedbackTopicDto) {
        feedbackTopicRepository.findByTopicName(feedbackTopicDto.getTopicName())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Chủ đề " + " / "+ feedbackTopicDto.getTopicName() + " / " + "đã tồn tại!");
                });

        FeedbackTopicEntity entity = modelMapper.map(feedbackTopicDto, FeedbackTopicEntity.class);
        FeedbackTopicEntity saved = feedbackTopicRepository.save(entity);
        return modelMapper.map(saved, FeedbackTopicResponse.class);
    }

    @Override
    public FeedbackTopicResponse update(Integer id, FeedbackTopicRequest request) {
        FeedbackTopicEntity entity = feedbackTopicRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Feedback topic not found"));

        entity.setTopicName(request.getTopicName());
        entity.setDescription(request.getDescription());

        FeedbackTopicEntity updated = feedbackTopicRepository.save(entity);
        return modelMapper.map(updated, FeedbackTopicResponse.class);
    }
}
