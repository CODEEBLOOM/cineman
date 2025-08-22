package com.codebloom.cineman.service.impl;


import com.codebloom.cineman.common.enums.TicketType;
import com.codebloom.cineman.common.enums.UserType;
import com.codebloom.cineman.controller.request.UserRoleRequest;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.controller.response.UserRolePageableResponse;
import com.codebloom.cineman.controller.response.UserRoleResponse;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.*;
import com.codebloom.cineman.service.UserRoleService;
import com.codebloom.cineman.controller.request.PageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "USER-ROLE-SERVICE")
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserRoleResponse create(UserRoleRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        RoleEntity role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        UserRoleEntity entity = new UserRoleEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setUser(user);
        entity.setRole(role);

        UserRoleEntity saved = userRoleRepository.save(entity);
        return convertToResponse(saved);
    }

    @Override
    public UserRoleResponse update(Long id, UserRoleRequest request) {
        UserRoleEntity entity = userRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserRole not found"));

        if (request.getName() != null) entity.setName(request.getName());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());

        if (request.getUserId() != null) {
            UserEntity user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            entity.setUser(user);
        }

        if (request.getRoleId() != null) {
            RoleEntity role = roleRepository.findById(String.valueOf(request.getRoleId()))
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            entity.setRole(role);
        }

        UserRoleEntity updated = userRoleRepository.save(entity);
        return convertToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        userRoleRepository.deleteById(id);
    }


    @Override
    public UserRolePageableResponse findAllByPage(PageRequest request) {
        List<UserRoleResponse> all = new ArrayList<>();
        List<UserRoleEntity> entities = userRoleRepository.findAll();

        for (UserRoleEntity entity : entities) {
            UserRoleResponse dto = convertToResponse(entity);
            all.add(dto);
        }

        int page = request.getPage();
        int size = request.getSize();
        int start = page * size;
        int end = Math.min(start + size, all.size());

        List<UserRoleResponse> pagedList = new ArrayList<>();
        if (start < all.size()) {
            pagedList = all.subList(start, end);
        }

        MetaResponse meta = MetaResponse.builder()
                .currentPage(page)
                .totalElements(all.size())
                .totalPages((int) Math.ceil((double) all.size() / size))
                .pageSize(size)
                .build();

        return UserRolePageableResponse.builder()
                .userRoleResponses(pagedList)
                .meta(meta)
                .build();
    }

    private UserRoleResponse convertToResponse(UserRoleEntity entity) {

        return UserRoleResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .userId(entity.getUser().getUserId())
                .roleId((long) UserType.valueOf(entity.getRole().getRoleId()).ordinal())
                .roleName(entity.getRole().getName())
                .build();
    }
}

