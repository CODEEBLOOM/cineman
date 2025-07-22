package com.codebloom.cineman.controller.request;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class SnackRequest {

    @NotBlank(message = "Tên snack không được để trống.")
    @Size(min = 1, max = 100, message = "Tên sản phẩm snack có độ dài nhỏ hơn hoặc bằng 100 kí tự")
    @NotNull( message = "Tên snack không được phép null !")
    private String snackName;

    @NotNull(message = "Đơn giá không được để trống.")
    @Min(value = 0, message = "Đơn giá của sản phẩm tối thiểu bằng 0 !")
    private Double unitPrice;

    @NotBlank(message = "Hình ảnh snack không được để trống !")
    @Size(min = 1, max = 100, message = "Ảnh sản phẩm snack có độ dài nhỏ hơn hoặc bằng 100 kí tự")
    @NotNull( message = "Hình ảnh snack không được phép null !")
    private String image;

    @NotBlank(message = "Mô tả snack không được để trống.")
    @Size(min = 1, max = 200, message = "Mô tả sản phẩm snack có độ dài nhỏ hơn hoặc bằng 200 kí tự")
    private String description;

    @NotNull(message = "Loại snack không được để trống.")
    @Min(value = 1, message = "Id của loại sản phẩm tối thiểu bằng 1 !")
    private Integer snackTypeId;

}


