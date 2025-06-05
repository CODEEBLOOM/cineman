package com.codebloom.cineman.controller.response;


import com.codebloom.cineman.common.enums.Rating;
import com.codebloom.cineman.model.MovieStatusEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MovieResponse {

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

}
