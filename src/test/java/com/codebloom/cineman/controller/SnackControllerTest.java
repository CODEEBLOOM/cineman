package com.codebloom.cineman.controller;


import com.codebloom.cineman.controller.admin.SnackController;
import com.codebloom.cineman.controller.request.SnackRequest;
import com.codebloom.cineman.controller.response.SnackResponse;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.SnackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SnackController.class)
@TestPropertySource(properties = "api.path=/api/v1")
@Import(TestSecurityConfig.class)
public class SnackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private SnackService snackService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllSnacks() throws Exception {
        SnackResponse snack = new SnackResponse(1, "Snack A", 10.0, "image.jpg", "Ngon", "Khoai tây", true);
        when(snackService.findAll()).thenReturn(List.of(snack));

        mockMvc.perform(get("/api/v1/admin/snacks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].snackName", is("Snack A")));
    }

    @Test
    void testGetSnackById() throws Exception {
        SnackResponse snack = new SnackResponse(1, "Snack A", 10.0, "image.jpg", "Ngon", "Khoai tây", true);
        when(snackService.findById(1)).thenReturn(snack);

        mockMvc.perform(get("/api/v1/admin/snacks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.snackName", is("Snack A")));
    }

    @Test
    void testCreateSnack() throws Exception {
        SnackRequest request = new SnackRequest("Snack A", 10.0, "image.jpg", "Ngon", 1);
        SnackResponse response = new SnackResponse(1, "Snack A", 10.0, "image.jpg", "Ngon", "Khoai tây", true);

        when(snackService.create(any(SnackRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/snacks/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.snackName", is("Snack A")));
    }

    @Test
    void testUpdateSnack() throws Exception {
        SnackRequest request = new SnackRequest("Snack Updated", 12.0, "img.png", "Giòn ngon", 1);
        SnackResponse response = new SnackResponse(1, "Snack Updated", 12.0, "img.png", "Giòn ngon", "Khoai", true);

        when(snackService.update(eq(1), any(SnackRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/admin/snacks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.snackName", is("Snack Updated")));
    }

    @Test
    void testDeleteSnack() throws Exception {
        doNothing().when(snackService).delete(1);

        mockMvc.perform(delete("/api/v1/admin/snacks/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status", is(204)))
                .andExpect(jsonPath("$.message", is("Xóa snack thành công.")))
                .andExpect(jsonPath("$.data", is(1)));
    }
}
