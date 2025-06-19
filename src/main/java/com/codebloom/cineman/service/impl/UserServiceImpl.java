package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.exception.*;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.RoleService;

import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.common.enums.UserType;
import com.codebloom.cineman.controller.request.*;
import com.codebloom.cineman.controller.response.UserPaginationResponse;
import com.codebloom.cineman.controller.response.UserResponse;
import com.codebloom.cineman.model.RoleEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.model.UserRoleEntity;
import com.codebloom.cineman.repository.RoleRepository;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.repository.UserRoleRepository;
import com.codebloom.cineman.service.UserService;
import com.codebloom.cineman.service.util.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final UserRoleRepository userRoleRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleService roleService;


    @Override
    public List<UserEntity> findAll() {
        return List.of();
    }

    @Override
    public UserPaginationResponse findAll(PageRequest pageRequest) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<UserEntity> list = userRepository.findAll(pageable);
        List<UserResponse> userResponses = new ArrayList<>();
        list.forEach(userEntity -> {
            UserResponse userResponse = convertToUserResponse(userEntity);
            userResponses.add(userResponse);
        });

        // Set các giá trị pageable \\\\\\\\
        MetaResponse meta = MetaResponse.builder()
                .currentPage(list.getNumber())
                .totalElements((int) list.getTotalElements())
                .totalPages(list.getTotalPages())
                .pageSize(list.getSize())
                .build();

        return UserPaginationResponse.builder()
                .userResponses(userResponses)
                .meta(meta)
                .build();
    }

    @Override
    public UserResponse findById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with user id: " + userId));
        return convertToUserResponse(user);
    }

    @Override
    public UserResponse findByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found with email" + email));
        return convertToUserResponse(user);
    }


    /**
     * Tạo tài khoản cho nhân viên hệ thống
     * Tức là role trong rạp chiếu
     *
     * @param request : Thông tin user hệ thống
     * @return : UserResponse
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(UserCreationRequest request) {
        log.info("Saving user {}", request);
        UserEntity user = modelMapper.map(request, UserEntity.class);
        user.setSavePoint(0);
        user.setStatus(UserStatus.ACTIVE);
        user.setFacebookId(0);
        user.setGoogleId(0);
        checkNewUser(user.getEmail(), user.getPhoneNumber());
        user = userRepository.save(user);

        // Add role for account //
        RoleEntity role = roleRepository.findById(request.getUserType().toString())
                .orElseThrow(() -> new DataNotFoundException("Not found user role with role :" + request.getUserType()));

        // User Role //
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setRole(role);
        userRoleEntity.setUser(user);
        userRoleEntity.setName(role.getName());
        userRoleEntity.setDescription("");

        userRoleRepository.save(userRoleEntity);
        log.info("Saved user {}", user);
        return user.getUserId();
    }

    @Override
    public UserResponse update(UserUpdateRequest user) {
        UserEntity existingUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found with user id: " + user.getUserId()));
        userRepository.findByEmailAndPhoneNumberAndUserIdNot(user.getEmail(), user.getPhoneNumber(), existingUser.getUserId())
                .ifPresent((userEntity) -> {
                    throw new DataExistingException("User already exists");
                });
        existingUser.setEmail(user.getEmail());
        existingUser.setFullName(user.getFullName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setDateOfBirth(user.getDateOfBirth());
        existingUser.setAddress(user.getAddress());
        existingUser.setGender(user.getGender());

        return convertToUserResponse(userRepository.save(existingUser));
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        UserEntity userEntity = userRepository.findByEmail(changePasswordRequest.getEmail())
                .orElseThrow(() -> new DataNotFoundException("User not found with user id: " + changePasswordRequest.getEmail()));

        if (!changePasswordRequest.getPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new InvalidDataException("Confirm password do not match");
        }

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), userEntity.getPassword())) {
            throw new InvalidDataException("Password do not match");
        }

        String newPass = passwordEncoder.encode(changePasswordRequest.getPassword());
        userEntity.setPassword(newPass);
        userRepository.save(userEntity);
    }

    @Override
    public void delete(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with user id: " + userId));
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long register(UserRegisterRequest user) {

        UserEntity userEntity ;
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new ConfirmPasswordException("Password not match confirm password");
        }

        /* Kiem tra neu email da ton tai va email trang thai dang laf pending thi cập nhat thooi*/
        Optional<UserEntity> existingUser = userRepository.findByEmailAndStatus(user.getEmail(), UserStatus.PENDING);
        if (existingUser.isPresent()) {
            userEntity = existingUser.get();
            userEntity.setFullName(user.getFullName());
            String password = passwordEncoder.encode(user.getPassword());
            userEntity.setPassword(password);
            userEntity.setAddress(user.getAddress());
            userEntity.setGender(user.getGender());
            userEntity.setPhoneNumber(user.getPhoneNumber());
            userRepository.save(userEntity);
        } else {
            checkNewUser(user.getEmail(), user.getPhoneNumber());
            if (user.getFacebookId() == 0 && user.getGoogleId() == 0) {
                String passwordEncode = passwordEncoder.encode(user.getPassword());
                user.setPassword(passwordEncode);
            }

            userEntity = UserEntity.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .fullName(user.getFullName())
                    .dateOfBirth(user.getDateOfBirth())
                    .gender(user.getGender())
                    .savePoint(0)
                    .facebookId(user.getFacebookId())
                    .googleId(user.getGoogleId())
                    .status(UserStatus.PENDING)
                    .phoneNumber(user.getPhoneNumber())
                    .address(user.getAddress())
                    .userType(UserType.USER)
                    .build();
            userEntity = userRepository.save(userEntity);
            RoleEntity userRole = roleService.findById(UserType.USER);

            // Add role user for account //
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setRole(userRole);
            userRoleEntity.setUser(userEntity);
            userRoleEntity.setName(userRole.getName());
            userRoleEntity.setDescription("Tài khoản dành cho khách hàng đăng kí tại nhà ( online )");
            userRoleRepository.save(userRoleEntity);
        }
        // Send email verification//
        try {
            emailService.emailVerification(user.getEmail(), user.getPhoneNumber(), user.getFullName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return userEntity.getUserId();
    }

    @Override
    public UserEntity getUserFromToken(String token, TokenType tokenType) {
        if (jwtService.isTokenExpired(token, tokenType)) {
            throw new ForBiddenException("Token is expired");
        }
        String email = jwtService.extractUsername(token, tokenType);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found with user id: " + email));
    }

    @Override
    public UserResponse getInfoUserByAccessToken(String token) {
        UserEntity user = this.getUserFromToken(token, TokenType.ACCESS_TOKEN);
        return this.convertToUserResponse(user);
    }

    @Override
    public void updateRefreshToken(String refreshToken) {
        UserEntity user = this.getUserFromToken(refreshToken, TokenType.REFRESH_TOKEN);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    @Override
    public void confirmEmail(String secretCode) {
        UserEntity user = this.getUserFromToken(secretCode, TokenType.VERIFY_EMAIL);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    private UserResponse convertToUserResponse(UserEntity user) {
        UserResponse userResponse = UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender().name())
                .savePoint(user.getSavePoint())
                .facebookId(user.getFacebookId())
                .googleId(user.getGoogleId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        List<RoleEntity> roles = new ArrayList<>();
        user.getUserRoles().forEach(userRoleEntity -> {
            roles.add(userRoleEntity.getRole());
        });
        userResponse.setRoles(roles);
        userResponse.setStatus(user.getStatus().toString());
        return userResponse;
    }


    private void checkNewUser(String email, String phoneNumber) {
        userRepository.findByEmail(email)
                .ifPresent(existingUser -> {
                    throw new DataExistingException("Email already exists at least one user!");
                });
        userRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(existingUser -> {
                    throw new DataExistingException("Phone Number already exists at least one user!");
                });
    }
}
