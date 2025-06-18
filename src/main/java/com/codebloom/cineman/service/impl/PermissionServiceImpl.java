package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.Method;
import com.codebloom.cineman.model.PermissionEntity;
import com.codebloom.cineman.service.PermissionService;

import java.util.List;

public class PermissionServiceImpl implements PermissionService {
    @Override
    public boolean hasPermission(String roleId, Method method, String url) {
        return false;
    }

    @Override
    public List<PermissionEntity> getPermissionsByRole(String roleId) {
        return List.of();
    }
}
