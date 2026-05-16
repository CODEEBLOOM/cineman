package com.codebloom.cineman.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieTheaterMappingResponse {

    private Integer movieTheaterMappingId;
    private Integer movieId;
    private String movieTitle;
    private String movieStatus;
    private Integer movieTheaterId;
    private String movieTheaterName;
    private Boolean active;
}
