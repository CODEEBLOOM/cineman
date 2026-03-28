package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.SnackRequest;
import com.codebloom.cineman.controller.response.SnackResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.SnackEntity;
import com.codebloom.cineman.model.SnackTypeEntity;
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

    @Test
    void updateShouldReplaceSnackTypeWithoutMutatingManagedSnackTypeIdentifier() {
        SnackRequest request = new SnackRequest();
        request.setSnackName("Combo B");
        request.setUnitPrice(20.0);
        request.setImage("combo-b.jpg");
        request.setDescription("Updated combo");
        request.setSnackTypeId(2);

        SnackTypeEntity currentType = SnackTypeEntity.builder().id(3).name("Old Type").isActive(true).build();
        SnackTypeEntity newType = SnackTypeEntity.builder().id(2).name("Combo").isActive(true).build();
        SnackEntity snack = SnackEntity.builder()
                .id(1)
                .snackName("Combo A")
                .unitPrice(15.0)
                .image("combo-a.jpg")
                .description("Old combo")
                .isActive(true)
                .snackType(currentType)
                .build();
        SnackResponse response = new SnackResponse();

        when(snackRepository.findByIdAndIsActive(1, true)).thenReturn(Optional.of(snack));
        when(snackTypeRepository.findByIdAndIsActive(2, true)).thenReturn(Optional.of(newType));
        when(snackRepository.save(snack)).thenReturn(snack);
        when(modelMapper.map(snack, SnackResponse.class)).thenReturn(response);

        snackService.update(1, request);

        assertEquals("Combo B", snack.getSnackName());
        assertEquals(20.0, snack.getUnitPrice());
        assertEquals("combo-b.jpg", snack.getImage());
        assertEquals("Updated combo", snack.getDescription());
        assertEquals(2, snack.getSnackType().getId());
        verify(snackRepository).save(snack);
    }
}
