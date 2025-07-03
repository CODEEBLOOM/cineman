package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CinemaTheaterResponse {

    private List<CinemaTheaterEntity> cinemaTheaters;
    private MetaResponse meta;

}
