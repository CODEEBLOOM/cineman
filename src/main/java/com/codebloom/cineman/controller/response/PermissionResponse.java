package com.codebloom.cineman.controller.response;


import com.codebloom.cineman.common.enums.Method;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionResponse {
    Integer permissionId;
    String title;
    String description;
    Method method;
    String url;
    Date createdAt;
    Date updatedAt;
}
