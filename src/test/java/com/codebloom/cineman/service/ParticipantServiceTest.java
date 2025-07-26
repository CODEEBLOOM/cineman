package com.codebloom.cineman.service;

import com.codebloom.cineman.common.enums.GenderUser;
import com.codebloom.cineman.controller.request.ParticipantRequest;
import com.codebloom.cineman.model.ParticipantEntity;
import com.codebloom.cineman.repository.ParticipantRepository;
import com.codebloom.cineman.service.impl.ParticipantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ParticipantServiceImpl participantService;

    private ParticipantEntity participant;

    private ParticipantRequest request;

    @BeforeEach
    void setUp() {
        participant = ParticipantEntity.builder()
                .participantId(1)
                .birthName("Nguyễn Văn A")
                .nickname("Diễn viên A")
                .gender(GenderUser.MALE)
                .nationality("Việt Nam")
                .miniBio("Tiểu sử ngắn gọn")
                .avatar("avatar.jpg")
                .active(true)
                .movieParticipants(new HashSet<>())
                .build();

        request = ParticipantRequest.builder()
                .birthName("Nguyễn Văn A")
                .nickname("Diễn viên A")
                .gender(GenderUser.MALE)
                .nationality("Việt Nam")
                .miniBio("Tiểu sử ngắn gọn")
                .avatar("avatar.jpg")
                .build();
    }

    @Test
    void findAll() {
        when(participantRepository.findAll()).thenReturn(List.of(participant));

        List<ParticipantEntity> result = participantService.findAll();

        assertEquals(1, result.size());
        assertEquals("Nguyễn Văn A", result.get(0).getBirthName());
    }

    @Test
    void findById() {
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));

        ParticipantEntity result = participantService.findById(1);

        assertNotNull(result);
        assertEquals("Nguyễn Văn A", result.getBirthName());
    }

    @Test
    void save() {
        when(modelMapper.map(request, ParticipantEntity.class)).thenReturn(participant);
        when(participantRepository.save(participant)).thenReturn(participant);

        ParticipantEntity result = participantService.save(request);

        assertNotNull(result);
        assertEquals("Nguyễn Văn A", result.getBirthName());
        verify(participantRepository).save(participant);
    }

    @Test
    void update() {
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(ParticipantEntity.class))).thenReturn(participant);

        request.setNickname("Diễn viên A mới");

        ParticipantEntity result = participantService.update(1, request);

        assertEquals("Diễn viên A mới", result.getNickname());
        verify(participantRepository).save(any(ParticipantEntity.class));
    }

    @Test
    void delete() {
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));

        participantService.delete(1);

        assertFalse(participant.getActive());
        verify(participantRepository).save(participant);
    }
}
