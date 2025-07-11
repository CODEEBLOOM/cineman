package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.constant.MovieStatus;
import com.codebloom.cineman.common.constant.MovieTheaterOfficeHours;
import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.controller.request.ShowTimeRequest;
import com.codebloom.cineman.controller.response.ShowTimeDetailResponse;
import com.codebloom.cineman.controller.response.ShowTimeResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.ShowTimeRepository;
import com.codebloom.cineman.service.ShowTimeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SHOWTIME-SERVICE")
public class ShowTimeServiceImpl implements ShowTimeService {

    private final ShowTimeRepository showTimeRepository;
    private final CinemaTheatersRepository cinemaTheaterRepository;
    private final MovieRepository movieRepository;

    /**
     * Tạo một lịch chiếu phim
     *
     * @param request ShowTimeRequest
     * @return ShowTimeResponse
     */
    @Override
    @Transactional
    public ShowTimeResponse create(ShowTimeRequest request) {
        log.info("Create Showtime With request: {}", request);
        CinemaTheaterEntity cinemaTheater = cinemaTheaterRepository.findByStatusAndCinemaTheaterId(CinemaTheaterStatus.PUBLISHED, request.getCinemaTheaterId())
                .orElseThrow(() -> new IllegalArgumentException("Cinema Theater Not Found With Id: " + request.getCinemaTheaterId()));

        MovieEntity movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie Not Found With Id: " + request.getMovieId()));

        if (movie.getReleaseDate().after(request.getShowDate())) {
            throw new IllegalArgumentException("Movie is not released yet !");
        }

        LocalTime endTime = this.checkShowTime(request, movie, cinemaTheater);
        ShowTimeEntity showTimeEntity = ShowTimeEntity.builder()
                .showDate(request.getShowDate())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .originPrice(request.getOriginPrice())
                .status(request.getStatus())
                .movie(movie)
                .cinemaTheater(cinemaTheater)
                .build();
        showTimeEntity = showTimeRepository.save(showTimeEntity);
        log.info("Created Showtime With Id: {} and status: {}", showTimeEntity.getId(), showTimeEntity.getStatus());
        return convertToShowTimeResponse(showTimeEntity);
    }

    /**
     * Update showtime
     *
     * @param id      showtime id
     * @param request ShowTimeRequest
     * @return ShowTimeResponse
     */
    @Override
    @Transactional
    public ShowTimeResponse update(Long id, ShowTimeRequest request) {
        log.info("Update Showtime With Id: {} and request: {}", id, request);
        ShowTimeEntity showTimeEntity = showTimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showtime Not Found With Id: " + id));
        if (showTimeEntity.getStatus().equals(ShowTimeStatus.VALID)) {
            throw new IllegalArgumentException("Showtime is not available to update !");
        } else {

            CinemaTheaterEntity cinemaTheater = cinemaTheaterRepository.findByStatusAndCinemaTheaterId(CinemaTheaterStatus.PUBLISHED, request.getCinemaTheaterId())
                    .orElseThrow(() -> new IllegalArgumentException("Cinema Theater Not Found With Id: " + request.getCinemaTheaterId()));

            MovieEntity movie = movieRepository.findById(request.getMovieId())
                    .orElseThrow(() -> new IllegalArgumentException("Movie Not Found With Id: " + request.getMovieId()));

            LocalTime endTime = this.checkShowTime(request, movie, cinemaTheater, showTimeEntity.getId());

            showTimeEntity.setShowDate(request.getShowDate());
            showTimeEntity.setStartTime(request.getStartTime());
            showTimeEntity.setEndTime(endTime);
            showTimeEntity.setOriginPrice(request.getOriginPrice());
            showTimeEntity.setStatus(request.getStatus());
            showTimeEntity = showTimeRepository.save(showTimeEntity);
            log.info("Updated Showtime With Id: {} and status: {}", id, showTimeEntity.getStatus());
            return convertToShowTimeResponse(showTimeEntity);
        }
    }

