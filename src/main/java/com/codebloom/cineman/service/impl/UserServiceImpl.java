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
import java.util.*;

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


    /**
     * hàm lấy toàn bộ user của hệ thống
     * @return danh sách UserResponse object
     */
    @Override
    public List<UserResponse> findAll() {
        List<UserResponse> userResponses = new ArrayList<>();
        userRepository.findAll().forEach(user -> userResponses.add(convertToUserResponse(user)));
        return userResponses;
    }


    /**
     * hàm lấy toàn bộ user của hệ thống có phân trang
     * @param pageRequest thông tin phân trang và query
     * @return UserPaginationResponse
     */
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


    /**
     * hàm tìm kiếm user theo userId
     * @param userId id của người dùng
     * @return UserResponse
     */
    @Override
    public UserResponse findById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with user id: " + userId));
        return convertToUserResponse(user);
    }

    /**
     * hàm tìm kiếm user theo email
     * @param email email của người dùng
     * @return UserResponse
     */
    @Override
    public UserResponse findByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found with email" + email));
        return convertToUserResponse(user);
    }


    /**
     * Tạo tài khoản cho nhân viên hệ thống
     * Tức là role trong rạp chiếu
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
        user.setFacebookId("");
        user.setGoogleId("");
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

    /**
     * Hàm cập nhật thông tin tài khoản
     * @param user UserUpdateRequest
     * @return UserResponse
     */
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

    /**
     * Hàm cho phép người dùng đổi mật khẩu
     * @param changePasswordRequest thông tin đầu vào
     */
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

    /**
     * hàm xóa mềm user trong hệ thống
     * @param userId id của tài khoản
     */
    @Override
    public void delete(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with user id: " + userId));
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    /**
     * Hàm tạo tài khoản người dùng với vài trò là USER
     * Nghĩa là khách hàng tạo tài khoản
     * @param user thông tin cần thiết để tạo tài khoản
     * @return user với trạng thải PENDING
     */
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
            if (user.getFacebookId() == null && user.getGoogleId() == null) {
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
                    .facebookId(user.getFacebookId() == null ? "" : user.getFacebookId())
                    .googleId(user.getGoogleId() == null ? "" : user.getGoogleId())
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


    /**
     * Trich xuất thông tin user từ token
     * @param token token
     * @param tokenType loại token
     * @return user trích xuất được
     */
    @Override
    public UserEntity getUserFromToken(String token, TokenType tokenType) {
        if (jwtService.isTokenExpired(token, tokenType)) {
            throw new ForBiddenException("Token is expired");
        }
        String email = jwtService.extractUsername(token, tokenType);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found with user id: " + email));
    }

    /**
     * get thông tin user bằng access token
     * @param token access token
     * @return UserResponse
     */
    @Override
    public UserResponse getInfoUserByAccessToken(String token) {
        UserEntity user = this.getUserFromToken(token, TokenType.ACCESS_TOKEN);
        return this.convertToUserResponse(user);
    }

    /**
     * Cập nhật refresh token của người dùng
     * @param token token
     * @param isLogout nếu true nghĩa là refreshToken == null
     */
    @Override
    public void updateRefreshToken(String token, boolean isLogout) {
        UserEntity user;
        if(isLogout){
            user = this.getUserFromToken(token, TokenType.ACCESS_TOKEN);
            user.setRefreshToken(null);
        }else {
            user = this.getUserFromToken(token, TokenType.REFRESH_TOKEN);
            user.setRefreshToken(token);
        }
        userRepository.save(user);
    }


    /**
     * Kích hoạt tài khoản người dùng --> status = active
     * @param secretCode mã token đã gửi cho nguời dùng qua email
     */
    @Override
    public void confirmEmail(String secretCode) {
        UserEntity user = this.getUserFromToken(secretCode, TokenType.VERIFY_EMAIL);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginRequest loginSocial(UserCreationRequest userLoginDTO) {
        Optional<UserEntity> optionalUser = Optional.empty();
        RoleEntity roleUser = roleService.findById(UserType.USER);


        // Kiểm tra Google Account ID
        if (userLoginDTO.isGoogleAccountIdValid()) {
            optionalUser = userRepository.findByGoogleId(userLoginDTO.getGoogleId());

            // Tạo người dùng mới nếu không tìm thấy
            if (optionalUser.isEmpty() ) {
                checkNewUser(userLoginDTO.getEmail(), userLoginDTO.getPhoneNumber());
                String password = passwordEncoder.encode(userLoginDTO.getPassword());
                UserEntity newUser = UserEntity.builder()
                        .email(userLoginDTO.getEmail())
                        .password(password)
                        .fullName(userLoginDTO.getFullName())
                        .dateOfBirth(userLoginDTO.getDateOfBirth())
                        .gender(userLoginDTO.getGender())
                        .savePoint(0)
                        .facebookId(userLoginDTO.getFacebookId() == null ? "" : userLoginDTO.getFacebookId())
                        .googleId(userLoginDTO.getGoogleId())
                        .status(UserStatus.ACTIVE)
                        .phoneNumber(userLoginDTO.getPhoneNumber())
                        .address(userLoginDTO.getAddress())
                        .userType(UserType.USER)
                        .avatar(userLoginDTO.getAvatar())
                        .build();

                // Lưu người dùng mới
                newUser = userRepository.save(newUser);

                // Add role user for account //
                UserRoleEntity userRoleEntity = new UserRoleEntity();
                userRoleEntity.setRole(roleUser);
                userRoleEntity.setUser(newUser);
                userRoleEntity.setName(roleUser.getName());
                userRoleEntity.setDescription("Tài khoản dành cho khách hàng đăng kí tại nhà ( online )");
                userRoleRepository.save(userRoleEntity);

                // GÁN NGƯỢC LẠI VÀO user (bộ nhớ)
                Set<UserRoleEntity> userRoles = new HashSet<>();
                userRoles.add(userRoleEntity);
                newUser.setUserRoles(userRoles);
                optionalUser = Optional.of(newUser);
            }
        }

        UserEntity user = optionalUser
                .orElseThrow(() -> new DataNotFoundException("User not found with google id: " + userLoginDTO.getGoogleId()));

        // Kiểm tra nếu tài khoản bị khóa
        if (user.getStatus().equals(UserStatus.LOCKED)) {
            throw new DataNotFoundException("Tài khoản đã bị khóa!");
        }

        return LoginRequest.builder()
                .email(user.getEmail())
                .password(userLoginDTO.getPassword())
                .build();

    }


    /**
     * Hàm nội bộ để thực hiện convert 
     * @param user UserEntity
     * @return UserResponse
     */
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
                .facebookId(user.getFacebookId() == null ? "" : user.getFacebookId())
                .googleId(user.getGoogleId() == null ? "" : user.getGoogleId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .avatar(user.getAvatar())
                .build();
        List<RoleEntity> roles = new ArrayList<>();
        user.getUserRoles().forEach(userRoleEntity -> roles.add(userRoleEntity.getRole()));
        userResponse.setRoles(roles);
        userResponse.setStatus(user.getStatus().toString());
        return userResponse;
    }


    /**
     * Kiểm tra sự tồn tại của tài khoản
     * @param email email
     * @param phoneNumber phoneNumber
     */
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
