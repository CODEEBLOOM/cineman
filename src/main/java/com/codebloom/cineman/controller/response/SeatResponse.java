package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.SeatStatus;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.SeatTypeEntity;
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
public class SeatResponse {

    private Long id;
    private Integer rowIndex;
    private Integer columnIndex;
    private String label;
    private SeatTypeEntity seatType;
    private SeatStatus status;

}
