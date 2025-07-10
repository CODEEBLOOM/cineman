package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.ProvinceEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MovieTheaterRequest {
    @NotBlank(message = "Name's movie theater is must not blank !")
    @Size(max = 200, message = "Name's movie theater is must be less than or equal 200 character !")
    private String name;

    @NotBlank(message = "Address's movie theater is must not blank !")
    @Size(max = 200, message = "Address's movie theater is must be less than or equal 200 character !")
    private String address;

    @NotBlank(message = "Hotline's movie theater is must not null !")
    @Size(max = 20, message = "Hotline's movie theater is must be less than or equal 20 character !")
    private String hotline;

    @NotBlank(message = "IframeCode's movie theater is must not null !")
    @Size(max = 300, message = "IframeCode's movie theater is must be less than or equal 200 character !")
    private String iframeCode;

    @NotNull(message = "Province id is must not null !")
    @Min(value = 1, message = "Id's province is must greater than 0 !")
    private Integer provinceId;

}
