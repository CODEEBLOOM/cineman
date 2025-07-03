package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.SeatStatus;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.SeatTypeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SeatResponse {

    private Long id;
    private Integer rowIndex;
    private Integer columnIndex;
    private String label;
    private SeatTypeEntity seatType;
    private SeatStatus status;

}
