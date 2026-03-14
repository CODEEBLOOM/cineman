package com.codebloom.cineman.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@Builder
public class MovieResponseNew {
    private Integer movieId;
    private String title;
    private String synopsis;
    private String detailDescription;
    private Date releaseDate;
    private Date endDate;
    private String language;
    private Integer duration;
    private String rating;
    private Integer age;
    private String trailerLink;
    private String posterImage;
    private String bannerImage;
    private String status;
}
