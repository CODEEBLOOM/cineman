package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.PermissionRequest;
import com.codebloom.cineman.controller.response.PermissionResponse;
import com.codebloom.cineman.common.enums.Method;
import com.codebloom.cineman.model.PermissionEntity;
import java.util.List;

public interface PermissionService {
    PermissionResponse create(PermissionRequest request);
    PermissionResponse update(Integer id, PermissionRequest request);
    void delete(Integer id);
    PermissionResponse getById(Integer id);
    List<PermissionResponse> getAll();
    boolean hasPermission(Long userId, Method method, String requesturl);
}
