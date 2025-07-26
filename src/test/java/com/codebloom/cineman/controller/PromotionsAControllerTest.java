package com.codebloom.cineman.controller;

import com.codebloom.cineman.common.enums.StatusPromotion;
import com.codebloom.cineman.controller.admin.PromotionAController;
import com.codebloom.cineman.controller.request.PromotionRequest;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.PromotionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(PromotionAController.class)
@Import(TestSecurityConfig.class)
public class PromotionsAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromotionService promotionService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_PATH = "/api/v1/admin/promotion";

    private PromotionRequest request;

    @BeforeEach
    void setUp() {
        request = new PromotionRequest();
        request.setName("Summer Sale");
        request.setContent("Discount for summer");
        request.setCode("SUMMER2025");
        request.setStartDay(new Date());
        request.setEndDay(new Date(System.currentTimeMillis() + 86400000)); // +1 day
        request.setDiscount(20.0);
        request.setConditionType("MIN_TOTAL_PRICE");
        request.setConditionValue(100000.0);
        request.setStaffId(1L);
    }

    @Test
    @DisplayName("GET /all - Should return all promotions")
    void getAllPromotions_ShouldReturnList() throws Exception {
        List<PromotionResponse> promotions = List.of(
                new PromotionResponse(1L, "Sale A", "Content A", "CODEA", new Date(), new Date(),
                        10.0, "MIN_TOTAL_TICKET", null, 2.0, StatusPromotion.ACTIVE),
                new PromotionResponse(2L, "Sale B", "Content B", "CODEB", new Date(), new Date(),
                        15.0, "MIN_TOTAL_PRICE", null, 100000.0, StatusPromotion.ACTIVE)
        );

        when(promotionService.getAllPromotions()).thenReturn(promotions);

        mockMvc.perform(get(BASE_PATH + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name").value("Sale A"));

        verify(promotionService).getAllPromotions();
    }

    @Test
    @DisplayName("GET /{id} - Should return promotion by ID")
    void getPromotionById_ShouldReturnPromotion() throws Exception {
        PromotionResponse response = new PromotionResponse(
                1L, "Sale Test", "Content Test", "CODE123", new Date(), new Date(),
                25.0, "DAY_OF_WEEK", "FRIDAY", null, StatusPromotion.ACTIVE
        );

        when(promotionService.getPromotionById(1L)).thenReturn(response);

        mockMvc.perform(get(BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("Sale Test"));
    }

    @Test
    @DisplayName("POST /add - Should create new promotion")
    void createPromotion_ShouldReturnCreated() throws Exception {
        PromotionResponse response = new PromotionResponse(
                3L, "Summer Sale", "Discount for summer", "SUMMER2025", request.getStartDay(), request.getEndDay(),
                20.0, "MIN_TOTAL_PRICE", null, 100000.0, StatusPromotion.ACTIVE
        );

        when(promotionService.createPromotion(any())).thenReturn(response);

        mockMvc.perform(post(BASE_PATH + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.name").value("Summer Sale"));
    }

    @Test
    @DisplayName("PUT /{id}/update - Should update promotion")
    void updatePromotion_ShouldReturnUpdated() throws Exception {
        PromotionResponse response = new PromotionResponse(
                5L, "Updated Sale", "Updated content", "UPDATE2025", request.getStartDay(), request.getEndDay(),
                30.0, "MIN_TOTAL_PRICE", null, 200000.0, StatusPromotion.ACTIVE
        );

        when(promotionService.updatePromotion(eq(5L), any())).thenReturn(response);

        mockMvc.perform(put(BASE_PATH + "/5/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("Updated Sale"));
    }

    @Test
    @DisplayName("DELETE /{id}/delete - Should delete promotion")
    void deletePromotion_ShouldReturnSuccess() throws Exception {
        doNothing().when(promotionService).deletePromotion(7L);

        mockMvc.perform(delete(BASE_PATH + "/7/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Deleted promotion successfully"));
    }
}
