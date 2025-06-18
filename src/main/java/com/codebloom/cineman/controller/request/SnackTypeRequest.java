package com.codebloom.cineman.controller.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Tên loại snack không được để trống.")
    private String name;

    @NotBlank(message = "Mô tả loại snack không được để trống.")
    private String description;

    @NotNull(message = "Trạng thái hoạt động không được để trống.")
    private Boolean isActive;
}

