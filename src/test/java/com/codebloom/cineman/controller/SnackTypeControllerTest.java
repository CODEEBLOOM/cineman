package com.codebloom.cineman.controller;



import com.codebloom.cineman.controller.admin.SnackTypeController;
import com.codebloom.cineman.controller.request.SnackTypeRequest;
import com.codebloom.cineman.controller.response.SnackTypeResponse;
import com.codebloom.cineman.service.JwtService;
import com.codebloom.cineman.service.SnackTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SnackTypeController.class)
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = "api.path=/api/v1")
class SnackTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private SnackTypeService snackTypeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllSnackTypes() throws Exception {
        List<SnackTypeResponse> responseList = List.of(
                new SnackTypeResponse(1L, "Snack mặn", "Snack mặn truyền thống", true),
                new SnackTypeResponse(2L, "Snack ngọt", "Snack vị dâu", true)
        );
        Mockito.when(snackTypeService.findAll()).thenReturn(responseList);

        mockMvc.perform(get("/api/v1/admin/snack-types/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("Snack mặn"));
    }

    @Test
    void testGetSnackTypeById() throws Exception {
        SnackTypeResponse response = new SnackTypeResponse(1L, "Snack cay", "Snack vị ớt", true);
        Mockito.when(snackTypeService.findById(1)).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/snack-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Snack cay"));
    }

    @Test
    void testCreateSnackType() throws Exception {
        SnackTypeRequest request = new SnackTypeRequest(null, "Snack mè", "Snack vị mè đen");
        SnackTypeResponse response = new SnackTypeResponse(3L, "Snack mè", "Snack vị mè đen", true);

        Mockito.when(snackTypeService.save(any(SnackTypeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/snack-types/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Snack mè"));
    }

    @Test
    void testUpdateSnackType() throws Exception {
        SnackTypeRequest request = new SnackTypeRequest(null, "Snack cải tiến", "Snack nâng cấp");
        SnackTypeResponse response = new SnackTypeResponse(1L, "Snack cải tiến", "Snack nâng cấp", true);

        Mockito.when(snackTypeService.update(eq(1), any(SnackTypeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/admin/snack-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Snack cải tiến"));
    }

    @Test
    void testDeleteSnackType() throws Exception {
        Mockito.doNothing().when(snackTypeService).delete(1);

        mockMvc.perform(delete("/api/v1/admin/snack-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));
    }
}
