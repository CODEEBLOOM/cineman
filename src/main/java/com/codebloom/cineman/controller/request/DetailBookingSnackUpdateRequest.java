package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class DetailBookingSnackUpdateRequest {

    @NotNull(message = "Id của chi tiết đặt snack không được phép null !")
    @Min(value = 1 , message = "Id của chi tiết đặt snack phải lớn hơn 0 !")
    private Long id;

    @NotNull(message = "Id snack không được phép null !")
    @Min(value = 1, message = "Id snack phải lớn hơn 0 !")
    private Integer snackId;
    @NotNull(message = "Số lượng snack không được phép null !")
    @Min(value = 1, message = "Số lượng snack phải lớn hơn 0 !")
    private Integer quantity;
    @NotNull(message = "Id hóa đơn khách hàng không được phép null !")
    @Min(value = 1, message = "Id hóa đơn khách hàng phải lớn hơn 0 !")
    private Long invoiceId;

}
