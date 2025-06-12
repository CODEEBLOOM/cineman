package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.request.RefreshTokenCreationRequest;
import com.codebloom.cineman.controller.request.RefreshTokenUpdateRequest;
import com.codebloom.cineman.model.RefreshTokenEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.TokenRepositoty;
import com.codebloom.cineman.repository.UserRepository;
import com.codebloom.cineman.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j(topic = "REFRESH-TOKEN-SERVICE")
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements TokenService {

    private final TokenRepositoty tokenRepositoty;


    private RefreshTokenEntity buildRefreshTokenEntity(RefreshTokenEntity request) {
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setToken(request.getToken());
        token.setUser(request.getUser());
        token.setRevoked(false);
        token.setCreatedAt(new Date());
        token.setExpiryDate(request.getExpiryDate());
        token.setDeviceInfo(request.getDeviceInfo());
        token.setCreatedByIp(request.getCreatedByIp());
        return token;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(RefreshTokenEntity request) {
        Optional<RefreshTokenEntity> optionalToken = tokenRepositoty.findByUser(request.getUser());

        if (optionalToken.isPresent()) {
            RefreshTokenEntity token = optionalToken.get();
            token.setToken(request.getToken());
            token.setRevoked(false);
            token.setCreatedAt(new Date());
            token.setExpiryDate(request.getExpiryDate());
            token.setDeviceInfo(request.getDeviceInfo());
            token.setCreatedByIp(request.getCreatedByIp());
            tokenRepositoty.save(token);
        } else {
            tokenRepositoty.save(request);
        }
    }



}
