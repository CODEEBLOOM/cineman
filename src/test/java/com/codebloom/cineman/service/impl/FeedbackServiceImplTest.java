package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.SatisfactionLevel;
import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.controller.request.FeedbackRequest;
import com.codebloom.cineman.controller.response.FeedbackResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.exception.InvalidDataException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedbackServiceImplTest {

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private FeedbackTopicRepository topicRepository;
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // kiểm tra lấy feedback theo ID khi feedback đang active
    @Test
    void findById_shouldReturnFeedback_whenActive() {
        FeedbackEntity feedback = mockFeedback();
        feedback.setIsActive(true);

        when(feedbackRepository.findById(1)).thenReturn(Optional.of(feedback));

        FeedbackResponse response = feedbackService.findById(1);

        assertEquals("Great service", response.getContent());
        verify(feedbackRepository).findById(1);
    }

    // kiểm tra lấy tất cả feedback nhưng chỉ trả về feedback đang active
    @Test
    void findAll_shouldReturnOnlyActiveFeedbacks() {
        FeedbackEntity active = mockFeedback();
        active.setIsActive(true);
        FeedbackEntity inactive = mockFeedback();
        inactive.setIsActive(false);

        when(feedbackRepository.findAll()).thenReturn(List.of(active, inactive));

        List<FeedbackResponse> result = feedbackService.findAll();

        assertEquals(1, result.size());
    }

    // kiểm tra lấy tất cả feedback bao gồm cả feedback không active
    @Test
    void findAllIncludeInactive_shouldReturnAllFeedbacks() {
        FeedbackEntity feedback = mockFeedback();
        when(feedbackRepository.findAll()).thenReturn(List.of(feedback));

        List<FeedbackResponse> result = feedbackService.findAllIncludeInactive();

        assertEquals(1, result.size());
    }

    // kiểm tra lưu feedback thành công khi dữ liệu hợp lệ
    @Test
    void save_shouldCreateFeedback_whenValid() {
        Long userId = 1L;
        FeedbackRequest request = new FeedbackRequest();
        request.setContent("Good");
        request.setSatisfactionLevel(SatisfactionLevel.SATISFIED);
        request.setReasonForReview("Nice experience");
        request.setInvoiceId(10L);
        request.setTopicId(5);

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setEmail("user@example.com");

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceId(10L);
        invoice.setCustomer(user);
        invoice.setStatus(InvoiceStatus.PAID);

        FeedbackTopicEntity topic = new FeedbackTopicEntity();
        topic.setTopicId(5);
        topic.setTopicName("Cleanliness");
        topic.setIsActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(invoiceRepository.findById(10L)).thenReturn(Optional.of(invoice));
        when(feedbackRepository.existsByInvoice_InvoiceId(10L)).thenReturn(false);
        when(topicRepository.findByTopicIdAndIsActiveTrue(5)).thenReturn(Optional.of(topic));
        when(feedbackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        FeedbackResponse response = feedbackService.save(request, userId);

        assertEquals("Good", response.getContent());
    }

    // kiểm tra chức năng admin xóa feedback bằng cách đánh dấu không active
    @Test
    void deleteByAdmin_shouldSetInactive_whenActive() {
        FeedbackEntity feedback = mockFeedback();
        feedback.setIsActive(true);

        when(feedbackRepository.findById(1)).thenReturn(Optional.of(feedback));

        feedbackService.deleteByAdmin(1);

        assertFalse(feedback.getIsActive());
        verify(feedbackRepository).save(feedback);
    }

    // kiểm tra lấy feedback của người dùng đang active
    @Test
    void findByUser_shouldReturnFeedbacks() {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setStatus(UserStatus.ACTIVE);

        FeedbackEntity feedback = mockFeedback();
        feedback.setIsActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(feedbackRepository.findByInvoiceCustomerAndInvoiceCustomerStatus(user, UserStatus.ACTIVE))
                .thenReturn(List.of(feedback));

        List<FeedbackResponse> responses = feedbackService.findByUser(userId);

        assertEquals(1, responses.size());
    }

    // kiểm tra lọc feedback theo mức độ hài lòng, chỉ trả về feedback active
    @Test
    void findBySatisfactionLevel_shouldReturnActiveFeedbacks() {
        FeedbackEntity f1 = mockFeedback();
        f1.setIsActive(true);
        FeedbackEntity f2 = mockFeedback();
        f2.setIsActive(false);

        when(feedbackRepository.findBySatisfactionLevel(SatisfactionLevel.SATISFIED))
                .thenReturn(List.of(f1, f2));

        List<FeedbackResponse> result = feedbackService.findBySatisfactionLevel(SatisfactionLevel.SATISFIED);

        assertEquals(1, result.size());
    }

    // kiểm tra lấy feedback theo email người dùng, chỉ trả về feedback active
    @Test
    void findByUserEmail_shouldReturnOnlyActiveFeedbacks() {
        FeedbackEntity f1 = mockFeedback();
        f1.setIsActive(true);
        FeedbackEntity f2 = mockFeedback();
        f2.setIsActive(false);

        when(feedbackRepository.findByInvoice_Customer_Email("user@example.com"))
                .thenReturn(List.of(f1, f2));

        List<FeedbackResponse> result = feedbackService.findByUserEmail("user@example.com");

        assertEquals(1, result.size());
    }

    // kiểm tra lỗi khi không tìm thấy hóa đơn trong lúc lưu feedback
    @Test
    void save_shouldThrow_whenInvoiceNotFound() {
        when(invoiceRepository.findById(10L)).thenReturn(Optional.empty());

        FeedbackRequest request = new FeedbackRequest();
        request.setInvoiceId(10L);
        Long userId = 1L;

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> feedbackService.save(request, userId));

        assertEquals("Người dùng không tồn tại", ex.getMessage());
    }

    // kiểm tra lỗi khi người dùng cố gửi feedback cho hóa đơn không thuộc về họ
    @Test
    void save_shouldThrow_whenInvoiceNotBelongToUser() {
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        UserEntity anotherUser = new UserEntity();
        anotherUser.setUserId(2L);

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceId(10L);
        invoice.setCustomer(anotherUser);
        invoice.setStatus(InvoiceStatus.PAID);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(invoiceRepository.findById(10L)).thenReturn(Optional.of(invoice));

        FeedbackRequest request = new FeedbackRequest();
        request.setInvoiceId(10L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> feedbackService.save(request, userId));

        assertEquals("Bạn không có quyền gửi feedback cho hóa đơn này", ex.getMessage());
    }

    // kiểm tra lỗi khi feedback đã tồn tại cho hóa đơn
    @Test
    void save_shouldThrow_whenAlreadyExistsFeedback() {
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceId(10L);
        invoice.setCustomer(user);
        invoice.setStatus(InvoiceStatus.PAID);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(invoiceRepository.findById(10L)).thenReturn(Optional.of(invoice));
        when(feedbackRepository.existsByInvoice_InvoiceId(10L)).thenReturn(true);

        FeedbackRequest request = new FeedbackRequest();
        request.setInvoiceId(10L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> feedbackService.save(request, userId));

        assertEquals("Hóa đơn này đã được feedback trước đó", ex.getMessage());
    }

    // kiểm tra lỗi khi chủ đề feedback không tồn tại hoặc không active
    @Test
    void save_shouldThrow_whenTopicNotFoundOrInactive() {
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceId(10L);
        invoice.setCustomer(user);
        invoice.setStatus(InvoiceStatus.PAID);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(invoiceRepository.findById(10L)).thenReturn(Optional.of(invoice));
        when(feedbackRepository.existsByInvoice_InvoiceId(10L)).thenReturn(false);
        when(topicRepository.findByTopicIdAndIsActiveTrue(5)).thenReturn(Optional.empty());

        FeedbackRequest request = new FeedbackRequest();
        request.setInvoiceId(10L);
        request.setTopicId(5);

        DataNotFoundException ex = assertThrows(DataNotFoundException.class,
                () -> feedbackService.save(request, userId));

        assertEquals("Chủ đề không tồn tại hoặc đã bị vô hiệu hóa", ex.getMessage());
    }

    private FeedbackEntity mockFeedback() {
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setFeedbackId(1);
        feedback.setContent("Great service");
        feedback.setSatisfactionLevel(SatisfactionLevel.SATISFIED);
        feedback.setReasonForReview("Clean and nice");
        feedback.setDateFeedback(new Date());
        feedback.setIsActive(true);

        FeedbackTopicEntity topic = new FeedbackTopicEntity();
        topic.setTopicId(1);
        topic.setTopicName("Service");

        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceId(10L);
        invoice.setCustomer(user);
        feedback.setInvoice(invoice);

        feedback.setTopic(topic);
        feedback.setUser(user);

        return feedback;
    }
}
