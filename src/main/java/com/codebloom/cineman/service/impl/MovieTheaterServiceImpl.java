package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.admin.MovieTheaterProvinceResponse;
import com.codebloom.cineman.controller.request.MovieTheaterRequest;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.controller.response.MovieTheaterPage;
import com.codebloom.cineman.controller.response.MovieTheaterResponse;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.MovieTheaterEntity;
import com.codebloom.cineman.model.ProvinceEntity;
import com.codebloom.cineman.repository.MovieTheaterRepository;
import com.codebloom.cineman.service.MovieTheaterService;
import com.codebloom.cineman.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "MOVIE-THEATER-SERVICE")
public class MovieTheaterServiceImpl implements MovieTheaterService {

    private final MovieTheaterRepository movieTheaterRepository;
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
    public MovieTheaterResponse findById(Integer id) {
        MovieTheaterEntity movieTheater = movieTheaterRepository.findByMovieTheaterIdAndStatus(id, true)
                .orElseThrow(() -> new DataNotFoundException("Movie theater not found with id: " + id));
        return convert(movieTheater);
    }

    @Override
    public MovieTheaterResponse save(MovieTheaterRequest movie) {
        movieTheaterRepository.findByHotline(movie.getHotline())
                .ifPresent(movieTheaterEntity -> {
                    throw new DataExistingException("Movie theater already exists with id: " + movie.getHotline());
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
    public MovieTheaterResponse update(Integer id, MovieTheaterRequest movie) {
        movieTheaterRepository.findByHotlineAndMovieTheaterIdNot(movie.getHotline(), id)
                .ifPresent(movieTheaterEntity -> {
                    throw new DataExistingException("Movie theater already exists with hotline: " + movie.getHotline());
                });

        MovieTheaterEntity movieTheaterEntity = modelMapper.map(movie, MovieTheaterEntity.class);
        ProvinceEntity province = provinceService.findById(movie.getProvinceId());
        movieTheaterEntity.setMovieTheaterId(id);
        movieTheaterEntity.setProvince(province);
        movieTheaterEntity.setStatus(true);
        return convert(movieTheaterRepository.save(movieTheaterEntity));
    }

    @Override
    public void delete(Integer id) {
        MovieTheaterEntity theater = movieTheaterRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Movie theater not found with id: " + id));
        theater.setStatus(false);
        movieTheaterRepository.save(theater);
    }


    private MovieTheaterResponse convert(MovieTheaterEntity theater) {
        return MovieTheaterResponse.builder()
                .movieTheaterId(theater.getMovieTheaterId())
                .name(theater.getName())
                .province(theater.getProvince())
                .status(theater.getStatus())
                .address(theater.getAddress())
                .hotline(theater.getHotline())
                .iframeCode(theater.getIframeCode())
                .province(theater.getProvince())
                .cinemaTheaters(theater.getCinemaTheaters())
                .build();
    }
}
