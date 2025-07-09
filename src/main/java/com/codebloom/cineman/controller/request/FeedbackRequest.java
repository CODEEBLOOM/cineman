package com.codebloom.cineman.controller.request;

import java.util.Date;

import com.codebloom.cineman.common.enums.SatisfactionLevel;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    @NotNull(message = "Content must not be null")
    @Min(1)
    private Integer feedbackId; 

    @Size(max = 100, message = "Title of movie must be less than 100 characters!")
    @NotNull(message = "Content must not be null")
    private String content;

    @NotNull(message = "Satisfaction level must not be null")
    private SatisfactionLevel satisfactionLevel;
    
    @NotNull(message = "Reason For Review must not be null")
    @Size(max = 250, message = "Title of movie must be less than 250 characters!")
    private String reasonForReview;

    @NotNull(message = "Topic ID must not be null")
    private Integer topicId;
    
    @NotNull(message = "Invoice ID must not be null")
    private Long invoiceId;

}
