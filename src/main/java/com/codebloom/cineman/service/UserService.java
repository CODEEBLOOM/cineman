package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.controller.request.*;
import com.codebloom.cineman.controller.response.UserPaginationResponse;
import com.codebloom.cineman.controller.response.UserResponse;
import com.codebloom.cineman.model.UserEntity;


import java.util.List;

public interface UserService {

    List<UserEntity> findAll();
    UserPaginationResponse findAll(PageQueryRequest pageRequest);
    UserResponse findById(Long userId);
    UserEntity findByEmail(String email);
    UserResponse findByUsername(String username);
    Long save(UserCreationRequest user);
    void update(UserUpdateRequest user);
    void changePassword(ChangePasswordRequest changePasswordRequest);
    void delete(Long userId);
    
    // Auth //
    UserEntity register(UserRegisterRequest user);
    UserEntity getUserFromToken(String token, TokenType tokenType);
    void updateRefreshToken(String refreshToken);
}
