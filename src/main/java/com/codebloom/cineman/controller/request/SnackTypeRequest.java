package com.codebloom.cineman.controller.request;


import jakarta.validation.constraints.NotBlank;
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
    private Long id;

    @NotBlank(message = "Tên loại sản phẩm snack không được để trống.")
    @Size(min = 0, max = 100, message = "Tên loại sản phẩm snack tối đa 100 kí tự !")
    private String name;

    private String description;

}

