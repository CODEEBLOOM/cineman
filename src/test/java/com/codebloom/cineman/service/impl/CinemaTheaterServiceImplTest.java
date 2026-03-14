package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.CinemaTheaterRequest;
import com.codebloom.cineman.exception.InvalidDataException;
import com.codebloom.cineman.model.CinemaTypeEntity;
import com.codebloom.cineman.model.MovieTheaterEntity;
import com.codebloom.cineman.repository.CinemaTheatersRepository;
import com.codebloom.cineman.repository.CinemaTypeRepository;
import com.codebloom.cineman.repository.MovieTheaterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CinemaTheaterServiceImplTest {

    @Mock
    private CinemaTheatersRepository cinemaTheatersRepository;

    @Mock
    private MovieTheaterRepository movieTheaterRepository;

    @Mock
    private CinemaTypeRepository cinemaTypeRepository;

    @InjectMocks
    private CinemaTheaterServiceImpl cinemaTheaterService;

    private CinemaTheaterRequest request;

    @BeforeEach
    void setUp() {
        request = new CinemaTheaterRequest();
        request.setName("Phong thuong 03");
        request.setNumberOfRows(10);
        request.setNumberOfColumns(20);
        request.setRegularSeatRow(10);
        request.setVipSeatRow(0);
        request.setDoubleSeatRow(0);
        request.setCinemaTypeId(1);
        request.setMovieTheaterId(1);
    }

    @Test
    void createShouldAllowVipAndDoubleRowsToBeZero() {
        when(movieTheaterRepository.findByStatusAndMovieTheaterId(true, 1))
                .thenReturn(Optional.of(new MovieTheaterEntity()));
        when(cinemaTypeRepository.findByStatusAndCinemaTypeId(true, 1))
                .thenReturn(Optional.of(new CinemaTypeEntity()));
        when(cinemaTheatersRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> cinemaTheaterService.create(request));
    }

    @Test
    void createShouldRejectWhenRegularSeatRowsMissing() {
        request.setRegularSeatRow(0);

        InvalidDataException exception = assertThrows(
                InvalidDataException.class,
                () -> cinemaTheaterService.create(request)
        );

        assertEquals("Phòng chiếu phải có ít nhất 1 hàng ghế thường", exception.getMessage());
        verifyNoInteractions(movieTheaterRepository, cinemaTypeRepository, cinemaTheatersRepository);
    }

    @Test
    void createShouldRejectWhenSeatRowConfigurationDoesNotMatchRoomRows() {
        request.setRegularSeatRow(8);
        request.setVipSeatRow(1);
        request.setDoubleSeatRow(0);

        InvalidDataException exception = assertThrows(
                InvalidDataException.class,
                () -> cinemaTheaterService.create(request)
        );

        assertEquals(
                "Tổng số hàng ghế thường, VIP và DOUBLE phải bằng số lượng hàng ghế của phòng chiếu. Hiện tại là 9/10 hàng.",
                exception.getMessage()
        );
        verifyNoInteractions(movieTheaterRepository, cinemaTypeRepository, cinemaTheatersRepository);
    }
}
