package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.model.SeatMapEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SeatMapResponse {

    private List<SeatMapEntity> seatMapEntity;
    private MetaResponse meta;

}
