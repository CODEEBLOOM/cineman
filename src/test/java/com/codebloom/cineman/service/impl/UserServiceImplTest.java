package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.GenderUser;
import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.request.UserCreationRequest;
import com.codebloom.cineman.controller.response.UserPaginationResponse;
import com.codebloom.cineman.controller.response.UserResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.exception.InvalidDataException;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.model.RoleEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.model.UserPointHistoryEntity;
import com.codebloom.cineman.model.UserRoleEntity;
import com.codebloom.cineman.repository.MembershipRankRepository;
import com.codebloom.cineman.repository.RoleRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.repository.UserRoleRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.RoleService;
import com.codebloom.cineman.service.util.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RoleService roleService;

    @Mock
    private MembershipRankRepository membershipRankRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findAllPageShouldReturnOnlyActiveUsers() {
        UserEntity activeUser = buildUser(1L, "active@cineman.test", UserStatus.ACTIVE);
        PageRequest request = new PageRequest();
        request.setPage(0);
        request.setSize(10);

        when(userRepository.findAllByStatus(UserStatus.ACTIVE, org.springframework.data.domain.PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(activeUser)));

        UserPaginationResponse response = userService.findAll(request);

        assertEquals(1, response.getUserResponses().size());
        assertEquals("active@cineman.test", response.getUserResponses().getFirst().getEmail());
        assertEquals(1, response.getMeta().getTotalElements());
    }

    @Test
    void findByIdShouldIgnoreSoftDeletedUser() {
        when(userRepository.findByUserIdAndStatus(10L, UserStatus.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userService.findById(10L));
    }

    @Test
    void deleteShouldSoftDeleteUserAndKeepRelations() {
        UserEntity user = buildUser(15L, "member@cineman.test", UserStatus.ACTIVE);
        List<InvoiceEntity> customerInvoices = new ArrayList<>(List.of(new InvoiceEntity()));
        List<UserPointHistoryEntity> pointHistories = new ArrayList<>(List.of(new UserPointHistoryEntity()));
        LinkedHashSet<UserRoleEntity> userRoles = new LinkedHashSet<>();
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRole(RoleEntity.builder().roleId("USER").name("Customer").build());
        userRole.setUser(user);
        userRoles.add(userRole);

        user.setRefreshToken("refresh-token");
        user.setCustomerInvoices(customerInvoices);
        user.setUserPointHistories(pointHistories);
        user.setUserRoles(userRoles);

        when(userRepository.findByUserIdAndStatus(15L, UserStatus.ACTIVE)).thenReturn(Optional.of(user));

        userService.delete(15L);

        assertEquals(UserStatus.INACTIVE, user.getStatus());
        assertNull(user.getRefreshToken());
        assertSame(customerInvoices, user.getCustomerInvoices());
        assertSame(pointHistories, user.getUserPointHistories());
        assertSame(userRoles, user.getUserRoles());
        verify(userRepository).save(user);
        verifyNoInteractions(userRoleRepository);
    }

    @Test
    void deleteShouldRejectAlreadySoftDeletedUser() {
        when(userRepository.findByUserIdAndStatus(20L, UserStatus.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userService.delete(20L));
    }

    @Test
    void getUserFromAccessTokenShouldRejectInactiveUser() {
        when(jwtService.isTokenExpired("token", TokenType.ACCESS_TOKEN)).thenReturn(false);
        when(jwtService.extractUsername("token", TokenType.ACCESS_TOKEN)).thenReturn("inactive@cineman.test");
        when(userRepository.findByEmailAndStatus("inactive@cineman.test", UserStatus.ACTIVE)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userService.getUserFromToken("token", TokenType.ACCESS_TOKEN));
    }

    @Test
    void findAllShouldConvertActiveUsers() {
        UserEntity activeUser = buildUser(2L, "viewer@cineman.test", UserStatus.ACTIVE);
        when(userRepository.findAllByStatus(UserStatus.ACTIVE)).thenReturn(List.of(activeUser));

        List<UserResponse> response = userService.findAll();

        assertEquals(1, response.size());
        assertEquals("ACTIVE", response.getFirst().getStatus());
        assertNotNull(response.getFirst().getRoles());
    }

    @Test
    void saveShouldRequireRoleIds() {
        UserCreationRequest request = UserCreationRequest.builder()
                .email("admin@cineman.test")
                .password("secret")
                .fullName("Admin User")
                .phoneNumber("0123456789")
                .address("Ho Chi Minh")
                .gender(GenderUser.MALE)
                .build();

        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");

        assertThrows(InvalidDataException.class, () -> userService.save(request));
        verifyNoInteractions(userRepository, roleRepository, userRoleRepository);
    }

    private UserEntity buildUser(Long id, String email, UserStatus status) {
        UserEntity user = UserEntity.builder()
                .userId(id)
                .email(email)
                .password("encoded-password")
                .fullName("Cinema User")
                .phoneNumber("0123456789")
                .address("Ho Chi Minh")
                .gender(GenderUser.MALE)
                .savePoint(0)
                .status(status)
                .build();
        user.setUserRoles(new LinkedHashSet<>());
        return user;
    }
}
