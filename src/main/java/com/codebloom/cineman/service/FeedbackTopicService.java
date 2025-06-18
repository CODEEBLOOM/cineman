package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.FeedbackTopicRequest;
import com.codebloom.cineman.controller.response.FeedbackTopicResponse;

import java.util.List;

public interface FeedbackTopicService {
    List<FeedbackTopicResponse> findAll();
    FeedbackTopicResponse findById(Integer id);
    FeedbackTopicResponse create(FeedbackTopicRequest feedbackTopic);
    FeedbackTopicResponse update(Integer id, FeedbackTopicRequest feedbackTopic);
    void delete(Integer id);
}
