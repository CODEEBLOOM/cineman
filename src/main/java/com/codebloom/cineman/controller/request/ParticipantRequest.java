package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.GenderUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantRequest {

    @NotBlank(message = "BirthName's participant is not blank !")
    @Size(max = 100, message = "BirthName's participant is must be less than or equal 100 character !")
    @NotNull(message = "BirthName's participant is not null !")
    private String birthName;

    @Size(max = 100, message = "Nickname's participant is must be less than or equal 100 character !")
    private String nickname;

    @NotNull(message = "Gender's participant's is not null!")
    private GenderUser gender;

    @NotBlank(message = "Nationality's participant is not blank !")
    @Size(max = 100, message = "Nationality's participant is must be less than or equal 100 character !")
    @NotNull(message = "Nationality's participant is not null !")
    private String nationality;

    @NotBlank(message = "Biography's participant is not blank !")
    @Size(max = 500, message = "Biography's participant is must be less than or equal 500 character !")
    private String miniBio;

    @NotBlank(message = "Avatar's participant is not blank !")
    @NotNull(message = "Avatar's participant is not null !")
    private String avatar;

}
