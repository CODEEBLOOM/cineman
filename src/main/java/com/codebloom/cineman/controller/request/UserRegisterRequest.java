package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    @NotBlank(message = "Email is must not empty !")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email incorrect format !")
    @Size(max = 150, message = "email is must be less than or equal 100 character !")
    private String email;

    @NotBlank(message = "Password is must not empty !")
    @Size(max = 100, message = "Password is must be less than or equal 100 character !")
    private String password;

    @NotBlank(message = "ConfirmPassword is must not empty !")
    @Size(max = 100, message = "Password is must be less than or equal 100 character !")
    private String confirmPassword;

}