    /**
     * Find all showtime
     *
     * @return list showtime response
     */
    @Override
    public List<ShowTimeResponse> findAll() {
        return showTimeRepository.findAllByStatusNot(ShowTimeStatus.DELETED)
                .stream()
                .map(this::convertToShowTimeResponse)
                .toList();
    }

    /**
     * Find showtime by id
     *
     * @param id showtime id
     * @return showtime response
     */
    @Override
    public ShowTimeResponse findById(Long id) {
        return showTimeRepository.findByIdAndStatusNot(id, ShowTimeStatus.DELETED)
                .map(this::convertToShowTimeResponse)
                .orElseThrow(() -> new DataNotFoundException("Showtime Not Found With Id: " + id));
    }

    /**
     * delete showtime ( soft delete )
     *
     * @param id showtime id
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Delete Showtime With Id: {}", id);
        ShowTimeEntity showTimeEntity = showTimeRepository.findByIdAndStatusNot(id, ShowTimeStatus.DELETED)
                .orElseThrow(() -> new DataNotFoundException("Showtime Not Found With Id: " + id));
        showTimeEntity.setStatus(ShowTimeStatus.DELETED);
        showTimeRepository.save(showTimeEntity);
    }

    /**
     * Find all showtime by movie id
     *
     * @param movieId movie id
     * @return list of showtime response
     */
    @Override
    public List<ShowTimeResponse> findShowTimeByMovieId(Integer movieId) {

        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new DataNotFoundException("Movie Not Found With Id: " + movieId));

        if (movie.getStatus().getStatusId().equals(MovieStatus.MOVIE_STATUS_CNS)) {
            throw new IllegalArgumentException("Movie is not available !");
        }

        List<ShowTimeResponse> showTimes = showTimeRepository.findAllByMovieAndStatusNot(movie, ShowTimeStatus.DELETED)
                .stream()
                .map(this::convertToShowTimeResponse)
                .toList();

