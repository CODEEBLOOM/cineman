package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.RolePermissionRequest;

public interface RolePermissionService {

    void addPermissionToRole(RolePermissionRequest rolePermissionRequest);
    void removePermissionFromRole(RolePermissionRequest rolePermissionRequest);
}
