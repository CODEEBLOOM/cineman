package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.controller.request.FeedbackRequest;
import com.codebloom.cineman.controller.response.FeedbackResponse;

import java.util.List;

public interface FeedbackService {
	void delete(Integer id, String userEmail);
	
	void deleteByAdmin(Integer id);
	
	List<FeedbackResponse> findAll();
    
    List<FeedbackResponse> findByUser(String userEmail);
    
    List<FeedbackResponse> findBySatisfactionLevel(SatisfactionLevel level);
    
    FeedbackResponse findById(Integer id); 
    
    FeedbackResponse save(FeedbackRequest request, String userEmail);
    
    FeedbackResponse update(Integer id, FeedbackRequest request, String userEmail);
    
//    FeedbackResponse update(Integer feedbackId, FeedbackRequest request, Long userId);
    
    List<FeedbackResponse> findAllIncludeInactive();

	

    
}
