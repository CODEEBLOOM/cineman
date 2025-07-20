package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MovieStatusRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.repository.MovieStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovieStatusServiceImplTest {

    private MovieStatusRepository movieStatusRepository;
    private MovieStatusServiceImpl movieStatusService;

    @BeforeEach
    void setUp() {
        movieStatusRepository = mock(MovieStatusRepository.class);
        movieStatusService = new MovieStatusServiceImpl(movieStatusRepository);
    }

    @Test
    @DisplayName("Test findAll should return all movie statuses")
    void testFindAll_ShouldReturnList() {
        List<MovieStatusEntity> mockList = Arrays.asList(
                MovieStatusEntity.builder()
                        .statusId("COMING_SOON")
                        .name("Coming Soon")
                        .description("Sắp chiếu")
                        .active(true)
                        .build(),
                MovieStatusEntity.builder()
                        .statusId("NOW_SHOWING")
                        .name("Now Showing")
                        .description("Đang chiếu")
                        .active(true)
                        .build()
        );

        when(movieStatusRepository.findAll()).thenReturn(mockList);

        List<MovieStatusEntity> result = movieStatusService.findAll();
        assertEquals(2, result.size());
        verify(movieStatusRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findById should return MovieStatusEntity when found")
    void testFindById_ShouldReturnEntity() {
        MovieStatusEntity entity = MovieStatusEntity.builder()
                .statusId("UPCOMING")
                .name("Upcoming")
                .description("Sắp tới")
                .active(true)
                .build();

        when(movieStatusRepository.findById("UPCOMING")).thenReturn(Optional.of(entity));

        MovieStatusEntity result = movieStatusService.findById("UPCOMING");

        assertNotNull(result);
        assertEquals("Upcoming", result.getName());
        verify(movieStatusRepository).findById("UPCOMING");
    }

    @Test
    @DisplayName("Test findById should throw exception when not found")
    void testFindById_ShouldThrow_WhenNotFound() {
        when(movieStatusRepository.findById("INVALID")).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> movieStatusService.findById("INVALID"));

        assertEquals("Status of movie not found with ID: INVALID", exception.getMessage());
    }


    @Test
    @DisplayName("Test save should throw DataExistingException if ID exists")
    void testSave_ShouldThrow_WhenIdExists() {
        MovieStatusRequest request = new MovieStatusRequest("ACTIVE", "Active", "Mô tả");
        MovieStatusEntity existing = MovieStatusEntity.builder()
                .statusId("ACTIVE")
                .name("Active")
                .description("Mô tả")
                .active(true)
                .build();

        when(movieStatusRepository.findById("ACTIVE")).thenReturn(Optional.of(existing));

        DataExistingException exception = assertThrows(DataExistingException.class,
                () -> movieStatusService.save(request));

        assertEquals("Movie status already existing with ID: ACTIVE", exception.getMessage());
    }


    @Test
    @DisplayName("Test save should store new MovieStatusEntity")
    void testSave_ShouldCreate_WhenIdNotExists() {
        MovieStatusRequest request = new MovieStatusRequest("NEW", "New", "Mới thêm");

        when(movieStatusRepository.findById("NEW")).thenReturn(Optional.empty());

        MovieStatusEntity saved = MovieStatusEntity.builder()
                .statusId("NEW")
                .name("New")
                .description("Mới thêm")
                .active(true)
                .build();

        when(movieStatusRepository.save(any(MovieStatusEntity.class))).thenReturn(saved);

        MovieStatusEntity result = movieStatusService.save(request);

        assertNotNull(result);
        assertEquals("NEW", result.getStatusId());
        assertEquals("New", result.getName());
        verify(movieStatusRepository).save(any(MovieStatusEntity.class));
    }

    @Test
    @DisplayName("Test update should modify existing MovieStatusEntity")
    void testUpdate_ShouldUpdateEntity() {
        MovieStatusRequest request = new MovieStatusRequest("EDIT", "Edited Name", "Edited Desc");

        MovieStatusEntity existing = MovieStatusEntity.builder()
                .statusId("EDIT")
                .name("Old Name")
                .description("Old Desc")
                .active(true)
                .build();

        when(movieStatusRepository.findById("EDIT")).thenReturn(Optional.of(existing));
        when(movieStatusRepository.save(existing)).thenReturn(existing);

        MovieStatusEntity result = movieStatusService.update(request);

        assertEquals("Edited Name", result.getName());
        assertEquals("Edited Desc", result.getDescription());
        verify(movieStatusRepository).save(existing);
    }

    @Test
    @DisplayName("Test deleteById should perform soft delete")
    void testDeleteById_ShouldSoftDelete() {
        MovieStatusEntity existing = MovieStatusEntity.builder()
                .statusId("DELETE_ME")
                .name("Deleted")
                .description("To be deleted")
                .active(true)
                .build();

        when(movieStatusRepository.findById("DELETE_ME")).thenReturn(Optional.of(existing));

        movieStatusService.deleteById("DELETE_ME");

        assertFalse(existing.getActive());
        verify(movieStatusRepository).delete(existing);
    }
}
