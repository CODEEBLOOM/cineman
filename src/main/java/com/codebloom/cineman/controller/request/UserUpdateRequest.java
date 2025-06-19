package com.codebloom.cineman.controller.request;

import com.codebloom.cineman.common.enums.GenderUser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class UserUpdateRequest {

    private Long userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Date dateOfBirth;
    private GenderUser gender;

}
