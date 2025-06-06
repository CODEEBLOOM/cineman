package com.codebloom.cineman.controller.response;


import com.codebloom.cineman.common.enums.Rating;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MovieResponse {
     @JsonIgnore
    private Integer movieId;
    private MovieStatusEntity status;
  //  @JsonIgnore // cái này nó giúp mình ẩn 1 số thông tin mà mình không muốn cho người ta ở clinent biết
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

    private Boolean isActive;
}
