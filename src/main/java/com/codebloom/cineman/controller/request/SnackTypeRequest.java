package com.codebloom.cineman.controller.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SnackTypeRequest {

    @NotBlank(message = "Tên loại sản phẩm snack không được để trống.")
    @Size(min = 1, max = 100, message = "Tên loại sản phẩm snack tối đa 100 kí tự !")
    @NotNull(message = "Tên sản phẩm không được phép null !")
    private String name;

    @Size(min = 1, max = 200, message = "Mô tả sản phẩm snack tối đa 200 kí tự !")
    @NotBlank(message = "Tên loại sản phẩm snack không được để trống.")
    @NotNull(message = "Mô tả sản phẩm không được phép null !")
    private String description;

}

