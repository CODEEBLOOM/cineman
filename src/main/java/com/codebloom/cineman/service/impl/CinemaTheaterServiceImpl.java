package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.controller.request.CinemaTheaterRequest;
import com.codebloom.cineman.controller.request.CinemaTheaterResponse;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.DummySeat;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.CinemaTypeRepository;
import com.codebloom.cineman.repository.MovieTheaterRepository;
import com.codebloom.cineman.service.CinemaTheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CinemaTheaterServiceImpl implements CinemaTheaterService {

    private final CinemaTheatersRepository cinemaTheatersRepository;
    private final MovieTheaterRepository movieTheaterRepository;
    private final CinemaTypeRepository cinemaTypeRepository;

    /**
     * Creates a new cinema theater.
     *
     * @param request the request data
     * @return the created cinema theater
     */
    @Override
    public CinemaTheaterEntity create(CinemaTheaterRequest request) {
        // Find the movie theater
        MovieTheaterEntity movieTheater = movieTheaterRepository.findByStatusAndMovieTheaterId(true, request.getMovieTheaterId())
                .orElseThrow(() -> new DataNotFoundException("Movie Theater Not Found"));

        // Find the cinema type
        CinemaTypeEntity cinemaType = cinemaTypeRepository.findByStatusAndCinemaTypeId(true, request.getCinemaTypeId())
                .orElseThrow(() -> new DataNotFoundException("Cinema Type Not Found"));

        // Create the cinema theater
        CinemaTheaterEntity cinemaTheater = CinemaTheaterEntity.builder()
                .name(request.getName())
                .cinemaType(cinemaType)
                .movieTheater(movieTheater)
                .numberOfColumns(request.getNumberOfColumns())
                .numberOfRows(request.getNumberOfRows())
                .regularSeatRow(request.getRegularSeatRow())
                .doubleSeatRow(request.getDoubleSeatRow())
                .vipSeatRow(request.getVipSeatRow())
                .status(CinemaTheaterStatus.DRAFT)
                .build();

        return cinemaTheatersRepository.save(cinemaTheater);
    }


    /**
     * Updates a cinema theater with the given data.
     * @param id the id of the cinema theater
     * @param request the request data
     * @return the updated cinema theater
     */
    @Override
    public CinemaTheaterEntity update(Integer id, CinemaTheaterRequest request) {
        // Find the cinema theater
        CinemaTheaterEntity existCinemaTheater = cinemaTheatersRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Cinema Theater Not Found")
        );

        // Find the movie theater
        MovieTheaterEntity movieTheater = movieTheaterRepository.findByStatusAndMovieTheaterId(true, request.getMovieTheaterId())
                .orElseThrow(() -> new DataNotFoundException("Movie Theater Not Found"));

        // Find the cinema type
        CinemaTypeEntity cinemaType = cinemaTypeRepository.findByStatusAndCinemaTypeId(true, request.getCinemaTypeId())
                .orElseThrow(() -> new DataNotFoundException("Cinema Type Not Found"));

        // Update the cinema theater
        existCinemaTheater.setName(request.getName());
        existCinemaTheater.setCinemaType(cinemaType);
        existCinemaTheater.setMovieTheater(movieTheater);
        existCinemaTheater.setNumberOfColumns(request.getNumberOfColumns());
        existCinemaTheater.setNumberOfRows(request.getNumberOfRows());
        existCinemaTheater.setRegularSeatRow(request.getRegularSeatRow());
        existCinemaTheater.setDoubleSeatRow(request.getDoubleSeatRow());
        existCinemaTheater.setVipSeatRow(request.getVipSeatRow());

        // Save the changes
        return cinemaTheatersRepository.save(existCinemaTheater);
    }

    /**
     * Finds all cinema theaters with optional filtering by status.
     * @param pageRequest the pagination request details
     * @param status optional statuses to filter by
     * @return a response containing the list of cinema theaters and pagination metadata
     */
    @Override
    public CinemaTheaterResponse findAll(PageRequest pageRequest, CinemaTheaterStatus... status) {
        Page<CinemaTheaterEntity> page;
        // Check if any statuses are provided, and fetch accordingly
        if (status.length == 0) {
            // Fetch all cinema theaters not marked as INVALID
            page = cinemaTheatersRepository.findAllByStatusNot(CinemaTheaterStatus.INVALID,
                    org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize()));
        } else {
            // Fetch cinema theaters by the specified status
            page = cinemaTheatersRepository.findAllByStatus(status[0],
                    org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize()));
        }

        // Build metadata for the paginated response
        MetaResponse meta = MetaResponse.builder()
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .pageSize(pageRequest.getSize())
                .build();

        // Return the response with the list of cinema theaters and metadata
        return CinemaTheaterResponse.builder()
                .cinemaTheaters(page.getContent())
                .meta(meta)
                .build();
    }

    @Override
    public CinemaTheaterEntity findById(Integer id) {
        return cinemaTheatersRepository.findByStatusNotAndCinemaTheaterId(CinemaTheaterStatus.INVALID, id).orElseThrow(
                () ->  new DataNotFoundException("Cinema Theater Not Found With Id: " + id)
        );
    }

    /**
     * Marks a cinema theater as invalid, which is considered a soft delete.
     * @param id the id of the cinema theater to delete
     * @throws DataNotFoundException if the cinema theater does not exist
     */
    @Override
    public void delete(Integer id) {
        CinemaTheaterEntity cinemaTheater = cinemaTheatersRepository.findByStatusNotAndCinemaTheaterId(CinemaTheaterStatus.INVALID, id).orElseThrow(
                () ->  new DataNotFoundException("Cinema Theater Not Found With Id: " + id));
        cinemaTheater.setStatus(CinemaTheaterStatus.INVALID);
        cinemaTheatersRepository.save(cinemaTheater);
    }

    /**
     * Retrieves a seat map for a given cinema theater id.
     * @param cinemaTheaterId the id of the cinema theater
     * @return a response containing the list of seats and cinema theater details
     * @throws DataNotFoundException if the cinema theater does not exist
     */
    @Override
    public SeatMapResponse findSeatMapByCinemaTheaterId(Integer cinemaTheaterId) {
        CinemaTheaterEntity cinemaTheater = cinemaTheatersRepository.findByStatusNotAndCinemaTheaterId(CinemaTheaterStatus.INVALID, cinemaTheaterId).orElseThrow(
                () ->  new DataNotFoundException("Cinema Theater Not Found With Id: " + cinemaTheaterId));

        List<SeatEntity> seats = cinemaTheater.getSeats();
        return SeatMapResponse.builder()
                .seats(seats)
                .cinemaTheaterId(cinemaTheaterId)
                .numberOfColumn(cinemaTheater.getNumberOfColumns())
                .numberOfRows(cinemaTheater.getNumberOfRows())
                .doubleSeatRow(cinemaTheater.getDoubleSeatRow())
                .vipSeatRow(cinemaTheater.getVipSeatRow())
                .regularSeatRow(cinemaTheater.getRegularSeatRow())
                .status(cinemaTheater.getStatus())
                .build();
    }

    @Override
    public void publishedCinemaTheater(Integer id) {
        CinemaTheaterEntity cinemaTheater = cinemaTheatersRepository.findByStatusNotAndCinemaTheaterId(CinemaTheaterStatus.INVALID, id).orElseThrow(
                () ->  new DataNotFoundException("Cinema Theater Not Found With Id: " + id));
        cinemaTheater.setStatus(CinemaTheaterStatus.PUBLISHED);
        cinemaTheatersRepository.save(cinemaTheater);
    }

    /**
     * Method to convert a number to a letter.
     * @param number the number to be converted
     * @return the corresponding letter
     */
    private  String numberToLetter(int number) {
        // Convert the number to a letter by adding the number to the ASCII value of 'A'
        // and subtracting 1. This is because the ASCII value of 'A' is 65 and we want
        // to start from 'A' which is 1.
        return String.valueOf((char) ('A' + number - 1));
    }

}
