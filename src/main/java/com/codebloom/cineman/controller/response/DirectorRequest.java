package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.common.enums.GenderUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DirectorRequest {

    @NotBlank(message = "FullName's director is not blank !")
    @Size(max = 100, message = "FullName's director is must be less than or equal 100 character !")
    private String fullname;

    @Size(max = 100, message = "Nickname's director is must be less than or equal 100 character !")
    private String nickname;

    @NotNull(message = "Gender's director's is not null!")
    private GenderUser gender;

    @NotBlank(message = "Nationality's director is not blank !")
    @Size(max = 100, message = "Nationality's director is must be less than or equal 100 character !")
    private String nationality;

    @NotBlank(message = "Biography's director is not blank !")
    @Size(max = 500, message = "Biography's director is must be less than or equal 500 character !")
    private String miniBio;

    @NotBlank(message = "Avatar's director is not blank !")
    private String avatar;

}
