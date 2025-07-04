package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.CinemaTypeRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.CinemaTypeEntity;
import com.codebloom.cineman.repository.CinemaTypeRepository;
import com.codebloom.cineman.service.CinemaTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic  = "CINEMA-TYPE-SERVICE-IMPL")
public class CinemaTypeServiceImpl implements CinemaTypeService {

    private final CinemaTypeRepository cinemaTypeRepository;

    /**
     * lấy tất cinema type theo id
     * @param id : id cinema type
     * @return CinemaTypeEntity
     */
    @Override
    public CinemaTypeEntity findById(Integer id) {
        return cinemaTypeRepository.findByCinemaTypeIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Cinema Type Not Found With Id: " + id));
    }

    /**
     * Lấy toàn bộ cinema type
     * @return List<CinemaTypeEntity>
     */
    @Override
    public List<CinemaTypeEntity> findAll() {
        return cinemaTypeRepository.findAllByStatus(true);
    }


    /**
     * Tạo mới một cinema type
     * @param cinemaTypeEntity CinemaTypeRequest
     * @return CinemaTypeEntity
     */
    @Override
    public CinemaTypeEntity create(CinemaTypeRequest cinemaTypeEntity) {
        cinemaTypeRepository.findByCodeAndStatus(cinemaTypeEntity.getCode(), true)
                .ifPresent((cinemaTypeEntity1) -> {throw new DataExistingException("Cinema type already Exists With Code: " + cinemaTypeEntity.getCode());});
        CinemaTypeEntity cinemaType = CinemaTypeEntity.builder()
                .name(cinemaTypeEntity.getName())
                .description(cinemaTypeEntity.getDescription())
                .code(cinemaTypeEntity.getCode())
                .status(true)
                .build();
        return cinemaTypeRepository.save(cinemaType);
    }

    /**
     * Cập nhật thông tin của cinema type
     * @param id id của cinema type
     * @param cinemaTypeEntity CinemaTypeRequest
     * @return CinemaTypeEntity
     */
    @Override
    public CinemaTypeEntity update(Integer id, CinemaTypeRequest cinemaTypeEntity) {
        CinemaTypeEntity cinemaType = cinemaTypeRepository.findByCodeAndStatusAndCinemaTypeIdNot(cinemaTypeEntity.getCode(), true, id)
                .orElseThrow(() -> new DataExistingException("Cinema type already Exists With Code: " + cinemaTypeEntity.getCode()));
        cinemaType.setName(cinemaTypeEntity.getName());
        cinemaType.setDescription(cinemaTypeEntity.getDescription());
        cinemaType.setCode(cinemaTypeEntity.getCode());
        cinemaType.setStatus(true);
        return cinemaTypeRepository.save(cinemaType);
    }

    /**
     * Hàm xóa mềm cinema type
     * @param id id của cinema type
     */
    @Override
    public void delete(Integer id) {
        CinemaTypeEntity cinemaType = cinemaTypeRepository.findByCinemaTypeIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Cinema Type Not Found With Id: " + id));
        cinemaType.setStatus(false);
        cinemaTypeRepository.save(cinemaType);
    }
}
