package com.codebloom.cineman.service;

import com.codebloom.cineman.controller.request.SnackTypeRequest;
import com.codebloom.cineman.controller.response.SnackTypeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnackTypeServiceTest {

    @Mock
    private SnackTypeService snackTypeService;

    private SnackTypeRequest snackTypeRequest;
    private SnackTypeResponse snackTypeResponse;

    @BeforeEach
    void setUp() {
        snackTypeRequest = new SnackTypeRequest();
        snackTypeRequest.setId(1L);
        snackTypeRequest.setName("Truyền thống");
        snackTypeRequest.setDescription("Các món snack truyền thống");

        snackTypeResponse = new SnackTypeResponse(1L, "Truyền thống", "Các món snack truyền thống", true);
    }

    @Test
    void testSaveSnackType_Success() {
        when(snackTypeService.save(any(SnackTypeRequest.class))).thenReturn(snackTypeResponse);

        SnackTypeResponse result = snackTypeService.save(snackTypeRequest);

        assertNotNull(result);
        assertEquals("Truyền thống", result.getName());
        verify(snackTypeService, times(1)).save(snackTypeRequest);
    }

    @Test
    void testUpdateSnackType_Success() {
        when(snackTypeService.update(eq(1), any(SnackTypeRequest.class))).thenReturn(snackTypeResponse);

        SnackTypeResponse result = snackTypeService.update(1, snackTypeRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(snackTypeService, times(1)).update(1, snackTypeRequest);
    }

    @Test
    void testFindById_Success() {
        when(snackTypeService.findById(1)).thenReturn(snackTypeResponse);

        SnackTypeResponse result = snackTypeService.findById(1);

        assertEquals("Truyền thống", result.getName());
        assertTrue(result.getIsActive());
        verify(snackTypeService, times(1)).findById(1);
    }

    @Test
    void testFindAll_Success() {
        when(snackTypeService.findAll()).thenReturn(List.of(snackTypeResponse));

        List<SnackTypeResponse> result = snackTypeService.findAll();

        assertEquals(1, result.size());
        verify(snackTypeService, times(1)).findAll();
    }

    @Test
    void testDelete_Success() {
        doNothing().when(snackTypeService).delete(1);

        snackTypeService.delete(1);

        verify(snackTypeService, times(1)).delete(1);
    }
}
