package com.codebloom.cineman.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionRequest {
    private String roleId;
    private Integer permissionId;
}
