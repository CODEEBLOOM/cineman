package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.GenderUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserRegisterRequest {

    @NotBlank(message = "User's fullName is is not blank !")
    @Size(max = 100, message = "FullName is must be less than or equal 100 character !")
    @NotNull(message = "FullName's user is must not null !")
    private String fullName;

    @NotBlank(message = "Email is must not empty !")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email incorrect format !")
    @Size(max = 150, message = "Email is must be less than or equal 100 character !")
    private String email;

    @NotBlank(message = "Password is must not empty !")
    @Size(max = 100, message = "Password is must be less than or equal 100 character !")
    private String password;

    @NotBlank(message = "ConfirmPassword is must not empty !")
    @Size(max = 100, message = "Password is must be less than or equal 100 character !")
    private String confirmPassword;

    @NotNull(message = "User's birth day is is not null !")
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @NotNull(message = "Gender's user is is not null !")
    private GenderUser gender;

    @NotBlank(message = "PhoneNumber is not blank !")
    @NotNull(message = "PhoneNumber is not null")
    private String phoneNumber;

    private String address;

    private Integer facebookId = 0;
    private Integer googleId = 0;

}
