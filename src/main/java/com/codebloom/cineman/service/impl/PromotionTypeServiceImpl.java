package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.PromotionTypeRequest;
import com.codebloom.cineman.controller.response.PromotionTypeResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.PromotionTypeEntity;
import com.codebloom.cineman.repository.PromotionRepository;
import com.codebloom.cineman.repository.PromotionTypeRepository;
import com.codebloom.cineman.service.PromotionTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PROMOTION_TYPE_SERVICE")
public class PromotionTypeServiceImpl implements PromotionTypeService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final PromotionTypeRepository promotionTypeRepository;
    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public PromotionTypeResponse create(PromotionTypeRequest request) {
        validateUnique(null, request);

        PromotionTypeEntity entity = PromotionTypeEntity.builder()
                .code(request.getCode().trim())
                .name(request.getName().trim())
                .description(normalizeDescription(request.getDescription()))
                .status(Boolean.TRUE)
                .build();
        return toResponse(promotionTypeRepository.save(entity));
    }

    @Override
    @Transactional
    public PromotionTypeResponse update(Long id, PromotionTypeRequest request) {
        PromotionTypeEntity entity = getActiveEntity(id);
        validateUnique(id, request);

        entity.setCode(request.getCode().trim());
        entity.setName(request.getName().trim());
        entity.setDescription(normalizeDescription(request.getDescription()));
        return toResponse(promotionTypeRepository.save(entity));
    }

    @Override
    public PromotionTypeResponse findById(Long id) {
        return toResponse(getActiveEntity(id));
    }

    @Override
    public List<PromotionTypeResponse> findAll() {
        return promotionTypeRepository.findAllByStatus(Boolean.TRUE).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PromotionTypeEntity entity = getActiveEntity(id);
        if (promotionRepository.existsByPromotionType(entity)) {
            throw new ConflictException("Khong the xoa loai khuyen mai dang duoc su dung");
        }

        entity.setStatus(Boolean.FALSE);
        promotionTypeRepository.save(entity);
    }

    @Override
    public PromotionTypeEntity getActiveEntity(Long id) {
        return promotionTypeRepository.findByIdAndStatus(id, Boolean.TRUE)
                .orElseThrow(() -> new DataNotFoundException("Khong tim thay loai khuyen mai co id: " + id));
    }

    private void validateUnique(Long currentId, PromotionTypeRequest request) {
        String code = request.getCode().trim();
        String name = request.getName().trim();

        promotionTypeRepository.findByCode(code)
                .filter(entity -> !entity.getId().equals(currentId))
                .ifPresent(entity -> {
                    throw new DataExistingException("Ma loai khuyen mai da ton tai");
                });

        promotionTypeRepository.findByName(name)
                .filter(entity -> !entity.getId().equals(currentId))
                .ifPresent(entity -> {
                    throw new DataExistingException("Ten loai khuyen mai da ton tai");
                });
    }

    private String normalizeDescription(String description) {
        return description == null || description.isBlank() ? null : description.trim();
    }

    private PromotionTypeResponse toResponse(PromotionTypeEntity entity) {
        return PromotionTypeResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt() == null ? null : entity.getCreatedAt().format(DATE_TIME_FORMATTER))
                .updatedAt(entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().format(DATE_TIME_FORMATTER))
                .build();
    }
}
