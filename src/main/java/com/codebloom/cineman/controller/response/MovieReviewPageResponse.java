package com.codebloom.cineman.controller.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieReviewPageResponse {

    private MetaResponse meta;
    private Double averageRating;
    private Long reviewCount;
    private List<MovieReviewResponse> reviews;
}
