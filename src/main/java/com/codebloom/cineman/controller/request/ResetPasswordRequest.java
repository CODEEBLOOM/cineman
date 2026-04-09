package com.codebloom.cineman.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token must not be blank")
    private String token;

    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotBlank(message = "Confirm password must not be blank")
    private String confirmPassword;
}
