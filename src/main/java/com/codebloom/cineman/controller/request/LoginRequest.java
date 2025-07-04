package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginRequest {
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email incorrect format !")
    @Size(max = 150, message = "Email is must be less than or equal 150 character !")
    private String email;

    @Size(max = 100, message = "Password is must be less than or equal 100 character !")
    private String password;
}
