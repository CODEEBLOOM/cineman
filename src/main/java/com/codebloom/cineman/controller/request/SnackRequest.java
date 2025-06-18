package com.codebloom.cineman.controller.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SnackRequest {

    @NotBlank(message = "Tên snack không được để trống.")
    private String snackName;

    @NotNull(message = "Đơn giá không được để trống.")
    private Double unitPrice;

    @NotBlank(message = "Hình ảnh snack không được để trống.")
    private String image;

    @NotBlank(message = "Mô tả snack không được để trống.")
    private String description;

    @NotNull(message = "Loại snack không được để trống.")
    private Integer snackTypeId;

    @NotNull(message = "Trạng thái hoạt động không được để trống.")
    private Boolean isActive;
}


