package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.SnackRequest;
import com.codebloom.cineman.controller.response.SnackResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.SnackEntity;
import com.codebloom.cineman.repository.SnackRepository;
import com.codebloom.cineman.repository.SnackTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnackServiceImplTest {

    @Mock
    private SnackRepository snackRepository;

    @Mock
    private SnackTypeRepository snackTypeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SnackServiceImpl snackService;

    @Test
    void createShouldRejectInactiveSnackType() {
        SnackRequest request = new SnackRequest();
        request.setSnackName("Popcorn");
        request.setUnitPrice(10.0);
        request.setImage("popcorn.jpg");
        request.setDescription("Large popcorn");
        request.setSnackTypeId(1);

        when(snackTypeRepository.findByIdAndIsActive(1, true)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> snackService.create(request));
    }

    @Test
    void findAllComboSnacksShouldReturnEmptyListWhenComboTypeMissing() {
        when(snackTypeRepository.findByNameAndIsActive("Combo", true)).thenReturn(Optional.empty());

        assertEquals(Collections.emptyList(), snackService.findAllComboSnacks());
        verify(snackRepository, never()).findBySnackTypeAndIsActive(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(true));
    }

    @Test
    void deleteShouldSoftDeleteActiveSnack() {
        SnackEntity snack = SnackEntity.builder().id(1).isActive(true).build();

        when(snackRepository.findByIdAndIsActive(1, true)).thenReturn(Optional.of(snack));

        snackService.delete(1);

        assertEquals(false, snack.getIsActive());
        verify(snackRepository).save(snack);
    }
}
