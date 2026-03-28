package com.codebloom.cineman.repository.projection;

import com.codebloom.cineman.common.enums.SeatStatus;
import com.codebloom.cineman.common.enums.SeatType;

public interface SeatSelectionProjection {

    Long getId();

    Integer getRowIndex();

    Integer getColumnIndex();

    String getLabel();

    SeatStatus getStatus();

    SeatType getSeatTypeId();

    String getSeatTypeName();

    Double getSeatTypePrice();

    Boolean getSeatTypeStatus();
}