        return !showTimes.isEmpty() ? showTimes : null;
    }

    /**
     * Find all showtime by cinema theater id
     *
     * @param cinemaTheaterId cinema theater id
     * @return list of showtime response
     */
    @Override
    public List<ShowTimeResponse> findShowTimeByCinemaTheaterId(Integer cinemaTheaterId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "showDate");
        CinemaTheaterEntity cinemaTheater = cinemaTheaterRepository.findByStatusAndCinemaTheaterId(CinemaTheaterStatus.PUBLISHED, cinemaTheaterId)
                .orElseThrow(() -> new DataNotFoundException("Cinema Theater Not Found With Id: " + cinemaTheaterId));
        List<ShowTimeResponse> showTimes = showTimeRepository.findAllByCinemaTheaterAndStatusNot(cinemaTheater, ShowTimeStatus.DELETED, sort)
                .stream()
                .map(this::convertToShowTimeResponse)
                .toList();
        return showTimes.isEmpty() ? null : showTimes;
    }

    @Override
    public List<ShowTimeEntity> findAllShowTimeByMovieIdAndMovieTheaterId(Integer movieId, Integer movieTheaterId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "showDate");
        List<ShowTimeEntity> showTimes = showTimeRepository.findAllShowTimeByMovieIdAndMovieTheaterId(movieId, ShowTimeStatus.VALID, movieTheaterId, sort);
        return showTimes.isEmpty() ? null : showTimes;
    }

    // Tìm kiếm showtime theo movie id và movie theater id and show date
    @Override
    public List<ShowTimeDetailResponse> findAllShowTimeByMovieIdAndMovieTheaterIdAndShowDateEqual(Integer movieId, Integer cinemaTheaterId, Date showDate) {
        Sort sort = Sort.by(Sort.Direction.ASC, "showDate");
        List<ShowTimeEntity> showTimes = showTimeRepository.findAllShowTimeByMovieIdAndMovieTheaterIdAndShowDateEqual(movieId, showDate, cinemaTheaterId, ShowTimeStatus.VALID, sort);

        List<ShowTimeDetailResponse> showTimeDetailResponses = showTimes.stream()
                .map(showTime -> {
                    return ShowTimeDetailResponse.builder()
                            .showTime(showTime)
                            .totalSeatEmpty(this.findCountByShowTimeId(showTime.getId()).intValue())
                            .movie(showTime.getMovie())
                            .cinemaTheater(showTime.getCinemaTheater())
                            .build();
                }).toList();
        return showTimeDetailResponses.isEmpty() ? null : showTimeDetailResponses;
    }

    /**
     * Find all seat by showtime id
     *
     * @param showTimeId cinema theater id
     * @return showtime response
     */
    @Override
    public Long findCountByShowTimeId(Long showTimeId) {
        return showTimeRepository.countSeatByShowTimeId(showTimeId, ShowTimeStatus.VALID);
    }

    /**
     * Convert ShowTimeEntity to ShowTimeResponse
     *
     * @param showTimeEntity ShowTimeEntity
     * @return ShowTimeResponse
     */
    private ShowTimeResponse convertToShowTimeResponse(ShowTimeEntity showTimeEntity) {
        return ShowTimeResponse.builder()
                .id(showTimeEntity.getId())
                .showDate(showTimeEntity.getShowDate())
                .startTime(showTimeEntity.getStartTime())
                .endTime(showTimeEntity.getEndTime())
                .status(showTimeEntity.getStatus())
                .originPrice(showTimeEntity.getOriginPrice())
                .movie(showTimeEntity.getMovie())
                .cinemaTheater(showTimeEntity.getCinemaTheater())
                .build();
    }

    private LocalTime checkShowTime(ShowTimeRequest request, MovieEntity movie, CinemaTheaterEntity cinemaTheater, Long... showTimeId) {


        LocalTime endTime = request.getStartTime().plusMinutes(movie.getDuration());
        /* Kiểm tra giờ hành chính của rạp chiếu*/
        if (request.getStartTime().isBefore(MovieTheaterOfficeHours.OPENING_HOURS) || endTime.isAfter(MovieTheaterOfficeHours.CLOSING_HOURS)) {
            log.info("Showtime must be between {} and {}", MovieTheaterOfficeHours.OPENING_HOURS, MovieTheaterOfficeHours.CLOSING_HOURS);
            throw new IllegalArgumentException("Showtime must be between " + MovieTheaterOfficeHours.OPENING_HOURS + " and " + MovieTheaterOfficeHours.CLOSING_HOURS);
        }

        /* Kiểm tra trạng thái phim trước khi tạo showtime */
        if (movie.getStatus().getStatusId().equals(MovieStatus.MOVIE_STATUS_CNS)
                || movie.getStatus().getStatusId().equals(MovieStatus.MOVIE_STATUS_NC)) {
            throw new DataNotFoundException("Movie is not available !");
        }

        /* Lấy danh sách showtime trong ngày của phòng chiếu và sắp xếp theo thời gian tăng dần của startTime*/
        Sort sort = Sort.by(Sort.Direction.ASC, "startTime");
        List<ShowTimeEntity> showTimesEntity = showTimeRepository
                .findAllByCinemaTheaterAndShowDateAndStatusNot(cinemaTheater, request.getShowDate(), ShowTimeStatus.DELETED, sort);


        /* Kiểm tra xem có bị đè khung giờ của nhau hay không*/
        for (ShowTimeEntity showTimeEntity : showTimesEntity) {

            LocalTime existingStart = showTimeEntity.getStartTime();
            LocalTime existingEnd = showTimeEntity.getEndTime();

            LocalTime newStart = request.getStartTime();
            if (showTimeId.length > 0 && showTimeEntity.getId().equals(showTimeId[0])) continue;

            if ((newStart.isBefore(existingEnd) || newStart.equals(existingEnd)) && (existingStart.isBefore(endTime) || existingStart.equals(endTime))) {
                log.error("Showtime already exists at this time!");
                throw new IllegalArgumentException("Showtime already exists at this time!");
            }
        }
        return endTime;
    }
}
