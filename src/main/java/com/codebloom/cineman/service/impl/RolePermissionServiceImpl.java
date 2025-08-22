package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.request.RolePermissionRequest;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.controller.response.PermissionResponse;
import com.codebloom.cineman.controller.response.RolePermisisonPageableResponse;
import com.codebloom.cineman.controller.response.RolePermissionResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.PermissionEntity;
import com.codebloom.cineman.model.RoleEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.repository.RoleRepository;
import com.codebloom.cineman.service.RolePermissionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void addPermissionToRole(RolePermissionRequest request) {
        RoleEntity role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found with id: " + request.getRoleId()));

        PermissionEntity permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new DataNotFoundException("Permission not found with id: " + request.getPermissionId()));

        boolean added = role.getPermissions().add(permission);
        if (added) {
            roleRepository.save(role);
            log.info("Added permission {} to role {}", permission.getPermissionId(), role.getRoleId());
        } else {
            log.warn("Permission {} already exists in role {}", permission.getPermissionId(), role.getRoleId());
        }
    }

    @Override
    @Transactional
    public void removePermissionFromRole(RolePermissionRequest request) {
        RoleEntity role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found with id: " + request.getRoleId()));

        PermissionEntity permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new DataNotFoundException("Permission not found with id: " + request.getPermissionId()));

        boolean removed = role.getPermissions().remove(permission);
        if (removed) {
            roleRepository.save(role);
            log.info("Removed permission {} from role {}", permission.getPermissionId(), role.getRoleId());
        } else {
            log.warn("Permission {} not associated with role {}", permission.getPermissionId(), role.getRoleId());
        }
    }

    @Override
    public RolePermisisonPageableResponse findAllByPage(com.codebloom.cineman.controller.request.PageRequest request) {
        List<RolePermissionResponse> all = new ArrayList<>();
        List<RoleEntity> roles = roleRepository.findAll();

        for (RoleEntity role : roles) {
            for (PermissionEntity permission : role.getPermissions()) {
                RolePermissionResponse dto = new RolePermissionResponse();
                dto.setRoleId(role.getRoleId());
                dto.setRoleName(role.getName());
                dto.setPermissionId(permission.getPermissionId());
                dto.setPermissionName(permission.getTitle());
                all.add(dto);
            }
        }

        // Áp dụng phân trang trên List
        int page = request.getPage();
        int size = request.getSize();
        int start = page * size;
        int end = Math.min(start + size, all.size());

        List<RolePermissionResponse> pagedList = new ArrayList<>();
        if (start < all.size()) {
            pagedList = all.subList(start, end);
        }

        // Tạo meta info
        MetaResponse meta = MetaResponse.builder()
                .currentPage(page)
                .totalElements(all.size())
                .totalPages((int) Math.ceil((double) all.size() / size))
                .pageSize(size)
                .build();

        return RolePermisisonPageableResponse.builder()
                .rolePermissionResponses(pagedList)
                .meta(meta)
                .build();
    }

    @Override
    public List<RolePermissionResponse> findAllRolePermissions() {
        List<RolePermissionResponse> result = new ArrayList<>();
        List<RoleEntity> roles = roleRepository.findAll();

        for (RoleEntity role : roles) {
            for (PermissionEntity permission : role.getPermissions()) {
                RolePermissionResponse dto = new RolePermissionResponse();
                dto.setRoleId(role.getRoleId());
                dto.setRoleName(role.getName());
                dto.setPermissionId(permission.getPermissionId());
                dto.setPermissionName(permission.getTitle());
                result.add(dto);
            }
        }

        return result;
    }
}