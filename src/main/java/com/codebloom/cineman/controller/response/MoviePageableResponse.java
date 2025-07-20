package com.codebloom.cineman.controller.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoviePageableResponse {

    private List<MovieResponse> movies;
    private MetaResponse meta;

}
