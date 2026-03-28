package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
import com.codebloom.cineman.common.enums.ShowTimeStatus;
import com.codebloom.cineman.controller.request.MovieTheaterRequest;
import com.codebloom.cineman.controller.response.MovieTheaterResponse;
import com.codebloom.cineman.model.CinemaTheaterEntity;
import com.codebloom.cineman.model.MovieTheaterEntity;
import com.codebloom.cineman.model.ProvinceEntity;
import com.codebloom.cineman.model.ShowTimeEntity;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.MovieTheaterRepository;
import com.codebloom.cineman.repository.ShowTimeRepository;
import com.codebloom.cineman.service.ProvinceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieTheaterServiceImplTest {

    @Mock
    private MovieTheaterRepository movieTheaterRepository;

    @Mock
    private CinemaTheatersRepository cinemaTheatersRepository;

    @Mock
    private ShowTimeRepository showTimeRepository;

    @Mock
    private ProvinceService provinceService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MovieTheaterServiceImpl movieTheaterService;

    @Test
    void updateShouldMutateExistingMovieTheater() {
        MovieTheaterEntity existing = MovieTheaterEntity.builder()
                .movieTheaterId(1)
                .name("Old")
                .status(true)
                .build();
        ProvinceEntity province = ProvinceEntity.builder().id(2).active(true).build();
        MovieTheaterRequest request = new MovieTheaterRequest();
        request.setName("New");
        request.setAddress("123 Street");
        request.setHotline("0123456789");
        request.setIframeCode("iframe");
        request.setProvinceId(2);

        when(movieTheaterRepository.findByHotlineAndMovieTheaterIdNot("0123456789", 1)).thenReturn(Optional.empty());
        when(movieTheaterRepository.findByMovieTheaterIdAndStatus(1, true)).thenReturn(Optional.of(existing));
        when(provinceService.findById(2)).thenReturn(province);
        doAnswer(invocation -> {
            MovieTheaterRequest source = invocation.getArgument(0);
            MovieTheaterEntity target = invocation.getArgument(1);
            target.setName(source.getName());
            target.setAddress(source.getAddress());
            target.setHotline(source.getHotline());
            target.setIframeCode(source.getIframeCode());
            return null;
        }).when(modelMapper).map(any(MovieTheaterRequest.class), any(MovieTheaterEntity.class));
        when(movieTheaterRepository.save(existing)).thenReturn(existing);

        MovieTheaterResponse response = movieTheaterService.update(1, request);

        assertEquals(1, response.getMovieTheaterId());
        assertEquals("New", existing.getName());
        assertEquals("123 Street", existing.getAddress());
        assertEquals("0123456789", existing.getHotline());
        assertEquals("iframe", existing.getIframeCode());
        assertEquals(province, existing.getProvince());
        assertEquals(true, existing.getStatus());
    }

    @Test
    void findAllByProvinceIdShouldReturnActiveMovieTheaters() {
        ProvinceEntity province = ProvinceEntity.builder().id(1).name("Ha Noi").active(true).build();
        CinemaTheaterEntity publishedCinemaTheater = CinemaTheaterEntity.builder()
                .cinemaTheaterId(10)
                .status(CinemaTheaterStatus.PUBLISHED)
                .build();
        CinemaTheaterEntity invalidCinemaTheater = CinemaTheaterEntity.builder()
                .cinemaTheaterId(11)
                .status(CinemaTheaterStatus.INVALID)
                .build();
        MovieTheaterEntity movieTheater = MovieTheaterEntity.builder()
                .movieTheaterId(2)
                .name("Cineman Cau Giay")
                .address("123 Street")
                .hotline("0123456789")
                .iframeCode("iframe")
                .status(true)
                .province(province)
                .cinemaTheaters(List.of(publishedCinemaTheater, invalidCinemaTheater))
                .build();

        when(provinceService.findById(1)).thenReturn(province);
        when(movieTheaterRepository.findAllByStatusAndProvince_Id(true, 1)).thenReturn(List.of(movieTheater));

        List<MovieTheaterResponse> response = movieTheaterService.findAllByProvinceId(1);

        assertEquals(1, response.size());
        assertEquals(2, response.get(0).getMovieTheaterId());
        assertEquals("Cineman Cau Giay", response.get(0).getName());
        assertEquals(1, response.get(0).getNumbersOfCinemaTheater());
        assertEquals(province, response.get(0).getProvince());
    }

    @Test
    void deleteShouldSoftDeleteDependentData() {
        MovieTheaterEntity movieTheater = MovieTheaterEntity.builder().movieTheaterId(1).status(true).build();
        CinemaTheaterEntity cinemaTheater = CinemaTheaterEntity.builder()
                .cinemaTheaterId(2)
                .status(CinemaTheaterStatus.DRAFT)
                .build();
        ShowTimeEntity showTime = ShowTimeEntity.builder().id(3L).status(ShowTimeStatus.VALID).build();

        when(movieTheaterRepository.findByMovieTheaterIdAndStatus(1, true)).thenReturn(Optional.of(movieTheater));
        when(cinemaTheatersRepository.findAllByStatusNotAndMovieTheater_MovieTheaterId(CinemaTheaterStatus.INVALID, 1))
                .thenReturn(List.of(cinemaTheater));
        when(showTimeRepository.findAllByCinemaTheaterInAndStatusNot(anyList(), eq(ShowTimeStatus.DELETED)))
                .thenReturn(List.of(showTime));

        movieTheaterService.delete(1);

        assertEquals(false, movieTheater.getStatus());
        assertEquals(CinemaTheaterStatus.INVALID, cinemaTheater.getStatus());
        assertEquals(ShowTimeStatus.DELETED, showTime.getStatus());
        verify(showTimeRepository).saveAll(List.of(showTime));
        verify(cinemaTheatersRepository).saveAll(List.of(cinemaTheater));
        verify(movieTheaterRepository).save(movieTheater);
    }
}
