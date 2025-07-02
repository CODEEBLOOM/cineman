package com.codebloom.cineman.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RatingResponse {
    private Long ticketId;
    private Integer movieId;
    private String movieTitle;
    private Integer rating;
    private Double averageRating;
    private String userEmail;
}
