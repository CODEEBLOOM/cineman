package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.RolePermissionRequest;
import com.codebloom.cineman.controller.response.RolePermissionResponse;

import java.util.List;

public interface RolePermissionService {

    void addPermissionToRole(RolePermissionRequest rolePermissionRequest);
    void removePermissionFromRole(RolePermissionRequest rolePermissionRequest);

    List<RolePermissionResponse> findAllRolePermissions();
}
