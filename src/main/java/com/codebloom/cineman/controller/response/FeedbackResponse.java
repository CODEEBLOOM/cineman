package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.SatisfactionLevel;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {
	
    private Integer feedbackId;
    private String content;
    private SatisfactionLevel satisfactionLevel;
    private String satisfactionLabel; // Mô tả tiếng Việt
    private String reasonForReview;
    private Date dateFeedback;
    private String topicName;
    private String userEmail;
    
}
