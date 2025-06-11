package com.codebloom.cineman.controller.response;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class UserResponse implements Serializable {
    private Long userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String dateOfBirth;
    private String gender;
    private Integer savePoint;
    private Integer facebookId;
    private Integer googleId;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isActive;
}
