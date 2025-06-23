package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.UserType;
import com.codebloom.cineman.model.RoleEntity;

import java.util.List;

public interface RoleService {

    RoleEntity create(UserType role);
    RoleEntity update(UserType role, String name);
    void delete(UserType id);
    void delete(String roleName);
    RoleEntity findById(UserType id);
    RoleEntity findByName(String name);
    List<RoleEntity> findAll();

}
