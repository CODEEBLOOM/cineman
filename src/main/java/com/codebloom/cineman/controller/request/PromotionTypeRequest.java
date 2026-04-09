package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PromotionTypeRequest {

    @NotNull(message = "Ma loai khuyen mai khong duoc phep null !")
    @NotBlank(message = "Ma loai khuyen mai khong duoc de trong !")
    @Size(min = 1, max = 50, message = "Ma loai khuyen mai toi da 50 ky tu !")
    private String code;

    @NotNull(message = "Ten loai khuyen mai khong duoc phep null !")
    @NotBlank(message = "Ten loai khuyen mai khong duoc de trong !")
    @Size(min = 1, max = 100, message = "Ten loai khuyen mai toi da 100 ky tu !")
    private String name;

    @Size(max = 500, message = "Mo ta loai khuyen mai toi da 500 ky tu !")
    private String description;
}
