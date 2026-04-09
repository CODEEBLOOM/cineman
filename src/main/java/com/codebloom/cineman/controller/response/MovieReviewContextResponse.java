package com.codebloom.cineman.controller.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieReviewContextResponse {

    private Double averageRating;
    private Long reviewCount;
    private Boolean authenticated;
    private Boolean canReview;
    private Boolean hasReviewed;
    private MovieReviewResponse myReview;
}
