package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.request.UserRoleRequest;
import com.codebloom.cineman.controller.response.UserRolePageableResponse;
import com.codebloom.cineman.controller.response.UserRoleResponse;

public interface UserRoleService {
    UserRoleResponse create(UserRoleRequest request);
    UserRoleResponse update(Long id, UserRoleRequest request);
    void delete(Long id);
    UserRolePageableResponse findAllByPage(PageRequest request);
}
