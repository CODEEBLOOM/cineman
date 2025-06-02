package com.codebloom.cineman.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserResponse implements Serializable {
    private Long userId;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String address;
//    private String dateOfBirth;
//    private String gender;
//    private Integer savePoint;
//    private Integer facebookId;
//    private Integer googleId;
//    private Date createdAt;
//    private LocalDateTime updatedAt;
//    private Boolean isActive;

}
