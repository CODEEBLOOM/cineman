package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.request.RolePermissionRequest;
import com.codebloom.cineman.controller.response.RolePermisisonPageableResponse;
import com.codebloom.cineman.controller.response.RolePermissionResponse;

import java.util.List;

public interface RolePermissionService {

    void addPermissionToRole(RolePermissionRequest rolePermissionRequest);
    void removePermissionFromRole(RolePermissionRequest rolePermissionRequest);

    RolePermisisonPageableResponse findAllByPage(PageRequest pageRequest);
    List<RolePermissionResponse> findAllRolePermissions();
}