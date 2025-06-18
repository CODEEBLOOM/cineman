package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailBookingSnackRequest {


    private Integer snackId;

    @NotNull(message = "Số lượng snack không được để trống.")
    private Integer totalSnack;
}
