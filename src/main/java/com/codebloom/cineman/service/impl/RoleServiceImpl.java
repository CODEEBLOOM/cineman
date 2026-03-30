package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.UserType;
import com.codebloom.cineman.controller.request.RoleRequest;
import com.codebloom.cineman.controller.response.RoleResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.exception.InvalidDataException;
import com.codebloom.cineman.model.PermissionEntity;
import com.codebloom.cineman.model.RoleEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.repository.RoleRepository;
import com.codebloom.cineman.repository.UserRoleRepository;
import com.codebloom.cineman.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "ROLE-SERVICE")
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        String normalizedRoleId = normalizeRoleId(request.getRoleId());
        String normalizedName = normalizeName(request.getName());

        if (roleRepository.existsById(normalizedRoleId)) {
            throw new DataExistingException("Role already exists with id: " + normalizedRoleId);
        }

        roleRepository.findByNameIgnoreCase(normalizedName)
                .ifPresent(existingRole -> {
                    throw new DataExistingException("Role already exists with name: " + normalizedName);
                });

        RoleEntity roleEntity = RoleEntity.builder()
                .roleId(normalizedRoleId)
                .name(normalizedName)
                .status(Boolean.TRUE)
                .permissions(resolvePermissions(request.getPermissionIds()))
                .build();

        return convertToRoleResponse(roleRepository.save(roleEntity));
    }

    @Override
    @Transactional
    public RoleResponse updateRole(String roleId, RoleRequest request) {
        RoleEntity existingRole = requireRole(roleId);
        String normalizedBodyRoleId = normalizeRoleId(request.getRoleId());
        if (!existingRole.getRoleId().equals(normalizedBodyRoleId)) {
            throw new InvalidDataException("Role id in path and body must match");
        }

        String normalizedName = normalizeName(request.getName());
        roleRepository.findByNameIgnoreCase(normalizedName)
                .filter(role -> !role.getRoleId().equals(existingRole.getRoleId()))
                .ifPresent(role -> {
                    throw new DataExistingException("Role already exists with name: " + normalizedName);
                });

        existingRole.setName(normalizedName);
        existingRole.setPermissions(resolvePermissions(request.getPermissionIds()));
        return convertToRoleResponse(roleRepository.save(existingRole));
    }

    @Override
    @Transactional
    public void deleteRole(String roleId) {
        RoleEntity existingRole = requireRole(roleId);
        if (userRoleRepository.existsByRole_RoleId(existingRole.getRoleId())) {
            throw new ConflictException("Cannot delete role because users are still assigned to it");
        }

        if (existingRole.getPermissions() != null) {
            existingRole.getPermissions().clear();
        }
        roleRepository.save(existingRole);
        roleRepository.delete(existingRole);
    }

    @Override
    @Transactional
    public RoleResponse changeStatus(String roleId, boolean status) {
        RoleEntity existingRole = requireRole(roleId);
        existingRole.setStatus(status);
        return convertToRoleResponse(roleRepository.save(existingRole));
    }

    @Override
    public RoleResponse getRoleById(String roleId) {
        return convertToRoleResponse(requireRole(roleId));
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAllByOrderByRoleIdAsc()
                .stream()
                .map(this::convertToRoleResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoleEntity create(UserType role) {
        String normalizedRoleId = normalizeRoleId(role.toString());
        return roleRepository.findById(normalizedRoleId)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder()
                        .roleId(normalizedRoleId)
                        .name(role.getDisplayName())
                        .status(Boolean.TRUE)
                        .permissions(new LinkedHashSet<>())
                        .build()));
    }

    @Override
    @Transactional
    public RoleEntity update(UserType role, String name) {
        RoleEntity existingRole = this.findById(role);
        String normalizedName = normalizeName(name);
        roleRepository.findByNameIgnoreCase(normalizedName)
                .filter(foundRole -> !foundRole.getRoleId().equals(existingRole.getRoleId()))
                .ifPresent(foundRole -> {
                    throw new DataExistingException("Role already exists with name: " + normalizedName);
                });
        existingRole.setName(normalizedName);
        return roleRepository.save(existingRole);
    }

    @Override
    public void delete(UserType role) {
        deleteRole(role.toString());
    }

    @Override
    public RoleEntity findById(UserType role) {
        return findActiveByRoleId(role.toString());
    }

    @Override
    public RoleEntity findByName(String name) {
        return roleRepository.findByNameIgnoreCase(normalizeName(name))
                .orElseThrow(() -> new DataNotFoundException("Role not found with name: " + name));
    }

    @Override
    public RoleEntity findActiveByRoleId(String roleId) {
        RoleEntity roleEntity = requireRole(roleId);
        if (Boolean.FALSE.equals(roleEntity.getStatus())) {
            throw new DataNotFoundException("Role not found with id: " + normalizeRoleId(roleId));
        }
        return roleEntity;
    }

    private RoleEntity requireRole(String roleId) {
        String normalizedRoleId = normalizeRoleId(roleId);
        return roleRepository.findById(normalizedRoleId)
                .orElseThrow(() -> new DataNotFoundException("Role not found with id: " + normalizedRoleId));
    }

    private Set<PermissionEntity> resolvePermissions(Set<Integer> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new LinkedHashSet<>();
        }

        Set<Integer> normalizedPermissionIds = permissionIds.stream()
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<PermissionEntity> permissions = permissionRepository.findAllById(normalizedPermissionIds);

        if (permissions.size() != normalizedPermissionIds.size()) {
            Set<Integer> foundIds = permissions.stream()
                    .map(PermissionEntity::getPermissionId)
                    .collect(Collectors.toSet());
            Set<Integer> missingIds = normalizedPermissionIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            throw new DataNotFoundException("Permissions not found with ids: " + missingIds);
        }

        return new LinkedHashSet<>(permissions);
    }

    private RoleResponse convertToRoleResponse(RoleEntity roleEntity) {
        Set<PermissionEntity> permissions = roleEntity.getPermissions() == null
                ? Collections.emptySet()
                : roleEntity.getPermissions();

        return RoleResponse.builder()
                .roleId(roleEntity.getRoleId())
                .name(roleEntity.getName())
                .status(roleEntity.getStatus() == null ? Boolean.TRUE : roleEntity.getStatus())
                .permissionIds(permissions.stream()
                        .map(PermissionEntity::getPermissionId)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .permissions(permissions.stream()
                        .map(PermissionEntity::getTitle)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .build();
    }

    private String normalizeRoleId(String roleId) {
        if (roleId == null || roleId.isBlank()) {
            throw new InvalidDataException("Role id must not be blank");
        }
        return roleId.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("Role name must not be blank");
        }
        return name.trim();
    }
}
