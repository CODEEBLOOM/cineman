package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.Rating;
import com.codebloom.cineman.model.MovieStatusEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MovieCreationRequest {
    private MovieStatusEntity status;
    private String title;
    private String synopsis;
    private String detailDescription;
    private Date releaseDate;
    private String language;
    private Integer duration;
    private Rating rating;
    private Integer age;
    private String trailerLink;
    private String posterImage;
    private String bannerImage;
    private Date createdAt;
    private Date updatedAt;
    private Boolean IsActive ;
}
