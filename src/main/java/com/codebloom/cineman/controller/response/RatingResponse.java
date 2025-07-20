package com.codebloom.cineman.controller.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {
    private Long ticketId;
    private Integer movieId;
    private String movieTitle;
    private Integer rating;
    private Double averageRating;
    private String userEmail;
}
