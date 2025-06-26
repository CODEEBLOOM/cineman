package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.SeatMapStatus;

import com.codebloom.cineman.controller.request.SeatMapRequest;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.controller.response.SeatMapResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.SeatMapEntity;
import com.codebloom.cineman.repository.SeatMapRepository;
import com.codebloom.cineman.service.SeatMapService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeatMapServiceImpl implements SeatMapService {

    private final SeatMapRepository seatMapRepository;

    @Override
    public List<SeatMapEntity> findAll() {
        return seatMapRepository.findAllByStatusNot(SeatMapStatus.DA_XOA);
    }

    @Override
    public SeatMapResponse findAll(com.codebloom.cineman.controller.request.PageRequest pageRequest, Map<String, Object>... params) {
        Page<SeatMapEntity> page =  null;
        PageRequest pageReq = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        if(params.length > 0 && params[0].containsKey("status")) {
            Boolean status  =Boolean.valueOf(params[0].get("status")+"");
            page = seatMapRepository.findAllByStatus(status ? SeatMapStatus.XUAT_BAN: SeatMapStatus.NHAP, pageReq);
        }else{
            page = seatMapRepository
                    .findAllByStatusNot(SeatMapStatus.DA_XOA, pageReq);
        }
        MetaResponse meta = MetaResponse.builder()
                .totalElements((int)page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .currentPage(page.getNumber())
                .build();
        return SeatMapResponse.builder()
                .seatMapEntity(page.getContent())
                .meta(meta)
                .build();
    }

    @Override
    public SeatMapEntity findById(Integer id) {
        return seatMapRepository.findByIdAndStatusNot(id, SeatMapStatus.DA_XOA)
                .orElseThrow(() -> new DataNotFoundException("SeatMap Not Found with id: " + id));
    }

    @Override
    @Transactional
    public SeatMapEntity save(SeatMapRequest seatMapRequest) {
        SeatMapEntity seatMapEntity = SeatMapEntity.builder()
                .name(seatMapRequest.getName())
                .numberOfColumns(seatMapRequest.getNumberOfColumns())
                .numberOfRows(seatMapRequest.getNumberOfRows())
                .regularSeatRow(seatMapRequest.getRegularSeatRow())
                .vipSeatRow(seatMapRequest.getVipSeatRow())
                .doubleSeatRow(seatMapRequest.getDoubleSeatRow())
                .description(seatMapRequest.getDescription())
                .status(seatMapRequest.getStatus() ? SeatMapStatus.XUAT_BAN : SeatMapStatus.NHAP)
                .build();
        return seatMapRepository.save(seatMapEntity);
    }

    @Override
    public SeatMapEntity update(Integer id, SeatMapRequest seatMapRequest) {
        SeatMapEntity existSeatMap = seatMapRepository.findByIdAndStatusNot(id, SeatMapStatus.DA_XOA)
                .orElseThrow(() -> new DataNotFoundException("SeatMap Not Found with id: " + id));

        existSeatMap.setName(seatMapRequest.getName());
        existSeatMap.setNumberOfColumns(seatMapRequest.getNumberOfColumns());
        existSeatMap.setNumberOfRows(seatMapRequest.getNumberOfRows());
        existSeatMap.setRegularSeatRow(seatMapRequest.getRegularSeatRow());
        existSeatMap.setVipSeatRow(seatMapRequest.getVipSeatRow());
        existSeatMap.setDoubleSeatRow(seatMapRequest.getDoubleSeatRow());
        existSeatMap.setDescription(seatMapRequest.getDescription());
        existSeatMap.setStatus(seatMapRequest.getStatus() ? SeatMapStatus.XUAT_BAN : SeatMapStatus.NHAP);
        return seatMapRepository.save(existSeatMap);
    }

    @Override
    public void deleteById(Integer id) {
        SeatMapEntity existSeatMap = seatMapRepository.findByIdAndStatusNot(id, SeatMapStatus.DA_XOA)
                .orElseThrow(() -> new DataNotFoundException("SeatMap Not Found with id: " + id));
        existSeatMap.setStatus(SeatMapStatus.DA_XOA);
        seatMapRepository.save(existSeatMap);
    }
}
