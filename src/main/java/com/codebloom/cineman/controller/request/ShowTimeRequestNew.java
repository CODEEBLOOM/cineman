package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.ShowTimeStatus;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ShowTimeRequestNew {

    @Min(value = 1, message = "Id's movie theater is must be greater than 0")
    private Long movieTheaterId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date showDate;

    private ShowTimeStatus showTimeStatus;

    private Boolean special;

}
