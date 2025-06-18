package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.controller.request.FeedbackRequest;
import com.codebloom.cineman.controller.response.FeedbackResponse;

import java.util.List;

public interface FeedbackService {
	
    FeedbackResponse findById(Integer id); 
    List<FeedbackResponse> findAll();
    List<FeedbackResponse> findByUser(String userEmail); 
    FeedbackResponse create(FeedbackRequest request, String userEmail);
    FeedbackResponse update(Integer id, FeedbackRequest request, String userEmail);
    void delete(Integer id, String userEmail);
    List<FeedbackResponse> findBySatisfactionLevel(SatisfactionLevel level);
}
