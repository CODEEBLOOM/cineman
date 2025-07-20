package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.GenresRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.GenresEntity;
import com.codebloom.cineman.repository.GenresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {

    private GenresRepository genresRepository;
    private ModelMapper modelMapper;
    private GenreServiceImple genreService;

    @BeforeEach
    void setUp() {
        genresRepository = mock(GenresRepository.class);
        modelMapper = new ModelMapper();
        genreService = new GenreServiceImple(genresRepository, modelMapper);
    }

    @Test
    @DisplayName("create() - should save new genre when name is unique")
    void create_ShouldSaveNewGenre_WhenNameIsUnique() {
        GenresRequest request = new GenresRequest();
        request.setName("Action");
        request.setDescription("Action genre");

        when(genresRepository.findByName("Action")).thenReturn(Optional.empty());

        GenresEntity saved = GenresEntity.builder()
                .genresId(1)
                .name("Action")
                .description("Action genre")
                .active(true)
                .build();

        when(genresRepository.save(any())).thenReturn(saved);

        GenresEntity result = genreService.create(request);

        assertThat(result.getGenresId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Action");
        assertThat(result.getDescription()).isEqualTo("Action genre");
        assertThat(result.getActive()).isTrue();
    }

    @Test
    @DisplayName("create() - should throw when name already exists")
    void create_ShouldThrow_WhenNameExists() {
        GenresRequest request = new GenresRequest();
        request.setName("Horror");

        when(genresRepository.findByName("Horror")).thenReturn(Optional.of(
                GenresEntity.builder().genresId(2).name("Horror").active(true).build()
        ));

        assertThatThrownBy(() -> genreService.create(request))
                .isInstanceOf(DataExistingException.class)
                .hasMessageContaining("already exists");

        verify(genresRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() - should update genre when exists")
    void update_ShouldUpdateGenre_WhenExists() {
        int id = 3;
        GenresRequest request = new GenresRequest();
        request.setName("Drama");
        request.setDescription("Updated desc");

        GenresEntity existing = GenresEntity.builder()
                .genresId(id)
                .name("OldName")
                .description("Old desc")
                .active(true)
                .build();

        when(genresRepository.findById(id)).thenReturn(Optional.of(existing));
        when(genresRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GenresEntity result = genreService.update(id, request);

        assertThat(result.getGenresId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Drama");
        assertThat(result.getDescription()).isEqualTo("Updated desc");
    }

    @Test
    @DisplayName("update() - should throw when genre not found")
    void update_ShouldThrow_WhenGenreNotFound() {
        when(genresRepository.findById(99)).thenReturn(Optional.empty());

        GenresRequest request = new GenresRequest();
        request.setName("Any");

        assertThatThrownBy(() -> genreService.update(99, request))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("delete() - should set active=false when genre exists")
    void delete_ShouldDeactivate_WhenExists() {
        GenresEntity genre = GenresEntity.builder()
                .genresId(10)
                .name("Romance")
                .description("Love story")
                .active(true)
                .build();

        when(genresRepository.findById(10)).thenReturn(Optional.of(genre));
        when(genresRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        genreService.delete(10);

        assertThat(genre.getActive()).isFalse();
        verify(genresRepository).save(genre);
    }

    @Test
    @DisplayName("findById() - should return genre when exists")
    void findById_ShouldReturnGenre_WhenExists() {
        GenresEntity genre = GenresEntity.builder()
                .genresId(20)
                .name("Sci-fi")
                .description("Futuristic content")
                .active(true)
                .build();

        when(genresRepository.findById(20)).thenReturn(Optional.of(genre));

        GenresEntity result = genreService.findById(20);

        assertThat(result).isNotNull();
        assertThat(result.getGenresId()).isEqualTo(20);
        assertThat(result.getName()).isEqualTo("Sci-fi");
    }

    @Test
    @DisplayName("findById() - should throw when not found")
    void findById_ShouldThrow_WhenNotFound() {
        when(genresRepository.findById(100)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.findById(100))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("findAll() - should return list of genres")
    void findAll_ShouldReturnList() {
        List<GenresEntity> genres = List.of(
                GenresEntity.builder().genresId(1).name("Action").active(true).build(),
                GenresEntity.builder().genresId(2).name("Comedy").active(true).build()
        );

        when(genresRepository.findAll()).thenReturn(genres);

        List<GenresEntity> result = genreService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Action");
    }
}
