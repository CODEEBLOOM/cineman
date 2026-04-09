package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieReviewRequest {

    @NotNull(message = "Rating score must not be null")
    @Min(value = 1, message = "Rating score must be at least 1")
    @Max(value = 5, message = "Rating score must be at most 5")
    private Integer ratingScore;

    @Size(max = 1000, message = "Comment must be less than or equal to 1000 characters")
    private String comment;
}
