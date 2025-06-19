package com.codebloom.cineman.controller.request;

import java.util.Date;

import com.codebloom.cineman.common.enums.SatisfactionLevel;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackRequest {

    private Integer feedbackId; 

    @Size(max = 100, message = "Title of movie must be less than 100 characters!")
    @NotNull(message = "Content must not be null")
    private String content;

    @NotNull(message = "Satisfaction level must not be null")
    private SatisfactionLevel satisfactionLevel;

    private String reasonForReview;

    @NotNull(message = "Topic ID must not be null")
    private Integer topicId;

}
