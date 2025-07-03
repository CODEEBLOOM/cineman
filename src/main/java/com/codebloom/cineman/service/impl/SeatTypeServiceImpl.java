package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.SeatType;
import com.codebloom.cineman.controller.request.SeatTypeRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.SeatTypeEntity;
import com.codebloom.cineman.repository.SeatTypeRepository;
import com.codebloom.cineman.service.SeatTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j(topic = "SEAT-TYPE-SERVICE")
@RequiredArgsConstructor
public class SeatTypeServiceImpl implements SeatTypeService {

    private final SeatTypeRepository seatTypeRepository;


    /**
     * Tạo mới một loại ghế
     * @param seatTypeRequest SeatTypeRequest
     * @return SeatTypeEntity
     */
    @Override
    public SeatTypeEntity create(SeatTypeRequest seatTypeRequest) {
        Optional<SeatTypeEntity> seatTypeEntity = seatTypeRepository.findByNameAndStatus(seatTypeRequest.getName(), true);
        if (seatTypeEntity.isPresent()) {
            throw new DataExistingException("Seat type already exists with name: " + seatTypeRequest.getName());
        }
        SeatTypeEntity createSeatTypeEntity = SeatTypeEntity.builder()
                .name(seatTypeRequest.getName())
                .price(seatTypeRequest.getPrice())
                .status(true)
                .build();
        return seatTypeRepository.save(createSeatTypeEntity);
    }


    /**
     * Cập nhật thông tin của loại ghế
     * @param id id của loại ghế
     * @param seatTypeRequest SeatTypeRequest
     * @return SeatTypeEntity
     */
    @Override
    public SeatTypeEntity update(String id, SeatTypeRequest seatTypeRequest) {
        SeatTypeEntity seatTypeEntity = seatTypeRepository.findByStatusAndId(true, id)
                .orElseThrow(() -> new DataNotFoundException("Seat type not found with id: " + id));
        seatTypeRepository.findByNameAndStatusAndIdNot(seatTypeRequest.getName(), true, id)
                        .ifPresent((seatType) -> {
                            throw new DataExistingException("Seat type already exists with name: " + seatTypeRequest.getName());
                        });
        seatTypeEntity.setName(seatTypeRequest.getName());
        seatTypeEntity.setPrice(seatTypeRequest.getPrice());
        seatTypeEntity.setStatus(true);
        return seatTypeRepository.save(seatTypeEntity);
    }

    /**
     * Lay toan bo loai ghe cua he thong
     * @return List<SeatTypeEntity>
     */
    @Override
    public List<SeatTypeEntity> findAll() {
        return seatTypeRepository.findAllByStatus(true);
    }

    /**
     * Lay thong tin loai ghe theo id
     * @param id id cua loai ghe
     * @return  SeatTypeEntity
     */
    @Override
    public SeatTypeEntity findById(String id) {
        return seatTypeRepository.findByStatusAndId(true, id)
                .orElseThrow(() -> new DataNotFoundException("Seat type not found with id: " + id));
    }

    /**
     * Xoa mem loai ghe theo id
     * @param id id cua loai ghe
     */
    @Override
    public void delete(String id) {
        SeatTypeEntity seatTypeEntity = findById(id);
        seatTypeEntity.setStatus(false);
        seatTypeRepository.save(seatTypeEntity);
    }
}
