package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Min;
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


    @NotNull(message = "Id snack không được chỉ điện.")
    @Min(value = 1, message = "Id snack phải lớn hơn 0 !")
    private Integer snackId;

    @NotNull(message = "Số lượng snack không được để trống.")
    @Min(value = 1, message = "Số lượng snack phải lớn hơn 0 !")
    private Integer totalSnack;
}
