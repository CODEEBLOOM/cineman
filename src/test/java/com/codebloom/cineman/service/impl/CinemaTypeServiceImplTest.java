package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.controller.request.CinemaTypeRequest;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.CinemaTypeEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.CinemaTypeRepository;
import com.codebloom.cineman.repository.ShowTimeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CinemaTypeServiceImplTest {

    @Mock
    private CinemaTypeRepository cinemaTypeRepository;

    @Mock
    private CinemaTheatersRepository cinemaTheatersRepository;

    @Mock
    private ShowTimeRepository showTimeRepository;

    @InjectMocks
    private CinemaTypeServiceImpl cinemaTypeService;

    @Test
    void updateShouldChangeExistingCinemaType() {
        CinemaTypeEntity cinemaType = CinemaTypeEntity.builder()
                .cinemaTypeId(1)
                .code("2D")
                .name("2D")
                .description("old")
                .status(true)
                .build();
        CinemaTypeRequest request = new CinemaTypeRequest();
        request.setCode("IMAX");
        request.setName("IMAX");
        request.setDescription("premium");

        when(cinemaTypeRepository.findByCodeAndStatusAndCinemaTypeIdNot("IMAX", true, 1)).thenReturn(Optional.empty());
        when(cinemaTypeRepository.findByCinemaTypeIdAndStatus(1, true)).thenReturn(Optional.of(cinemaType));
        when(cinemaTypeRepository.save(cinemaType)).thenReturn(cinemaType);

        CinemaTypeEntity response = cinemaTypeService.update(1, request);

        assertEquals("IMAX", response.getCode());
        assertEquals("IMAX", response.getName());
        assertEquals("premium", response.getDescription());
        assertEquals(true, response.getStatus());
    }

    @Test
    void deleteShouldSoftDeleteDependentData() {
        CinemaTypeEntity cinemaType = CinemaTypeEntity.builder().cinemaTypeId(1).status(true).build();
        CinemaTheaterEntity cinemaTheater = CinemaTheaterEntity.builder()
                .cinemaTheaterId(2)
                .status(CinemaTheaterStatus.PUBLISHED)
                .build();
        ShowTimeEntity showTime = ShowTimeEntity.builder().id(3L).status(ShowTimeStatus.VALID).build();

        when(cinemaTypeRepository.findByCinemaTypeIdAndStatus(1, true)).thenReturn(Optional.of(cinemaType));
        when(cinemaTheatersRepository.findAllByStatusNotAndCinemaType_CinemaTypeId(CinemaTheaterStatus.INVALID, 1))
                .thenReturn(List.of(cinemaTheater));
        when(showTimeRepository.findAllByCinemaTheaterInAndStatusNot(anyList(), eq(ShowTimeStatus.DELETED)))
                .thenReturn(List.of(showTime));

        cinemaTypeService.delete(1);

        assertEquals(false, cinemaType.getStatus());
        assertEquals(CinemaTheaterStatus.INVALID, cinemaTheater.getStatus());
        assertEquals(ShowTimeStatus.DELETED, showTime.getStatus());
        verify(showTimeRepository).saveAll(List.of(showTime));
        verify(cinemaTheatersRepository).saveAll(List.of(cinemaTheater));
        verify(cinemaTypeRepository).save(cinemaType);
    }
}
