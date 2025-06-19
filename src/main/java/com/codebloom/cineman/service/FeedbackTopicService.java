package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.FeedbackTopicRequest;
import com.codebloom.cineman.controller.response.FeedbackTopicResponse;

import java.util.List;

public interface FeedbackTopicService {
	void delete(Integer id);
	
    List<FeedbackTopicResponse> findAll();
    
    FeedbackTopicResponse findById(Integer id);
    
    FeedbackTopicResponse save(FeedbackTopicRequest feedbackTopic);
    
    FeedbackTopicResponse update(Integer id, FeedbackTopicRequest feedbackTopic);
    
}
