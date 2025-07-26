package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.GenresRequest;
import com.codebloom.cineman.model.GenresEntity;
import com.codebloom.cineman.repository.GenresRepository;
import com.codebloom.cineman.service.impl.GenreServiceImple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenresRepository genresRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private GenreServiceImple genreService;

    private GenresEntity hanhdong;
    private GenresEntity hai;
    private GenresRequest request;

    @BeforeEach
    void setUp() {
        hanhdong = GenresEntity.builder()
                .genresId(1)
                .name("Hành động")
                .description("Phim hành động")
                .active(true)
                .movieGenres(new ArrayList<>())
                .build();

        hai = GenresEntity.builder()
                .genresId(2)
                .name("Hài")
                .description("Phim hài")
                .active(true)
                .movieGenres(new ArrayList<>())
                .build();

        request = new GenresRequest();
        request.setName("Hành động");
        request.setDescription("Phim hành động");
    }

    @Test
    void create() {
        when(genresRepository.findByName("Hành động")).thenReturn(Optional.empty());
        when(modelMapper.map(request, GenresEntity.class)).thenReturn(hanhdong);
        when(genresRepository.save(hanhdong)).thenReturn(hanhdong);

        GenresEntity result = genreService.create(request);

        assertNotNull(result);
        assertEquals("Hành động", result.getName());
        verify(genresRepository).save(hanhdong);
    }

    @Test
    void update() {
        GenresRequest updateRequest = new GenresRequest();
        updateRequest.setName("Hành động mới");
        updateRequest.setDescription("Cập nhật mô tả");

        when(genresRepository.findById(1)).thenReturn(Optional.of(hanhdong));
        doAnswer(invocation -> {
            GenresRequest req = invocation.getArgument(0);
            GenresEntity entity = invocation.getArgument(1);
            entity.setName(req.getName());
            entity.setDescription(req.getDescription());
            return null;
        }).when(modelMapper).map(eq(updateRequest), eq(hanhdong));
        when(genresRepository.save(hanhdong)).thenReturn(hanhdong);

        GenresEntity result = genreService.update(1, updateRequest);

        assertEquals("Hành động mới", result.getName());
        assertEquals("Cập nhật mô tả", result.getDescription());
    }

    @Test
    void delete() {
        when(genresRepository.findById(1)).thenReturn(Optional.of(hanhdong));

        genreService.delete(1);

        assertFalse(hanhdong.getActive());
        verify(genresRepository).save(hanhdong);
    }

    @Test
    void findById() {
        when(genresRepository.findById(1)).thenReturn(Optional.of(hanhdong));

        GenresEntity result = genreService.findById(1);

        assertNotNull(result);
        assertEquals("Hành động", result.getName());
    }

    @Test
    void findAll() {
        when(genresRepository.findAll()).thenReturn(List.of(hanhdong, hai));

        List<GenresEntity> result = genreService.findAll();

        assertEquals(2, result.size());
        assertEquals("Hành động", result.get(0).getName());
        assertEquals("Hài", result.get(1).getName());
    }
}
