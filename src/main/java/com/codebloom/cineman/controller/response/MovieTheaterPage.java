package com.codebloom.cineman.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieTheaterPage {

    private List<MovieTheaterResponse> movieTheaters;
    private MetaResponse meta;

}
