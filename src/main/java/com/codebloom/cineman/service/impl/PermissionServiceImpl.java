package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.Method;
import com.codebloom.cineman.model.PermissionEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j(topic = "PERMISSION-SERVICE")
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    // thư viện tiện ích jup so sánh URL -> /user/api/** match /user/api/123/abc
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // cache các permission để tránh gọi DB quá nhiều lần
    private final Map<Long, List<PermissionEntity>> userPermissionCache = new ConcurrentHashMap<>();


    @Override
    public boolean hasPermission(Long userId, Method method, String requestUrl) {
        List<PermissionEntity> permissions = userPermissionCache.computeIfAbsent(userId,
                id -> permissionRepository.findAllByUserId(userId));
        log.info("Permissions for User ID {} are {}", userId, permissions);
        log.info("requestUrl : {} , method : {}  ",method, requestUrl);

        return permissions.stream().anyMatch(p ->
                p.getMethod() == method &&
                        pathMatcher.match(p.getUrl(), requestUrl)
        );
    }

}
