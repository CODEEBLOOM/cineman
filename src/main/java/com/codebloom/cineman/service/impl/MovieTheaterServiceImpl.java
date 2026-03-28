package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.controller.request.MovieTheaterRequest;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.controller.response.MovieTheaterPage;
import com.codebloom.cineman.controller.response.MovieTheaterResponse;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.MovieTheaterEntity;
import com.codebloom.cineman.model.ProvinceEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.MovieTheaterRepository;
import com.codebloom.cineman.repository.ShowTimeRepository;
import com.codebloom.cineman.service.MovieTheaterService;
import com.codebloom.cineman.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "MOVIE-THEATER-SERVICE")
public class MovieTheaterServiceImpl implements MovieTheaterService {

    private final MovieTheaterRepository movieTheaterRepository;
    private final CinemaTheatersRepository cinemaTheatersRepository;
    private final ShowTimeRepository showTimeRepository;
    private final ProvinceService provinceService;
    private final ModelMapper modelMapper;

    @Override
    public MovieTheaterPage findAllByPage(PageRequest pageRequest) {
        Page<MovieTheaterEntity> page = movieTheaterRepository.findAllByStatus(true, org.springframework.data.domain.PageRequest.of(pageRequest.getPage(), pageRequest.getSize()));
        List<MovieTheaterResponse> list = page.getContent()
                .stream()
                .map((this::convert))
                .toList();
        MetaResponse meta = MetaResponse.builder()
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .currentPage(page.getNumber())
                .build();
        return MovieTheaterPage.builder()
                .movieTheaters(list)
                .meta(meta)
                .build();

    }

    @Override
    public List<MovieTheaterResponse> findAllByProvinceId(Integer provinceId) {
        ProvinceEntity province = provinceService.findById(provinceId);
        return movieTheaterRepository.findAllByStatusAndProvince_Id(true, province.getId())
                .stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public MovieTheaterResponse findById(Integer id) {
        MovieTheaterEntity movieTheater = movieTheaterRepository.findByMovieTheaterIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Movie theater not found with id: " + id));
        return convert(movieTheater);
    }

    @Override
    @Transactional
    public MovieTheaterResponse save(MovieTheaterRequest movie) {
        movieTheaterRepository.findByHotline(movie.getHotline())
                .ifPresent(movieTheaterEntity -> {
                    throw new DataExistingException("Movie theater already exists with hotline: " + movie.getHotline());
                });

        MovieTheaterEntity movieTheaterEntity = modelMapper.map(movie, MovieTheaterEntity.class);
        ProvinceEntity province = provinceService.findById(movie.getProvinceId());
        movieTheaterEntity.setMovieTheaterId(null);
        movieTheaterEntity.setProvince(province);
        movieTheaterEntity.setStatus(true);
        movieTheaterRepository.save(movieTheaterEntity);
        return convert(movieTheaterEntity);
    }

    @Override
    @Transactional
    public MovieTheaterResponse update(Integer id, MovieTheaterRequest movie) {
        movieTheaterRepository.findByHotlineAndMovieTheaterIdNot(movie.getHotline(), id)
                .ifPresent(movieTheaterEntity -> {
                    throw new DataExistingException("Movie theater already exists with hotline: " + movie.getHotline());
                });

        MovieTheaterEntity movieTheaterEntity = movieTheaterRepository.findByMovieTheaterIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Movie theater not found with id: " + id));
        ProvinceEntity province = provinceService.findById(movie.getProvinceId());
        modelMapper.map(movie, movieTheaterEntity);
        movieTheaterEntity.setProvince(province);
        movieTheaterEntity.setStatus(true);
        return convert(movieTheaterRepository.save(movieTheaterEntity));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        MovieTheaterEntity theater = movieTheaterRepository.findByMovieTheaterIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Movie theater not found with id: " + id));
        List<CinemaTheaterEntity> cinemaTheaters = cinemaTheatersRepository
                .findAllByStatusNotAndMovieTheater_MovieTheaterId(CinemaTheaterStatus.INVALID, id);
        softDeleteCinemaTheaters(cinemaTheaters);
        theater.setStatus(false);
        movieTheaterRepository.save(theater);
    }


    private MovieTheaterResponse convert(MovieTheaterEntity theater) {
        List<CinemaTheaterEntity> activeCinemaTheaters = theater.getCinemaTheaters() == null
                ? List.of()
                : theater.getCinemaTheaters().stream()
                .filter(cinemaTheater -> cinemaTheater.getStatus() != CinemaTheaterStatus.INVALID)
                .toList();
        return MovieTheaterResponse.builder()
                .movieTheaterId(theater.getMovieTheaterId())
                .name(theater.getName())
                .province(theater.getProvince())
                .status(theater.getStatus())
                .address(theater.getAddress())
                .numbersOfCinemaTheater(activeCinemaTheaters.size())
                .hotline(theater.getHotline())
                .iframeCode(theater.getIframeCode())
                .cinemaTheaters(activeCinemaTheaters)
                .build();
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
