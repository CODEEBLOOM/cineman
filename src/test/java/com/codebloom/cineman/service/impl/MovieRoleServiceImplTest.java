package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MovieRoleRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.MovieRoleEntity;
import com.codebloom.cineman.repository.MovieParticipantRepository;
import com.codebloom.cineman.repository.MovieRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieRoleServiceImplTest {

    private MovieRoleRepository movieRoleRepository;
    private MovieParticipantRepository movieParticipantRepository;
    private ModelMapper modelMapper;
    private MovieRoleServiceImpl movieRoleService;

    @BeforeEach
    void setUp() {
        movieRoleRepository = mock(MovieRoleRepository.class);
        movieParticipantRepository = mock(MovieParticipantRepository.class);
        modelMapper = mock(ModelMapper.class);
        movieRoleService = new MovieRoleServiceImpl(movieRoleRepository, movieParticipantRepository, modelMapper);
    }

    @Test
    void testFindAll_ShouldReturnAllMovieRoles() {
        MovieRoleEntity role1 = new MovieRoleEntity();
        role1.setMovieRoleId(1);
        role1.setName("Director");

        MovieRoleEntity role2 = new MovieRoleEntity();
        role2.setMovieRoleId(2);
        role2.setName("Actor");

        when(movieRoleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        List<MovieRoleEntity> result = movieRoleService.findAll();

        assertEquals(2, result.size());
        verify(movieRoleRepository, times(1)).findAll();
    }

    @Test
    void testFindById_ShouldReturnRole_WhenFound() {
        MovieRoleEntity role = new MovieRoleEntity();
        role.setMovieRoleId(1);
        role.setName("Director");
        role.setDescription("Vision");
        role.setActive(true);

        when(movieRoleRepository.findById(1)).thenReturn(Optional.of(role));

        MovieRoleEntity result = movieRoleService.findById(1);

        assertEquals("Director", result.getName());
        assertTrue(result.getActive());
    }

    @Test
    void testFindById_ShouldThrowException_WhenNotFound() {
        when(movieRoleRepository.findById(999)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () ->
                movieRoleService.findById(999));

        assertEquals("Movie Role Not Found with id: 999", exception.getMessage());
    }


    @Test
    void testCreate_ShouldSaveRole_WhenNameNotExist() {
        MovieRoleRequest request = new MovieRoleRequest();
        request.setName("Writer");
        request.setDescription("Writes script");

        when(movieRoleRepository.findByName("Writer")).thenReturn(Optional.empty());

        MovieRoleEntity toSave = new MovieRoleEntity();
        toSave.setName("Writer");
        toSave.setDescription("Writes script");

        MovieRoleEntity saved = new MovieRoleEntity();
        saved.setMovieRoleId(5);
        saved.setName("Writer");
        saved.setDescription("Writes script");
        saved.setActive(true);

        when(modelMapper.map(request, MovieRoleEntity.class)).thenReturn(toSave);
        when(movieRoleRepository.save(toSave)).thenReturn(saved);

        MovieRoleEntity result = movieRoleService.create(request);

        assertEquals("Writer", result.getName());
        assertTrue(result.getActive());
        assertEquals(5, result.getMovieRoleId());
    }

    @Test
    void testCreate_ShouldThrowException_WhenNameAlreadyExists() {
        MovieRoleRequest request = new MovieRoleRequest();
        request.setName("Actor");

        MovieRoleEntity existing = new MovieRoleEntity();
        existing.setMovieRoleId(2);
        existing.setName("Actor");

        when(movieRoleRepository.findByName("Actor")).thenReturn(Optional.of(existing));

        DataExistingException exception = assertThrows(DataExistingException.class, () ->
                movieRoleService.create(request));

        assertEquals("Movie Role already exist with name: Actor", exception.getMessage());
    }


    @Test
    void testUpdate_ShouldUpdateRole_WhenFound() {
        MovieRoleRequest request = new MovieRoleRequest();
        request.setName("Producer");
        request.setDescription("Finances the film");

        MovieRoleEntity existing = new MovieRoleEntity();
        existing.setMovieRoleId(3);
        existing.setName("Old");
        existing.setDescription("Old Desc");
        existing.setActive(true);

        when(movieRoleRepository.findById(3)).thenReturn(Optional.of(existing));
        when(movieRoleRepository.save(existing)).thenReturn(existing);

        MovieRoleEntity result = movieRoleService.update(3, request);

        assertEquals("Producer", result.getName());
        assertEquals("Finances the film", result.getDescription());
    }

    @Test
    void testDelete_ShouldSetActiveFalse_WhenFound() {
        MovieRoleEntity existing = new MovieRoleEntity();
        existing.setMovieRoleId(10);
        existing.setName("Director");
        existing.setActive(true);

        when(movieRoleRepository.findById(10)).thenReturn(Optional.of(existing));
        when(movieRoleRepository.save(existing)).thenReturn(existing);

        movieRoleService.delete(10);

        assertFalse(existing.getActive());
        verify(movieRoleRepository).save(existing);
    }
}
