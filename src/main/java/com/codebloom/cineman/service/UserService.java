package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.TokenType;
import com.codebloom.cineman.controller.request.*;
import com.codebloom.cineman.controller.response.UserPaginationResponse;
import com.codebloom.cineman.controller.response.UserResponse;
import com.codebloom.cineman.model.UserEntity;


import java.util.List;

public interface UserService {

    List<UserEntity> findAll();
    UserPaginationResponse findAll(PageRequest pageRequest);
    UserResponse findById(Long userId);
    UserResponse findByEmail(String email);
    long save(UserCreationRequest user);
    UserResponse update(UserUpdateRequest user);
    void changePassword(ChangePasswordRequest changePasswordRequest);
    void delete(Long userId);
    
    // Auth //
    long register(UserRegisterRequest user);
    UserEntity getUserFromToken(String token, TokenType tokenType);
    UserResponse getInfoUserByAccessToken(String token);
    void updateRefreshToken(String refreshToken);
    void confirmEmail(String secretCode);
}
