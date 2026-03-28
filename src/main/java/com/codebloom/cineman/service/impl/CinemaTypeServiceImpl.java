package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.CinemaTypeRequest;
import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.CinemaTypeEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.CinemaTypeRepository;
import com.codebloom.cineman.repository.ShowTimeRepository;
import com.codebloom.cineman.service.CinemaTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic  = "CINEMA-TYPE-SERVICE-IMPL")
public class CinemaTypeServiceImpl implements CinemaTypeService {

    private final CinemaTypeRepository cinemaTypeRepository;
    private final CinemaTheatersRepository cinemaTheatersRepository;
    private final ShowTimeRepository showTimeRepository;

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
    @Transactional
    public CinemaTypeEntity update(Integer id, CinemaTypeRequest cinemaTypeEntity) {
        cinemaTypeRepository.findByCodeAndStatusAndCinemaTypeIdNot(cinemaTypeEntity.getCode(), true, id)
                .ifPresent(existingCinemaType -> {
                    throw new DataExistingException("Cinema type already Exists With Code: " + cinemaTypeEntity.getCode());
                });
        CinemaTypeEntity cinemaType = cinemaTypeRepository.findByCinemaTypeIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Cinema Type Not Found With Id: " + id));
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
    @Transactional
    public void delete(Integer id) {
        CinemaTypeEntity cinemaType = cinemaTypeRepository.findByCinemaTypeIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Cinema Type Not Found With Id: " + id));
        List<CinemaTheaterEntity> cinemaTheaters = cinemaTheatersRepository
                .findAllByStatusNotAndCinemaType_CinemaTypeId(CinemaTheaterStatus.INVALID, id);
        softDeleteCinemaTheaters(cinemaTheaters);
        cinemaType.setStatus(false);
        cinemaTypeRepository.save(cinemaType);
    }

    private void softDeleteCinemaTheaters(List<CinemaTheaterEntity> cinemaTheaters) {
        if (cinemaTheaters.isEmpty()) {
            return;
        }

        List<ShowTimeEntity> showTimes = showTimeRepository.findAllByCinemaTheaterInAndStatusNot(cinemaTheaters, ShowTimeStatus.DELETED);
        showTimes.forEach(showTime -> showTime.setStatus(ShowTimeStatus.DELETED));
        cinemaTheaters.forEach(cinemaTheater -> cinemaTheater.setStatus(CinemaTheaterStatus.INVALID));

        showTimeRepository.saveAll(showTimes);
        cinemaTheatersRepository.saveAll(cinemaTheaters);
    }
}
