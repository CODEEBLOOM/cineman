package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.controller.response.DummySeat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SeatMapResponse {

    private Integer cinemaTheaterId;
    private Integer numberOfColumn;
    private Integer numberOfRows;
    private Integer regularSeatRow;
    private Integer vipSeatRow;
    private Integer doubleSeatRow;
    private CinemaTheaterStatus status;
    List<SeatEntity> seats;

}
