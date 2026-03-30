package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.RoleRequest;
import com.codebloom.cineman.controller.response.RoleResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.PermissionEntity;
import com.codebloom.cineman.model.RoleEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.repository.RoleRepository;
import com.codebloom.cineman.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void createRoleShouldPersistNormalizedIdAndPermissions() {
        PermissionEntity permission = new PermissionEntity();
        permission.setPermissionId(10);
        permission.setTitle("Manage Users");

        when(roleRepository.existsById("RCP_MANAGER")).thenReturn(false);
        when(roleRepository.findByNameIgnoreCase("Reception Manager")).thenReturn(Optional.empty());
        when(permissionRepository.findAllById(Set.of(10))).thenReturn(List.of(permission));
        when(roleRepository.save(any(RoleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoleResponse response = roleService.createRole(RoleRequest.builder()
                .roleId("rcp_manager")
                .name("Reception Manager")
                .permissionIds(Set.of(10))
                .build());

        ArgumentCaptor<RoleEntity> captor = ArgumentCaptor.forClass(RoleEntity.class);
        verify(roleRepository).save(captor.capture());
        assertEquals("RCP_MANAGER", captor.getValue().getRoleId());
        assertEquals("Reception Manager", captor.getValue().getName());
        assertEquals(Boolean.TRUE, captor.getValue().getStatus());
        assertEquals(Set.of(10), response.getPermissionIds());
    }

    @Test
    void createRoleShouldRejectDuplicateName() {
        when(roleRepository.existsById("RCP_MANAGER")).thenReturn(false);
        when(roleRepository.findByNameIgnoreCase("Reception Manager"))
                .thenReturn(Optional.of(RoleEntity.builder().roleId("OLD").name("Reception Manager").build()));

        assertThrows(DataExistingException.class, () -> roleService.createRole(RoleRequest.builder()
                .roleId("rcp_manager")
                .name("Reception Manager")
                .build()));
    }

    @Test
    void changeStatusShouldPersistNewStatus() {
        RoleEntity roleEntity = RoleEntity.builder()
                .roleId("RCP_MANAGER")
                .name("Reception Manager")
                .status(Boolean.TRUE)
                .permissions(new LinkedHashSet<>())
                .build();

        when(roleRepository.findById("RCP_MANAGER")).thenReturn(Optional.of(roleEntity));
        when(roleRepository.save(any(RoleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoleResponse response = roleService.changeStatus("rcp_manager", false);

        assertFalse(response.getStatus());
        assertFalse(roleEntity.getStatus());
    }

    @Test
    void deleteRoleShouldRejectWhenUsersAreAssigned() {
        RoleEntity roleEntity = RoleEntity.builder()
                .roleId("RCP_MANAGER")
                .name("Reception Manager")
                .status(Boolean.TRUE)
                .permissions(new LinkedHashSet<>())
                .build();

        when(roleRepository.findById("RCP_MANAGER")).thenReturn(Optional.of(roleEntity));
        when(userRoleRepository.existsByRole_RoleId("RCP_MANAGER")).thenReturn(true);

        assertThrows(ConflictException.class, () -> roleService.deleteRole("rcp_manager"));
        verify(roleRepository, never()).delete(any(RoleEntity.class));
    }

    @Test
    void updateRoleShouldRejectMissingPermissionIds() {
        RoleEntity roleEntity = RoleEntity.builder()
                .roleId("RCP_MANAGER")
                .name("Reception Manager")
                .status(Boolean.TRUE)
                .permissions(new LinkedHashSet<>())
                .build();

        when(roleRepository.findById("RCP_MANAGER")).thenReturn(Optional.of(roleEntity));
        when(roleRepository.findByNameIgnoreCase("Reception Manager")).thenReturn(Optional.of(roleEntity));
        when(permissionRepository.findAllById(Set.of(11))).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> roleService.updateRole("RCP_MANAGER", RoleRequest.builder()
                .roleId("RCP_MANAGER")
                .name("Reception Manager")
                .permissionIds(Set.of(11))
                .build()));
    }
}
