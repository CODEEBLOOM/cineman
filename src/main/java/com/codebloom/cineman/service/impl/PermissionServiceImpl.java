package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.PermissionRequest;
import com.codebloom.cineman.controller.response.PermissionResponse;
import com.codebloom.cineman.exception.*;
import com.codebloom.cineman.common.enums.Method;
import com.codebloom.cineman.model.PermissionEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;





@Service
@Slf4j(topic = "PERMISSION-SERVICE")
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    // Repository để thao tác với bảng permissions
    private final PermissionRepository permissionRepository;

    // ModelMapper để map giữa DTO và entity
    private final ModelMapper mapper;

    /**
     * Tạo một permission mới từ request
     * @param request PermissionRequest
     * @return PermissionResponse đã lưu
     */
    @Transactional
    @Override
    public PermissionResponse create(PermissionRequest request) {
        // Chuyển đổi request sang entity
        PermissionEntity permission = mapper.map(request, PermissionEntity.class);
        permission.setCreatedAt(new Date());
        permission.setUpdatedAt(new Date());

        // Lưu vào database
        PermissionEntity saved = permissionRepository.save(permission);

        // Trả về DTO
        return mapper.map(saved, PermissionResponse.class);
    }

    /**
     * Cập nhật permission theo ID
     * @param id ID của permission
     * @param request dữ liệu cập nhật
     * @return PermissionResponse sau khi cập nhật
     */
    @Override
    public PermissionResponse update(Integer id, PermissionRequest request) {
        // Tìm permission hiện tại
        PermissionEntity permission = permissionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Permission not found"));

        // Cập nhật thông tin
        mapper.map(request, permission);
        permission.setUpdatedAt(new Date());

        // Lưu lại
        PermissionEntity updated = permissionRepository.save(permission);
        return mapper.map(updated, PermissionResponse.class);
    }

    /**
     * Xóa permission theo ID
     * @param id ID của permission cần xóa
     */
    @Override
    public void delete(Integer id) {
        // Tìm và xóa
        PermissionEntity permission = permissionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Permission not found"));
        permissionRepository.delete(permission);
    }

    /**
     * Lấy permission theo ID
     * @param id ID
     * @return PermissionResponse tương ứng
     */
    @Override
    public PermissionResponse getById(Integer id) {
        PermissionEntity permission = permissionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Permission not found"));
        return mapper.map(permission, PermissionResponse.class);
    }

    /**
     * Lấy danh sách tất cả permissions
     * @return List<PermissionResponse>
     */
    @Override
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll()
                .stream()
                .map(p -> mapper.map(p, PermissionResponse.class))
                .toList();
    }



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
