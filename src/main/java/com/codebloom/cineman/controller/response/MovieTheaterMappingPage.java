package com.codebloom.cineman.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MovieTheaterMappingPage {

    private List<MovieTheaterMappingResponse> movieTheaterMappings;
    private MetaResponse meta;
}
