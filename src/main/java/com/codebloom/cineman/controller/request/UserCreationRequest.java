package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.GenderUser;
import com.codebloom.cineman.common.enums.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class UserCreationRequest {

    @NotBlank(message = "User's email is is not blank !")
    @NotNull( message = "User's email is is not null !")
    private String email;

    @NotBlank(message = "User's fullName is is not blank !")
    @NotNull(message = "User's fullName is is not null !")
    private String fullName;

    @NotNull(message = "User's password is is not null !")
    @Size(max = 100, message = "Password is must be less than or equal 100 character !")
    private String password;

    @Pattern(regexp = "^0[0-9]{9,10}$", message = "User's phone number invalid !")
    @NotBlank(message = "User's phone number is is not blank !")
    @NotNull(message = "User's phone number is is not null !")
    private String phoneNumber;

    @NotBlank(message = "User's address is is not blank !")
    private String address;

    @NotNull(message = "User's birth day is is not null !")
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @NotNull(message = "User's gender is is not null !")
    private GenderUser gender;

    private String avatar;
    private String facebookId;
    private String googleId ;

    @Builder.Default
    private UserType userType = UserType.USER;

    public boolean isGoogleAccountIdValid() {
        return googleId != null && !googleId.isEmpty();
    }

}
