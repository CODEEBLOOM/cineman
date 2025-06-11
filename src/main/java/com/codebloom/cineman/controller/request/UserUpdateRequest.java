package com.codebloom.cineman.controller.request;

import lombok.Getter;

import java.util.Date;

@Getter
public class    UserUpdateRequest {

    private Long userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Date dateOfBirth;
    private Boolean gender;

}
