package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackTopicRequest {
    @NotBlank(message = "Topic name must not be blank!")
    @Size(max = 100, message = "Title of movie must be less than 100 character !")
    private String topicName;
    
    private String description;
}
