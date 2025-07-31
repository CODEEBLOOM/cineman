package com.codebloom.cineman.controller.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionResponse {
    private String roleId;
    private String roleName;
    private Integer permissionId;
    private String permissionName;
}
