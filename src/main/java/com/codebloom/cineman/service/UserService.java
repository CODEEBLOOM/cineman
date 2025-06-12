package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.ChangePasswordRequest;
import com.codebloom.cineman.controller.request.UserCreationRequest;
import com.codebloom.cineman.controller.request.UserUpdateRequest;
import com.codebloom.cineman.controller.response.UserResponse;
import com.codebloom.cineman.model.UserEntity;

import java.util.List;

public interface UserService {

    List<UserEntity> findAll();
    UserResponse findById(Long userId);
    UserResponse findByEmail(String email);
    UserResponse findByPhoneNumber(String phoneNumber);
    Long save(UserCreationRequest user);
    void update(UserUpdateRequest user);
    void changePassword(ChangePasswordRequest changePasswordRequest);
    void delete(Long userId);

}
