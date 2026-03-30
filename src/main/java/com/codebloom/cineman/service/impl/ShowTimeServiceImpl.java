package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.constant.MovieStatus;
import com.codebloom.cineman.common.constant.MovieTheaterOfficeHours;
import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.controller.request.MoviePageQueryRequest;
import com.codebloom.cineman.controller.request.ShowTimeDetailResponseNew;
import com.codebloom.cineman.controller.request.ShowTimeRequest;
import com.codebloom.cineman.controller.request.ShowTimeRequestNew;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.controller.response.ShowTimeDetailResponse;
import com.codebloom.cineman.controller.response.ShowTimeResponse;
import com.codebloom.cineman.exception.ConflictException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieVariationRepository;
import com.codebloom.cineman.repository.ShowTimeRepository;
import com.codebloom.cineman.service.MovieService;
import com.codebloom.cineman.service.MovieStatusService;
import com.codebloom.cineman.service.ShowTimeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final MovieService movieService;
    private final MovieStatusService movieStatusService;
    private final MovieVariationRepository movieVariationRepository;

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
                .orElseThrow(() -> new ConflictException("Cinema Theater Not Found With Id: " + request.getCinemaTheaterId()));

        MovieEntity movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ConflictException("Movie Not Found With Id: " + request.getMovieId()));

        if (movie.getReleaseDate().after(request.getShowDate())) {
            throw new ConflictException("Movie is not released yet !");
        }

        LocalTime endTime = this.checkShowTime(request, movie, cinemaTheater);
        MovieVariationEntity movieVariationEntity = movieVariationRepository.findById(request.getMovieVariationId())
                .orElseThrow(() -> new DataNotFoundException("Movie Variation Not Found With Id: " + request.getMovieVariationId()));
        ShowTimeEntity showTimeEntity = ShowTimeEntity.builder()
                .showDate(request.getShowDate())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .originPrice(request.getOriginPrice())
                .status(request.getStatus())
                .special(Boolean.TRUE.equals(request.getSpecial()))
                .movie(movie)
                .cinemaTheater(cinemaTheater)
                .movieVariation(movieVariationEntity)
                .build();
        showTimeEntity = showTimeRepository.save(showTimeEntity);
        syncMovieStatusAfterShowTimeChanged(movie.getMovieId());
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
                .orElseThrow(() -> new ConflictException("Showtime Not Found With Id: " + id));
        Integer oldMovieId = showTimeEntity.getMovie().getMovieId();
        if (showTimeEntity.getStatus().equals(ShowTimeStatus.VALID)) {
            throw new ConflictException("Showtime is not available to update !");
        } else {

            CinemaTheaterEntity cinemaTheater = cinemaTheaterRepository.findByStatusAndCinemaTheaterId(CinemaTheaterStatus.PUBLISHED, request.getCinemaTheaterId())
                    .orElseThrow(() -> new ConflictException("Cinema Theater Not Found With Id: " + request.getCinemaTheaterId()));

            MovieEntity movie = movieRepository.findById(request.getMovieId())
                    .orElseThrow(() -> new ConflictException("Movie Not Found With Id: " + request.getMovieId()));

            LocalTime endTime = this.checkShowTime(request, movie, cinemaTheater, showTimeEntity.getId());
            MovieVariationEntity movieVariationEntity = movieVariationRepository.findById(request.getMovieVariationId())
                    .orElseThrow(() -> new DataNotFoundException("Movie Variation Not Found With Id: " + request.getMovieVariationId()));

            showTimeEntity.setShowDate(request.getShowDate());
            showTimeEntity.setStartTime(request.getStartTime());
            showTimeEntity.setEndTime(endTime);
            showTimeEntity.setOriginPrice(request.getOriginPrice());
            showTimeEntity.setStatus(request.getStatus());
            showTimeEntity.setSpecial(Boolean.TRUE.equals(request.getSpecial()));
            showTimeEntity.setMovie(movie);
            showTimeEntity.setCinemaTheater(cinemaTheater);
            showTimeEntity.setMovieVariation(movieVariationEntity);
            showTimeEntity = showTimeRepository.save(showTimeEntity);
            syncMovieStatusAfterShowTimeChanged(movie.getMovieId());
            if (!oldMovieId.equals(movie.getMovieId())) {
                syncMovieStatusAfterShowTimeChanged(oldMovieId);
            }
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
        Integer movieId = showTimeEntity.getMovie().getMovieId();
        showTimeEntity.setStatus(ShowTimeStatus.DELETED);
        showTimeRepository.save(showTimeEntity);
        syncMovieStatusAfterShowTimeChanged(movieId);
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
            throw new ConflictException("Movie is not available !");
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
    public List<ShowTimeResponse> findOccupiedSlots(Integer cinemaTheaterId, Date showDate) {
        return List.of();
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
                                .movieVariation(showTime.getMovieVariation())
                                .build();

                }).toList();
        return showTimeDetailResponses.isEmpty() ? null : showTimeDetailResponses;
    }

    /**
     * Find all seat by showtime id
     * @param showTimeId cinema theater id
     * @return showtime response
     */
    @Override
    public Long findCountByShowTimeId(Long showTimeId) {
        return showTimeRepository.countSeatByShowTimeId(showTimeId, ShowTimeStatus.VALID);
    }

    /**
     * Find seat map by showtime id
     * @param id showtime id
     * @param cinemaTheaterId cinema theater id
     * @return SeatMapResponse
     */
    @Override
    public SeatMapResponse findSeatMapByShowTimeIdAndCinemaTheaterId(Long id, Integer cinemaTheaterId) {
//        CinemaTheaterEntity cinemaTheater = cinemaTheaterRepository.findByStatusNotAndCinemaTheaterId(CinemaTheaterStatus.INVALID, cinemaTheaterId)
//                .orElseThrow(
//                () ->  new DataNotFoundException("Cinema Theater Not Found With Id: " + cinemaTheaterId));
//
//        List<SeatEntity> seats = cinemaTheater.getSeats();
//        return SeatMapResponse.builder()
//                .seats(seats)
//                .cinemaTheaterId(cinemaTheaterId)
//                .numberOfColumn(cinemaTheater.getNumberOfColumns())
//                .numberOfRows(cinemaTheater.getNumberOfRows())
//                .doubleSeatRow(cinemaTheater.getDoubleSeatRow())
//                .vipSeatRow(cinemaTheater.getVipSeatRow())
//                .regularSeatRow(cinemaTheater.getRegularSeatRow())
//                .status(cinemaTheater.getStatus())
//                .build();
//        return null;
        return null;
    }

    /**
     * Lấy tất cả ngày chiếu của rạp chiếu có id
     * @param cinemaTheaterId id rạp chiếu
     * @return list ngày chiếu
     */
    @Override
    public List<Date> findAllShowDateByCinemaTheaterIdInFeatured(Integer cinemaTheaterId) {
        List<Date> showDates = showTimeRepository
                .findAllShowDateByCinemaTheaterIdAndStatusInFeatured(cinemaTheaterId, ShowTimeStatus.VALID, Sort.by(Sort.Direction.ASC, "showDate"));
        if (showDates.isEmpty()) {
            log.info("No featured show dates found for cinema theater id {}, fallback to movie theater id", cinemaTheaterId);
            showDates = showTimeRepository
                    .findAllShowDateByMovieTheaterIdAndStatusInFeatured(cinemaTheaterId, ShowTimeStatus.VALID, Sort.by(Sort.Direction.ASC, "showDate"));
        }
        return showDates.isEmpty() ? null : showDates;
    }

    /**
     * Lấy tất cả
     * @param cinemaTheaterId id rạp chiếu
     * @param showDate ngày chiếu
     * @return list phim
     */
    @Override
    public List<MovieResponse> findAllMovieByCinemaTheaterIdAndShowDate(Integer cinemaTheaterId, Date showDate, MoviePageQueryRequest request) {

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<MovieEntity> page = movieRepository.findAllMovieByCinemaTheaterIdAndShowDate(cinemaTheaterId,ShowTimeStatus.VALID, showDate, pageable);
        if (page.isEmpty()) {
            log.info("No movies found for cinema theater id {} on {}, fallback to movie theater id", cinemaTheaterId, showDate);
            page = movieRepository.findAllMovieByMovieTheaterIdAndShowDate(cinemaTheaterId, ShowTimeStatus.VALID, showDate, pageable);
        }
        return movieService.movieToMoviePageableResponse(page).getMovies();
    }

    @Override
    public List<ShowTimeDetailResponse> findAllByFilter(ShowTimeRequestNew request) {
        Sort sort = Sort.by(Sort.Order.desc("showDate"), Sort.Order.asc("startTime"));
        Specification<ShowTimeEntity> specification = Specification.where(null);

        if (request.getMovieTheaterId() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(
                            root.join("cinemaTheater").join("movieTheater").get("movieTheaterId"),
                            request.getMovieTheaterId().intValue()
                    ));
        }

        if (request.getShowDate() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("showDate"), request.getShowDate()));
        }

        if (request.getShowTimeStatus() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), request.getShowTimeStatus()));
        } else {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.notEqual(root.get("status"), ShowTimeStatus.DELETED));
        }

        if (request.getSpecial() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("special"), request.getSpecial()));
        }

        List<ShowTimeEntity> showTimes = showTimeRepository.findAll(specification, sort);
        List<ShowTimeDetailResponse> showTimeDetailResponses = showTimes.stream()
                .map(showTime -> {
                    return ShowTimeDetailResponse.builder()
                            .showTime(showTime)
                            .totalSeatEmpty(this.findCountByShowTimeId(showTime.getId()).intValue())
                            .movie(showTime.getMovie())
                            .cinemaTheater(showTime.getCinemaTheater())
                            .movieVariation(showTime.getMovieVariation())
                            .build();
                }).toList();
        return showTimeDetailResponses.isEmpty() ? null : showTimeDetailResponses;
    }

    private ShowTimeDetailResponseNew toShowTimeDetailResponseNew (List<ShowTimeEntity> showTime) {
        return ShowTimeDetailResponseNew.builder()
                .showTimes(showTime)
                .movieTheater(showTime.get(0).getCinemaTheater().getMovieTheater())
                .cinemaTheater(showTime.get(0).getCinemaTheater())
                .movie(showTime.get(0).getMovie())
                .build();
    }

    /**
     * Convert ShowTimeEntity to ShowTimeResponse
     *
     * @param showTimeEntity ShowTimeEntity
     * @return ShowTimeResponse
     */
    private ShowTimeResponse convertToShowTimeResponse(ShowTimeEntity showTimeEntity) {
        MovieResponse movieResponse = movieService.findById(showTimeEntity.getMovie().getMovieId());
        return ShowTimeResponse.builder()
                .id(showTimeEntity.getId())
                .showDate(showTimeEntity.getShowDate())
                .startTime(showTimeEntity.getStartTime())
                .endTime(showTimeEntity.getEndTime())
                .status(showTimeEntity.getStatus())
                .special(Boolean.TRUE.equals(showTimeEntity.getSpecial()))
                .originPrice(showTimeEntity.getOriginPrice())
                .movie(movieResponse)
                .cinemaTheater(showTimeEntity.getCinemaTheater())
                .build();
    }

    private LocalTime checkShowTime(ShowTimeRequest request, MovieEntity movie, CinemaTheaterEntity cinemaTheater, Long... showTimeId) {


        LocalTime endTime = request.getStartTime().plusMinutes(movie.getDuration());
        /* Kiểm tra giờ hành chính của rạp chiếu*/
        if (request.getStartTime().isBefore(MovieTheaterOfficeHours.OPENING_HOURS) || endTime.isAfter(MovieTheaterOfficeHours.CLOSING_HOURS)) {
            log.info("Showtime must be between {} and {}", MovieTheaterOfficeHours.OPENING_HOURS, MovieTheaterOfficeHours.CLOSING_HOURS);
            throw new ConflictException("Showtime must be between " + MovieTheaterOfficeHours.OPENING_HOURS + " and " + MovieTheaterOfficeHours.CLOSING_HOURS);
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
                throw new ConflictException("Showtime already exists at this time!");
            }
        }
        return endTime;
    }

    private void syncMovieStatusAfterShowTimeChanged(Integer movieId) {
        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new DataNotFoundException("Movie Not Found With Id: " + movieId));

        if (MovieStatus.MOVIE_STATUS_CNS.equals(movie.getStatus().getStatusId())) {
            return;
        }

        List<ShowTimeEntity> showTimes = showTimeRepository.findAllByMovieAndStatusNot(movie, ShowTimeStatus.DELETED);

        String targetStatusId = hasAvailableShowTime(showTimes)
                ? MovieStatus.MOVIE_STATUS_DC
                : isMovieEnded(movie)
                ? MovieStatus.MOVIE_STATUS_NC
                : MovieStatus.MOVIE_STATUS_SC;

        if (targetStatusId.equals(movie.getStatus().getStatusId())) {
            return;
        }

        movie.setStatus(movieStatusService.findById(targetStatusId));
    }

    private boolean hasAvailableShowTime(List<ShowTimeEntity> showTimes) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return showTimes.stream()
                .filter(showTime -> showTime.getStatus() == ShowTimeStatus.VALID)
                .anyMatch(showTime -> {
                    LocalDate showDate = toLocalDate(showTime.getShowDate());
                    if (showDate.isAfter(today)) {
                        return true;
                    }
                    if (showDate.isBefore(today)) {
                        return false;
                    }
                    return showTime.getEndTime() == null || showTime.getEndTime().isAfter(now);
                });
    }

    private boolean isMovieEnded(MovieEntity movie) {
        return toLocalDate(movie.getEndDate()).isBefore(LocalDate.now());
    }

    private LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return new java.sql.Date(date.getTime()).toLocalDate();
    }
}
