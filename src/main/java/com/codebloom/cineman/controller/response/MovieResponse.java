package com.codebloom.cineman.controller.response;


import com.codebloom.cineman.common.enums.Rating;
import com.codebloom.cineman.model.GenresEntity;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class MovieResponse {

    private Integer movieId;
    private String status;
    private String title;
    private Date releaseDate;
    private String language;
    private Integer duration;
    private Rating rating;
    private Integer age;
    private String trailerLink;
    private String posterImage;
    private String bannerImage;
    private List<GenresEntity> genres ;
}
