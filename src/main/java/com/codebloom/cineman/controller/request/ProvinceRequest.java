package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProvinceRequest {

    @NotNull(message = "Code's province is must not null !")
    @Min(value = 0, message = "Code's province must be greater than or equal 1 !")
    private Integer code;

    @NotBlank(message = "Name's province is not blank !")
    @Size(max = 150, message = "Name's province greater less than or equal 150 character !")
    private String name;

}
