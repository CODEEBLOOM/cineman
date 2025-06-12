package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.request.ChangePasswordRequest;
import com.codebloom.cineman.controller.request.UserCreationRequest;
import com.codebloom.cineman.controller.request.UserUpdateRequest;
import com.codebloom.cineman.controller.response.UserResponse;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserEntity> findAll() {
        return List.of();
    }

    @Override
    public UserResponse findById(Long userId) {
        return null;
    }

    @Override
    public UserResponse findByEmail(String email) {
        return null;
    }

    @Override
    public UserResponse findByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(UserCreationRequest request) {
        log.info("Saving user {}", request);
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setSavePoint(0);
        user.setFacebookId(0);
        user.setGoogleId(0);
        user.setUserType(request.getUserType());
        user.setStatus(UserStatus.NONE);
        userRepository.save(user);

        log.info("Saved user {}", user);
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
}
