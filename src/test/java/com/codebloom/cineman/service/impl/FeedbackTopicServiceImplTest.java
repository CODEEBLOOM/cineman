package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.FeedbackTopicRequest;
import com.codebloom.cineman.controller.response.FeedbackTopicResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.exception.InvalidDataException;
import com.codebloom.cineman.model.FeedbackTopicEntity;
import com.codebloom.cineman.repository.FeedbackTopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedbackTopicServiceImplTest {

    private FeedbackTopicRepository feedbackTopicRepository;
    private ModelMapper modelMapper;
    private FeedbackTopicServiceImpl feedbackTopicService;

    @BeforeEach
    void setUp() {
        feedbackTopicRepository = mock(FeedbackTopicRepository.class);
        modelMapper = new ModelMapper();
        feedbackTopicService = new FeedbackTopicServiceImpl(feedbackTopicRepository, modelMapper);
    }

    @Test
    void delete_shouldSetIsActiveFalse_whenTopicExists() {
        FeedbackTopicEntity entity = new FeedbackTopicEntity();
        entity.setTopicId(1);
        entity.setIsActive(true);

        when(feedbackTopicRepository.findById(1)).thenReturn(Optional.of(entity));
        feedbackTopicService.delete(1);

        assertFalse(entity.getIsActive());
        verify(feedbackTopicRepository).save(entity);
    }

    @Test
    void delete_shouldThrowException_whenTopicNotFound() {
        when(feedbackTopicRepository.findById(1)).thenReturn(Optional.empty());

        DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> feedbackTopicService.delete(1));
        assertEquals("Feedback topic not found", ex.getMessage());
    }

    @Test
    void findAll_shouldReturnListOfResponses_whenActiveTopicsExist() {
        FeedbackTopicEntity e1 = FeedbackTopicEntity.builder()
                .topicId(1)
                .topicName("Tên 1")
                .description("Mô tả 1")
                .isActive(true)
                .build();

        FeedbackTopicEntity e2 = FeedbackTopicEntity.builder()
                .topicId(2)
                .topicName("Tên 2")
                .description("Mô tả 2")
                .isActive(true)
                .build();

        when(feedbackTopicRepository.findByIsActiveTrue()).thenReturn(List.of(e1, e2));

        List<FeedbackTopicResponse> responses = feedbackTopicService.findAll();

        assertEquals(2, responses.size());
        assertEquals("Tên 1", responses.get(0).getTopicName());
    }

    @Test
    void findById_shouldReturnResponse_whenTopicIsActive() {
        FeedbackTopicEntity entity = FeedbackTopicEntity.builder()
                .topicId(1)
                .topicName("Rạp")
                .description("abc")
                .isActive(true)
                .build();

        when(feedbackTopicRepository.findById(1)).thenReturn(Optional.of(entity));

        FeedbackTopicResponse response = feedbackTopicService.findById(1);

        assertEquals("Rạp", response.getTopicName());
    }

    @Test
    void findById_shouldThrowException_whenTopicNotFoundOrInactive() {
        FeedbackTopicEntity inactiveEntity = FeedbackTopicEntity.builder()
                .topicId(1)
                .topicName("Rạp")
                .description("abc")
                .isActive(false)
                .build();

        when(feedbackTopicRepository.findById(1)).thenReturn(Optional.of(inactiveEntity));

        DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> feedbackTopicService.findById(1));
        assertEquals("Feedback topic not found or inactive", ex.getMessage());
    }

    @Test
    void save_shouldCreateTopic_whenTopicNameNotExists() {
        FeedbackTopicRequest request = new FeedbackTopicRequest("Dịch vụ", "Phục vụ quầy");
        when(feedbackTopicRepository.findByTopicName("Dịch vụ")).thenReturn(Optional.empty());

        FeedbackTopicEntity entity = modelMapper.map(request, FeedbackTopicEntity.class);
        entity.setTopicId(1);
        entity.setIsActive(true);

        when(feedbackTopicRepository.save(any())).thenReturn(entity);

        FeedbackTopicResponse response = feedbackTopicService.save(request);

        assertEquals("Dịch vụ", response.getTopicName());
        assertTrue(response.getDescription().contains("Phục vụ"));
    }

    @Test
    void save_shouldThrowException_whenTopicNameExists() {
        FeedbackTopicRequest request = new FeedbackTopicRequest("Trùng", "desc");

        FeedbackTopicEntity existing = FeedbackTopicEntity.builder()
                .topicId(2)
                .topicName("Trùng")
                .description("desc")
                .isActive(true)
                .build();

        when(feedbackTopicRepository.findByTopicName("Trùng")).thenReturn(Optional.of(existing));

        InvalidDataException ex = assertThrows(InvalidDataException.class, () -> feedbackTopicService.save(request));
        assertEquals("Chủ đề  / Trùng / đã tồn tại!", ex.getMessage());
    }

    @Test
    void save_shouldThrowException_whenTopicNameBlank() {
        FeedbackTopicRequest request = new FeedbackTopicRequest("   ", "Mô tả");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> feedbackTopicService.save(request));
        assertEquals("source cannot be null", ex.getMessage());
    }

    @Test
    void update_shouldUpdateTopic_whenValid() {
        FeedbackTopicEntity existing = FeedbackTopicEntity.builder()
                .topicId(1)
                .topicName("Cũ")
                .description("desc")
                .isActive(true)
                .build();

        FeedbackTopicRequest request = new FeedbackTopicRequest("Mới", "Mô tả mới");

        when(feedbackTopicRepository.findById(1)).thenReturn(Optional.of(existing));
        when(feedbackTopicRepository.isTopicNameDuplicated("Mới", 1)).thenReturn(false);
        when(feedbackTopicRepository.save(any())).thenReturn(existing);

        FeedbackTopicResponse response = feedbackTopicService.update(1, request);

        assertEquals("Mới", response.getTopicName());
        assertEquals("Mô tả mới", response.getDescription());
    }

    @Test
    void update_shouldThrowException_whenTopicNameDuplicated() {
        FeedbackTopicEntity existing = FeedbackTopicEntity.builder()
                .topicId(1)
                .topicName("Cũ")
                .description("desc")
                .isActive(true)
                .build();

        FeedbackTopicRequest request = new FeedbackTopicRequest("Trùng", "desc mới");

        when(feedbackTopicRepository.findById(1)).thenReturn(Optional.of(existing));
        when(feedbackTopicRepository.isTopicNameDuplicated("Trùng", 1)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> feedbackTopicService.update(1, request));
        assertEquals("Tên chủ đề đã tồn tại, vui lòng chọn tên khác.", ex.getMessage());
    }

    @Test
    void update_shouldThrowException_whenTopicNameIsBlank() {
        FeedbackTopicEntity existing = FeedbackTopicEntity.builder()
                .topicId(1)
                .topicName("Cũ")
                .description("desc")
                .isActive(true)
                .build();

        FeedbackTopicRequest request = new FeedbackTopicRequest("   ", "desc mới");

        when(feedbackTopicRepository.findById(1)).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> feedbackTopicService.update(1, request));
        assertEquals("source cannot be null", ex.getMessage());
    }

    @Test
    void update_shouldThrowException_whenTopicNotFoundOrInactive() {
        FeedbackTopicEntity inactive = FeedbackTopicEntity.builder()
                .topicId(1)
                .topicName("Cũ")
                .description("desc")
                .isActive(false)
                .build();

        FeedbackTopicRequest request = new FeedbackTopicRequest("Trùng", "desc mới");

        when(feedbackTopicRepository.findById(1)).thenReturn(Optional.of(inactive));

            DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> feedbackTopicService.update(1, request));
            assertEquals("Feedback topic not found or inactive", ex.getMessage());
    }
}
