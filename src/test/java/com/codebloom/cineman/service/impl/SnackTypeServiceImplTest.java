package com.codebloom.cineman.service.impl;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnackTypeServiceImplTest {

    @Mock
    private SnackTypeRepository snackTypeRepository;

    @Mock
    private SnackRepository snackRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SnackTypeServiceImpl snackTypeService;

    @Test
    void findByIdShouldIgnoreInactiveSnackType() {
        when(snackTypeRepository.findByIdAndIsActive(1, true)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> snackTypeService.findById(1));
    }

    @Test
    void deleteShouldSoftDeleteChildSnacks() {
        SnackTypeEntity snackType = SnackTypeEntity.builder().id(1).isActive(true).build();
        SnackEntity snack = SnackEntity.builder().id(2).isActive(true).snackType(snackType).build();

        when(snackTypeRepository.findByIdAndIsActive(1, true)).thenReturn(Optional.of(snackType));
        when(snackRepository.findBySnackTypeAndIsActive(snackType, true)).thenReturn(List.of(snack));

        snackTypeService.delete(1);

        assertEquals(false, snackType.getIsActive());
        assertEquals(false, snack.getIsActive());
        verify(snackRepository).saveAll(List.of(snack));
        verify(snackTypeRepository).save(snackType);
    }
}
