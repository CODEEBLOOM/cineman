package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.MovieTheaterEntity;
import com.codebloom.cineman.model.ProvinceEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.MovieTheaterRepository;
import com.codebloom.cineman.repository.ProvinceRepository;
import com.codebloom.cineman.repository.ShowTimeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProvinceServiceImplTest {

    @Mock
    private ProvinceRepository provinceRepository;

    @Mock
    private MovieTheaterRepository movieTheaterRepository;

    @Mock
    private CinemaTheatersRepository cinemaTheatersRepository;

    @Mock
    private ShowTimeRepository showTimeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProvinceServiceImpl provinceService;

    @Test
    void findByIdShouldIgnoreInactiveProvince() {
        when(provinceRepository.findByIdAndActive(1, true)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> provinceService.findById(1));
    }

    @Test
    void deleteShouldSoftDeleteProvinceHierarchy() {
        ProvinceEntity province = ProvinceEntity.builder().id(1).active(true).build();
        MovieTheaterEntity movieTheater = MovieTheaterEntity.builder().movieTheaterId(2).status(true).build();
        CinemaTheaterEntity cinemaTheater = CinemaTheaterEntity.builder()
                .cinemaTheaterId(3)
                .status(CinemaTheaterStatus.PUBLISHED)
                .build();
        ShowTimeEntity showTime = ShowTimeEntity.builder().id(4L).status(ShowTimeStatus.VALID).build();

        when(provinceRepository.findByIdAndActive(1, true)).thenReturn(Optional.of(province));
        when(movieTheaterRepository.findAllByStatusAndProvince_Id(true, 1)).thenReturn(List.of(movieTheater));
        when(cinemaTheatersRepository.findAllByStatusNotAndMovieTheater_MovieTheaterId(CinemaTheaterStatus.INVALID, 2))
                .thenReturn(List.of(cinemaTheater));
        when(showTimeRepository.findAllByCinemaTheaterInAndStatusNot(anyList(), org.mockito.ArgumentMatchers.eq(ShowTimeStatus.DELETED)))
                .thenReturn(List.of(showTime));

        provinceService.delete(1);

        assertEquals(false, province.getActive());
        assertEquals(false, movieTheater.getStatus());
        assertEquals(CinemaTheaterStatus.INVALID, cinemaTheater.getStatus());
        assertEquals(ShowTimeStatus.DELETED, showTime.getStatus());
        verify(showTimeRepository).saveAll(List.of(showTime));
        verify(cinemaTheatersRepository).saveAll(List.of(cinemaTheater));
        verify(movieTheaterRepository).saveAll(List.of(movieTheater));
        verify(provinceRepository).save(province);
    }
}
