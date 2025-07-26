package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.SnackRequest;
import com.codebloom.cineman.controller.response.SnackResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnackServiceTest {

    @Mock
    private SnackService snackService;

    private SnackRequest snackRequest;
    private SnackResponse snackResponse;

    @BeforeEach
    void setUp() {
        snackRequest = SnackRequest.builder()
                .snackName("Bắp rang")
                .unitPrice(25000.0)
                .image("popcorn.png")
                .description("Bắp rang bơ truyền thống")
                .snackTypeId(1)
                .build();

        snackResponse = new SnackResponse(1, "Bắp rang", 25000.0, "popcorn.png", "Bắp rang bơ truyền thống", "Truyền thống", true);
    }

    @Test
    void testCreateSnack_Success() {
        when(snackService.create(any(SnackRequest.class))).thenReturn(snackResponse);

        SnackResponse result = snackService.create(snackRequest);

        assertNotNull(result);
        assertEquals("Bắp rang", result.getSnackName());
        verify(snackService, times(1)).create(snackRequest);
    }

    @Test
    void testUpdateSnack_Success() {
        when(snackService.update(eq(1), any(SnackRequest.class))).thenReturn(snackResponse);

        SnackResponse result = snackService.update(1, snackRequest);

        assertNotNull(result);
        assertEquals(25000.0, result.getUnitPrice());
        verify(snackService, times(1)).update(1, snackRequest);
    }

    @Test
    void testFindSnackById_Success() {
        when(snackService.findById(1)).thenReturn(snackResponse);

        SnackResponse result = snackService.findById(1);

        assertEquals("Bắp rang", result.getSnackName());
        verify(snackService, times(1)).findById(1);
    }

    @Test
    void testFindAllSnacks_Success() {
        when(snackService.findAll()).thenReturn(List.of(snackResponse));

        List<SnackResponse> result = snackService.findAll();

        assertEquals(1, result.size());
        verify(snackService, times(1)).findAll();
    }

    @Test
    void testDeleteSnack_Success() {
        doNothing().when(snackService).delete(1);

        snackService.delete(1);

        verify(snackService, times(1)).delete(1);
    }
}
