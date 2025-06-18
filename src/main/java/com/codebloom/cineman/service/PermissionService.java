package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.Method;
import com.codebloom.cineman.model.PermissionEntity;

import java.util.List;

public interface PermissionService {
    boolean hasPermission(Long userId, Method method, String requesturl);
}
