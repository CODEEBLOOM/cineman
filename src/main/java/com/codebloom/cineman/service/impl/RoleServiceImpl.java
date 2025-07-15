package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.UserType;
import com.codebloom.cineman.Exception.*;
import com.codebloom.cineman.model.RoleEntity;
import com.codebloom.cineman.repository.RoleRepository;
import com.codebloom.cineman.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "ROLE-SERVICE")
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleEntity create(UserType role) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(role.toString());
        roleEntity.setName(role.getDisplayName());
        return roleRepository.save(roleEntity);
    }

    @Override
    public RoleEntity update(UserType role, String name) {
        RoleEntity existingRole = this.findById(role);
        existingRole.setName(name);
        return roleRepository.save(existingRole);
    }

    @Override
    public void delete(UserType role) {
        RoleEntity existingRole = this.findById(role);
        roleRepository.delete(existingRole);
    }

    @Override
    public void delete(String roleName) {
        RoleEntity existingRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new DataNotFoundException("Role not found with name: " + roleName));
        roleRepository.delete(existingRole);
    }

    @Override
    public RoleEntity findById(UserType role) {
        return roleRepository.findById(role.toString())
                .orElseThrow(() -> new DataNotFoundException("Role not found with id: " + role));
    }

    @Override
    public RoleEntity findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException("Role not found with name: " + name));
    }

    @Override
    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

}
