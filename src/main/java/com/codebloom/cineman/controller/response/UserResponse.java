package com.codebloom.cineman.controller.response;

import com.codebloom.cineman.model.RoleEntity;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class UserResponse implements Serializable {
    private Long userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Date dateOfBirth;
    private String gender;
    private Integer savePoint;
    private String facebookId;
    private String googleId;
    private Date createdAt;
    private Date updatedAt;
    private String status;
    private String avatar;
    private List<RoleEntity> roles;
}
