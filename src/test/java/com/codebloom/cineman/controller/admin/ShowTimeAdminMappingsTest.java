package com.codebloom.cineman.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ShowTimeAdminMappingsTest {

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    void shouldRegisterAdminShowTimeMappings() {
        boolean hasFindAllMapping = handlerMapping.getHandlerMethods().keySet().stream()
                .anyMatch(info -> info.getPatternValues().contains("/api/v01/admin/show-time/all"));

        boolean hasOccupiedSlotsMapping = handlerMapping.getHandlerMethods().keySet().stream()
                .anyMatch(info -> info.getPatternValues().contains("/api/v01/admin/show-time/cinema-theater/{id}/occupied-slots"));

        assertTrue(hasFindAllMapping, "Missing mapping for /api/v01/admin/show-time/all");
        assertTrue(hasOccupiedSlotsMapping, "Missing mapping for /api/v01/admin/show-time/cinema-theater/{id}/occupied-slots");
    }
}
