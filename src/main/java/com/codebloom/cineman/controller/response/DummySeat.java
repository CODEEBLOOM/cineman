package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.model.SeatTypeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DummySeat {

    private Integer columnIndex;
    private Integer rowIndex;
    private String label;
    private String seatType;

}
