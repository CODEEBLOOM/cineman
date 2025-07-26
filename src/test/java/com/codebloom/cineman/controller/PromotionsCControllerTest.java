package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.customer.PromotionCController;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.model.PromotionEntity;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.PromotionService;
import com.codebloom.cineman.repository.PermissionRepository;
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
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromotionCController.class)
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = "api.path=/api/v1")
class PromotionsCControllerTest {

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

    private PromotionEntity promo1;
    private PromotionResponse promoResponse;

    private final String BASE_PATH = "/api/v1/customer/promotion";

    @BeforeEach
    void setup() {
        promo1 = PromotionEntity.builder()
                .promotionId(1L)
                .name("Summer Sale")
                .content("Up to 50% off")
                .code("SUMMER50")
                .startDay(new Date())
                .endDay(new Date())
                .discount(50.0)
                .build();

        promoResponse = PromotionResponse.builder()
                .promotionId(1L)
                .code("SUMMER50")
                .discount(50.0)
                .name("Summer Sale")
                .build();
    }

    @Test
    @DisplayName("GET /available - should return list of valid promotions")
    void getAvailablePromotions_ShouldReturnList() throws Exception {
        when(promotionService.getAvailablePromotions()).thenReturn(List.of(promoResponse));

        mockMvc.perform(get(BASE_PATH + "/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Danh sách voucher còn hiệu lực"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].code").value("SUMMER50"));
    }

    @Test
    @DisplayName("GET /validate - should return valid voucher")
    void validateVoucher_ShouldReturnValidVoucher() throws Exception {
        String code = "SUMMER50";
        when(promotionService.validateVoucherCode(code)).thenReturn(promoResponse);

        mockMvc.perform(get(BASE_PATH + "/validate").param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Mã hợp lệ"))
                .andExpect(jsonPath("$.data.code").value("SUMMER50"));
    }

    @Test
    @DisplayName("POST /apply - should apply voucher successfully")
    void applyVoucherToInvoice_ShouldApplySuccessfully() throws Exception {
        String code = "SUMMER50";
        Long invoiceId = 100L;


        doNothing().when(promotionService).applyVoucherToInvoice(code, invoiceId);

        mockMvc.perform(post(BASE_PATH + "/apply")
                        .param("code", code)
                        .param("invoiceId", String.valueOf(invoiceId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Áp dụng voucher thành công"));

        verify(promotionService, times(1)).applyVoucherToInvoice(code, invoiceId);
    }
}
