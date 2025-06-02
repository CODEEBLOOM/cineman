package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.GenderUser;
import com.codebloom.cineman.common.enums.UserType;
import lombok.Getter;

import java.util.Date;

@Getter
public class UserCreationRequest {

    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Date dateOfBirth;
    private GenderUser gender;
    private UserType userType;

}
