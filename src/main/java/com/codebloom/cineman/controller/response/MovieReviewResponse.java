package com.codebloom.cineman.controller.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieReviewResponse {

    private Long reviewId;
    private Integer movieId;
    private Integer ratingScore;
    private String comment;
    private Date createdAt;
    private Date updatedAt;
    private Long userId;
    private String userFullName;
    private String userAvatar;
}
