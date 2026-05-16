package com.codebloom.cineman.controller.response;

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
public class DummySeat {

    private Integer columnIndex;
    private Integer rowIndex;
    private String label;
    private String seatType;

}
