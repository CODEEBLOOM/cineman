package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MovieTheaterRequest;
import com.codebloom.cineman.controller.request.PageRequest;
import com.codebloom.cineman.controller.response.MovieTheaterPage;
import com.codebloom.cineman.controller.response.MovieTheaterResponse;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.MovieTheaterEntity;
import com.codebloom.cineman.model.ProvinceEntity;
import com.codebloom.cineman.repository.MovieTheaterRepository;
import com.codebloom.cineman.service.ProvinceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieTheaterServiceImplTest {

    private MovieTheaterRepository movieTheaterRepository;
    private ProvinceService provinceService;
    private ModelMapper modelMapper;
    private MovieTheaterServiceImpl service;

    @BeforeEach
    void setUp() {
        movieTheaterRepository = mock(MovieTheaterRepository.class);
        provinceService = mock(ProvinceService.class);
        modelMapper = new ModelMapper();
        service = new MovieTheaterServiceImpl(movieTheaterRepository, provinceService, modelMapper);
    }

    @Test
    @DisplayName("findAllByPage - Should return list of movie theaters")
    void testFindAllByPage_ShouldReturnMovieTheaterPage() {
        PageRequest request = new PageRequest();
        MovieTheaterEntity entity = MovieTheaterEntity.builder().movieTheaterId(1).name("CGV").status(true).build();
        Page<MovieTheaterEntity> page = new PageImpl<>(List.of(entity));

        when(movieTheaterRepository.findAllByStatus(eq(true), any(Pageable.class)))
                .thenReturn(page);

        MovieTheaterPage result = service.findAllByPage(request);

        assertEquals(1, result.getMovieTheaters().size());
        assertEquals("CGV", result.getMovieTheaters().get(0).getName());
    }

    @Test
    @DisplayName("findById - Should return movie theater when found")
    void testFindById_ShouldReturnMovieTheaterResponse() {
        MovieTheaterEntity entity = MovieTheaterEntity.builder().movieTheaterId(1).name("BHD").status(true).build();

        when(movieTheaterRepository.findByMovieTheaterIdAndStatus(1, true)).thenReturn(Optional.of(entity));

        MovieTheaterResponse response = service.findById(1);

        assertEquals("BHD", response.getName());
    }

    @Test
    @DisplayName("findById - Should throw DataNotFoundException when not found")
    void testFindById_ShouldThrowException_WhenNotFound() {
        when(movieTheaterRepository.findByMovieTheaterIdAndStatus(99, true)).thenReturn(Optional.empty());

        DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> service.findById(99));
        assertEquals("Movie theater not found with id: 99", ex.getMessage());
    }

    @Test
    @DisplayName("save - Should save and return response")
    void testSave_ShouldReturnSavedMovieTheaterResponse() {
        MovieTheaterRequest request = new MovieTheaterRequest();
        request.setName("Beta");
        request.setHotline("0909");
        request.setProvinceId(1);

        ProvinceEntity province = ProvinceEntity.builder().id(1).name("HCM").build();

        when(movieTheaterRepository.findByHotline("0909")).thenReturn(Optional.empty());
        when(provinceService.findById(1)).thenReturn(province);
        when(movieTheaterRepository.save(any(MovieTheaterEntity.class)))
                .thenAnswer(invocation -> {
                    MovieTheaterEntity saved = invocation.getArgument(0);
                    saved.setMovieTheaterId(123);
                    return saved;
                });

        MovieTheaterResponse result = service.save(request);

        assertEquals("Beta", result.getName());
        assertEquals("0909", result.getHotline());
    }

    @Test
    @DisplayName("save - Should throw DataExistingException when hotline exists")
    void testSave_ShouldThrowException_WhenHotlineExists() {
        MovieTheaterRequest request = new MovieTheaterRequest();
        request.setHotline("0909");

        when(movieTheaterRepository.findByHotline("0909")).thenReturn(Optional.of(new MovieTheaterEntity()));

        DataExistingException ex = assertThrows(DataExistingException.class, () -> service.save(request));
        assertEquals("Movie theater already exists with id: 0909", ex.getMessage());
    }

    @Test
    @DisplayName("update - Should return updated response")
    void testUpdate_ShouldReturnUpdatedResponse() {
        MovieTheaterRequest request = new MovieTheaterRequest();
        request.setHotline("0909");
        request.setProvinceId(1);
        request.setName("Galaxy");

        ProvinceEntity province = ProvinceEntity.builder().id(1).name("HN").build();

        when(movieTheaterRepository.findByHotlineAndMovieTheaterIdNot("0909", 2)).thenReturn(Optional.empty());
        when(provinceService.findById(1)).thenReturn(province);
        when(movieTheaterRepository.save(any(MovieTheaterEntity.class)))
                .thenAnswer(i -> i.getArgument(0));

        MovieTheaterResponse result = service.update(2, request);

        assertEquals("Galaxy", result.getName());
    }

    @Test
    @DisplayName("update - Should throw DataExistingException when hotline exists on other ID")
    void testUpdate_ShouldThrowException_WhenHotlineExistsOnOtherId() {
        MovieTheaterRequest request = new MovieTheaterRequest();
        request.setHotline("0909");

        when(movieTheaterRepository.findByHotlineAndMovieTheaterIdNot("0909", 2))
                .thenReturn(Optional.of(new MovieTheaterEntity()));

        DataExistingException ex = assertThrows(DataExistingException.class, () -> service.update(2, request));
        assertEquals("Movie theater already exists with hotline: 0909", ex.getMessage());
    }

    @Test
    @DisplayName("delete - Should set status to false")
    void testDelete_ShouldMarkMovieTheaterInactive() {
        MovieTheaterEntity entity = MovieTheaterEntity.builder().movieTheaterId(1).status(true).build();

        when(movieTheaterRepository.findById(1)).thenReturn(Optional.of(entity));

        service.delete(1);

        assertFalse(entity.getStatus());
        verify(movieTheaterRepository).save(entity);
    }

    @Test
    @DisplayName("delete - Should throw DataNotFoundException when not found")
    void testDelete_ShouldThrowException_WhenNotFound() {
        when(movieTheaterRepository.findById(1)).thenReturn(Optional.empty());

        DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> service.delete(1));
        assertEquals("Movie theater not found with id: 1", ex.getMessage());
    }
}
