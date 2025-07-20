package com.codebloom.cineman.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackTopicResponse {
	 private Integer topicId;
	 private String topicName;
	 private String description;
}
