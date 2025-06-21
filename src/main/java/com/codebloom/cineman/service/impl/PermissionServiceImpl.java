package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.PermissionRequest;
import com.codebloom.cineman.controller.response.PermissionResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.PermissionEntity;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
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
}
