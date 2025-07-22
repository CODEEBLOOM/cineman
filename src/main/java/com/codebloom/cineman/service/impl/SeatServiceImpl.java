package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.common.enums.SeatStatus;
import com.codebloom.cineman.common.enums.SeatType;
import com.codebloom.cineman.controller.request.SeatRequest;
import com.codebloom.cineman.controller.response.SeatResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.exception.InvalidDataException;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.SeatEntity;
import com.codebloom.cineman.model.SeatTypeEntity;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.SeatRepository;
import com.codebloom.cineman.repository.SeatTypeRepository;
import com.codebloom.cineman.service.SeatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SEAT-SERVICE")
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatTypeRepository seatTypeRepository;
    private final CinemaTheatersRepository cinemaTheatersRepository;

    /**
     * Finds a seat by its id and the id of the cinema theater that the seat belongs to.
     * @param cinemaTheaterId the id of the cinema theater
     * @param id the id of the seat
     * @return a seat response
     * @throws DataNotFoundException if the cinema theater with the given id not found or
     * the seat with the given id not found in the given cinema theater
     */
    @Override
    public SeatResponse findById(Integer cinemaTheaterId, long id) {
        CinemaTheaterEntity cinemaTheater = cinemaTheatersRepository.findByStatusNotAndCinemaTheaterId(CinemaTheaterStatus.INVALID, cinemaTheaterId)
                .orElseThrow(() -> new DataNotFoundException("Cinema Theater Not Found"));
        SeatEntity seatEntity =  seatRepository.findByIdAndCinemaTheaterAndStatusNot(id, cinemaTheater, SeatStatus.DELETED).orElseThrow(
                () -> new DataNotFoundException("Seat with id " + id + " not found")
        );
        return convertToSeatResponse(seatEntity);
    }

    /**
     * Finds all seats by a given cinema theater id.
     * @param cinemaTheaterId the id of the cinema theater
     * @return a list of seat responses
     * @throws DataNotFoundException if the cinema theater with the given id not found
     */
    @Override
    public List<SeatResponse> findAllByCinemaTheater(Integer cinemaTheaterId) {
        CinemaTheaterEntity cinemaTheater = cinemaTheatersRepository.findByStatusNotAndCinemaTheaterId(CinemaTheaterStatus.INVALID, cinemaTheaterId)
                .orElseThrow(() -> new DataNotFoundException("Cinema Theater Not Found"));
        return seatRepository.findAllByStatusNotAndCinemaTheater(SeatStatus.DELETED, cinemaTheater)
                .stream()
                .map(this::convertToSeatResponse)
                .collect(Collectors.toList());
    }

    /**
     * Saves a seat.
     * @param seat the seat information to be saved
     * @return the saved seat
     * @throws DataNotFoundException if the seat type or cinema theater not found
     */
    @Override
    @Transactional
    public SeatEntity save(SeatRequest seat) {
        log.info("Saving seat: {}", seat);
        SeatType existingSeatType = switch (seat.getSeatType()) {
            case "VIP" -> SeatType.VIP;
            case "DOUBLE" -> SeatType.DOUBLE;
            case "REGULAR" -> SeatType.REGULAR;
            default -> throw new DataNotFoundException("Seat Type Not Found");
        };

        SeatTypeEntity seatType = seatTypeRepository.findByIdAndStatus(existingSeatType, true)
                .orElseThrow(() -> new DataNotFoundException("Seat Type Not Found With Name: " + seat.getSeatType()));

        CinemaTheaterEntity cinemaTheater = cinemaTheatersRepository.findByStatusNotAndCinemaTheaterId(CinemaTheaterStatus.INVALID, seat.getCinemaTheaterId())
                .orElseThrow(() -> new DataNotFoundException("Cinema Theater Not Found"));
        checkCinemaTheaterStatus(cinemaTheater.getCinemaTheaterId());
        SeatEntity seatEntity = SeatEntity.builder()
                .seatType(seatType)
                .label(seat.getLabel().isEmpty() ? String.valueOf((char) ('A' + seat.getRowIndex() - 1)) : seat.getLabel())
                .columnIndex(seat.getColumnIndex())
                .rowIndex(seat.getRowIndex())
                .cinemaTheater(cinemaTheater)
                .status(SeatStatus.ACTIVE)
                .build();
        log.info("Saved seat: {}", seat);
        return seatRepository.save(seatEntity);
    }

    /**
     * Updates a seat.
     * @param id the id of the seat to be updated
     * @param seat the new seat information
     * @return the updated seat
     * @throws DataNotFoundException if the seat with the given id not found
     */
    @Override
    public SeatEntity update(long id, SeatRequest seat) {
        if(seat.getStatus() == SeatStatus.DELETED) {
            throw new DataNotFoundException("Seat Status Not Found");
        }
        SeatEntity seatEntity = seatRepository.findByIdAndStatusNot(id, seat.getStatus())
                .orElseThrow(() -> new DataNotFoundException("Seat Not Found With Id: " + id));
        checkCinemaTheaterStatus(seatEntity.getCinemaTheater().getCinemaTheaterId());
        seatEntity.setLabel(seat.getLabel());
        if(!seat.getStatus().equals(SeatStatus.DELETED)){
            seatEntity.setStatus(seat.getStatus());
        }
        seatEntity.setColumnIndex(seat.getColumnIndex());
        return seatRepository.save(seatEntity);
    }

    /**
     * Deletes a seat.
     *
     * @param id the id of the seat to be deleted
     * @throws DataNotFoundException if the seat with the given id not found
     */
    @Override
    public void delete(long id) {
        SeatEntity seatEntity = seatRepository.findByIdAndStatusNot(id, SeatStatus.DELETED)
                .orElseThrow(() -> new DataNotFoundException("Seat Not Found With Id: " + id));
        checkCinemaTheaterStatus(seatEntity.getCinemaTheater().getCinemaTheaterId());
        if(seatEntity.getCinemaTheater().getStatus().equals(CinemaTheaterStatus.DRAFT)){
            seatRepository.delete(seatEntity);
        }else {
            seatEntity.setStatus(SeatStatus.DELETED);
            seatRepository.save(seatEntity);
        }
    }

    /**
     * Save multiple seats in one shot.
     * @param seats The list of SeatRequest
     * @return The list of SeatResponse
     */
    @Override
    @Transactional
    public List<SeatResponse> addMultiple(List<SeatRequest> seats) {
        List<SeatResponse> list = new ArrayList<>();
        seats.forEach(seat -> {
            SeatResponse seatRes = convertToSeatResponse(this.save(seat));
            list.add(seatRes);
        });
        return list;
    }


    /**
     * Deletes multiple seats in one shot.
     * @param ids The list of ids to be deleted
     */
    @Override
    @Transactional
    public void deleteMultiple(List<Long> ids) {
        ids.forEach(this::delete);
    }

    /**
     * Change the status of the seat.
     * If the cinema theater is published, it will change the status of the seat
     * between active and inactive.
     * @param id the id of the seat
     * @return the seat response
     * @throws DataNotFoundException if the seat with the given id not found
     */
    @Override
    public SeatResponse changeStatus(long id) {
        SeatEntity seatEntity = seatRepository.findByIdAndStatusNot(id, SeatStatus.DELETED)
                .orElseThrow(() -> new DataNotFoundException("Seat Not Found With Id: " + id));
        if(seatEntity.getCinemaTheater().getStatus().equals(CinemaTheaterStatus.PUBLISHED)){
            if(seatEntity.getStatus().equals(SeatStatus.ACTIVE)){
                seatEntity.setStatus(SeatStatus.INACTIVE);
            }else if(seatEntity.getStatus().equals(SeatStatus.INACTIVE)){
                seatEntity.setStatus(SeatStatus.ACTIVE);
            }
        }
        return convertToSeatResponse(seatRepository.save(seatEntity));
    }

    /**
     * Converts a SeatEntity object to a SeatResponse object.
     * This method takes a SeatEntity object and returns a new SeatResponse object with the same information.
     * @param seatEntity the SeatEntity to be converted
     * @return the converted SeatResponse object
     */
    private SeatResponse convertToSeatResponse(SeatEntity seatEntity) {
        // Create a new SeatResponse object with the same information as the SeatEntity
        return SeatResponse.builder()
                .id(seatEntity.getId())
                .seatType(seatEntity.getSeatType())
                .label(seatEntity.getLabel())
                .columnIndex(seatEntity.getColumnIndex())
                .rowIndex(seatEntity.getRowIndex())
                .status(seatEntity.getStatus())
                .build();
    }

    /**
     * Checks if the cinema theater with the given id is published.
     * If the cinema theater is published, it throws an InvalidDataException.
     * @param cinemaTheaterId the id of the cinema theater
     * @throws InvalidDataException if the cinema theater is published
     * @throws DataNotFoundException if the cinema theater is not found
     */
    private void checkCinemaTheaterStatus(Integer cinemaTheaterId) {
        CinemaTheaterEntity cinemaTheater = cinemaTheatersRepository.findByStatusNotAndCinemaTheaterId(CinemaTheaterStatus.INVALID, cinemaTheaterId)
                .orElseThrow(() -> new DataNotFoundException("Cinema Theater Not Found"));
        if(cinemaTheater.getStatus().equals(CinemaTheaterStatus.PUBLISHED)){
            throw new InvalidDataException("Phòng chiếu đã được xuất bản, chỉ có thể cập nhật trạng thái ghế !");
        }
    }
}
