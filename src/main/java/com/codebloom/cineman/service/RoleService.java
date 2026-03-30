package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.UserType;
import com.codebloom.cineman.controller.request.RoleRequest;
import com.codebloom.cineman.controller.response.RoleResponse;
import com.codebloom.cineman.model.RoleEntity;

import java.util.List;

public interface RoleService {

    RoleResponse createRole(RoleRequest request);
    RoleResponse updateRole(String roleId, RoleRequest request);
    void deleteRole(String roleId);
    RoleResponse changeStatus(String roleId, boolean status);
    RoleResponse getRoleById(String roleId);
    List<RoleResponse> getAllRoles();

    RoleEntity create(UserType role);
    RoleEntity update(UserType role, String name);
    void delete(UserType id);
    RoleEntity findById(UserType id);
    RoleEntity findByName(String name);
    RoleEntity findActiveByRoleId(String roleId);
}
