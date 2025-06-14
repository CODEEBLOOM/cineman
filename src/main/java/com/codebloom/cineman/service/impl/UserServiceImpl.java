package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.exception.ForBiddenException;
import com.codebloom.cineman.service.jwt.JwtServiceImpl;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.common.enums.UserType;
import com.codebloom.cineman.controller.request.*;
import com.codebloom.cineman.controller.response.UserPaginationResponse;
import com.codebloom.cineman.controller.response.UserResponse;
import com.codebloom.cineman.exception.ConfirmPasswordException;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
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
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    private final JwtServiceImpl jwtService;


    @Override
    public List<UserEntity> findAll() {
        return List.of();
    }

    @Override
    public UserPaginationResponse findAll(PageQueryRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<UserEntity> list = userRepository.findAll(pageable);
        List<UserResponse> userResponses = new ArrayList<>();
        list.forEach(userEntity -> {
            UserResponse userResponse = convertToUserResponse(userEntity);
            userResponses.add(userResponse);
        });
        
        // Set các giá trị pageable \\\\\\\\
        UserPaginationResponse.Meta meta = new UserPaginationResponse.Meta();
        meta.setCurrentPage(list.getNumber() + 1);
        meta.setTotalElements(list.getNumberOfElements());
        meta.setTotalPages(list.getTotalPages());
        meta.setPageSize(list.getTotalPages());

        return UserPaginationResponse.builder()
                .userResponses(userResponses)
                .meta(meta)
                .build();
    }

    @Override
    public UserResponse findById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with user id: " + userId));
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getUserId());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setFullName(user.getFullName());
        userResponse.setAddress(user.getAddress());
        return userResponse;
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found with email"+email));
    }

    @Override
    public UserResponse findByUsername(String username) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(UserCreationRequest request) {
        log.info("Saving user {}", request);
        Date now = new Date();
        UserEntity user = modelMapper.map(request, UserEntity.class);
        user.setSavePoint(0);
        user.setStatus(UserStatus.PENDING);
        user.setFacebookId(0);
        user.setGoogleId(0);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        checkNewUser(user); // Nếu có sự trùng lặp dữ liệu thì đã throw rồi
        user = userRepository.save(user);

        // Add role for account //
        RoleEntity userRole = roleRepository.findById(UserType.USER.toString())
                .orElseThrow(() -> new DataNotFoundException("Not found user role with role :"+UserType.USER));

        // User Role //
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setRole(userRole);
        userRoleEntity.setUser(user);
        userRoleEntity.setName(userRole.getName());
        userRoleEntity.setDescription("Tài khoản dành cho khách hàng đăng kí tại nhà ( online )");

        userRoleRepository.save(userRoleEntity);
        log.info("Saved user {}", user);

        // Send email //
        try {
            emailService.emailVerification(request.getEmail(), request.getFullName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user.getUserId();
    }

    @Override
    public void update(UserUpdateRequest user) {

    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {

    }

    @Override
    public void delete(Long userId) {

    }

    @Override
    public UserEntity register(UserRegisterRequest user) {
        if(!user.getPassword().equals(user.getConfirmPassword())) {
            throw new ConfirmPasswordException("Password not match confirm password");
        }

        UserEntity registerUser = this.findByEmail(user.getEmail());
        if(registerUser.getFacebookId() == 0 &&  registerUser.getGoogleId() == 0) {
            String passwordEncode = passwordEncoder.encode(user.getPassword());
            registerUser.setPassword(passwordEncode);
        }

        registerUser.setStatus(UserStatus.ACTIVE);
        return userRepository.save(registerUser);
    }

    @Override
    public UserEntity getUserFromToken(String token, TokenType tokenType) {
        if(jwtService.isTokenExpired(token, tokenType)){
            throw new ForBiddenException("Token is expired");
        }
        String email = jwtService.extractUsername(token, tokenType);
        return this.findByEmail(email);
    }

    @Override
    public void updateRefreshToken(String refreshToken) {
        UserEntity user = this.getUserFromToken(refreshToken, TokenType.REFRESH_TOKEN);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    private UserResponse convertToUserResponse(UserEntity user) {
        return modelMapper.map(user, UserResponse.class);
    }


    private void checkNewUser(UserEntity user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existingUser -> {throw new DataExistingException("Email already exists at least one user!");});
        userRepository.findByPhoneNumber(user.getPhoneNumber())
                .ifPresent(existingUser -> {throw new DataExistingException("Phone Number already exists at least one user!");});
    }
}
