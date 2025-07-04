package com.codebloom.cineman.controller.response;


import com.codebloom.cineman.common.enums.Method;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
public class PermissionResponse {
    private Integer permissionId;
    private String title;
    private String description;
    private Method method;
    private String url;
    private Date createdAt;
    private Date updatedAt;
}
