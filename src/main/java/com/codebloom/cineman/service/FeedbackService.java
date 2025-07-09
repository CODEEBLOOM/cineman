package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.controller.request.FeedbackRequest;
import com.codebloom.cineman.controller.response.FeedbackResponse;

import java.util.List;

public interface FeedbackService {

    void deleteByAdmin(Integer id);

    List<FeedbackResponse> findAll();

    List<FeedbackResponse> findByUser(Long userId);

    List<FeedbackResponse> findBySatisfactionLevel(SatisfactionLevel level);

    FeedbackResponse findById(Integer id);

    FeedbackResponse save(FeedbackRequest request, Long userId);

    List<FeedbackResponse> findAllIncludeInactive();
    
    List<FeedbackResponse> findByUserEmail(String email);

}
