package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.customer.CustomerInvoiceSnackController;
import com.codebloom.cineman.controller.request.DetailBookingSnackRequest;
import com.codebloom.cineman.controller.response.DetailBookingSnackResponse;
import com.codebloom.cineman.repository.PermissionRepository;
import com.codebloom.cineman.service.DetailBookingSnackService;
import com.codebloom.cineman.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "api.path=/api/v1")
@WebMvcTest(CustomerInvoiceSnackController.class)
@Import(TestSecurityConfig.class)
class CustomerInvoiceSnackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DetailBookingSnackService detailBookingSnackService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PermissionRepository permissionRepository;

    private DetailBookingSnackRequest request;

    @BeforeEach
    void setUp() {
        request = new DetailBookingSnackRequest();
        request.setSnackId(10);
        request.setTotalSnack(3);
    }

    @Test
    @DisplayName("POST /invoices/detail-snacks/{id}/add - Thêm snack vào hóa đơn")
    void testAddToInvoice() throws Exception {
        DetailBookingSnackResponse response = new DetailBookingSnackResponse(
                1L,
                3,
                45000,
                10,
                5L
        );

        when(detailBookingSnackService.addToInvoice(Mockito.eq(5L), any(DetailBookingSnackRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/invoices/detail-snacks/5/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Snack added to Invoice successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.totalSnack").value(3))
                .andExpect(jsonPath("$.data.totalMoney").value(45000))
                .andExpect(jsonPath("$.data.snackId").value(10))
                .andExpect(jsonPath("$.data.invoiceId").value(5));
    }
}
