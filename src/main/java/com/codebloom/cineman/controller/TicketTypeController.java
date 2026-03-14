package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.TicketTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/ticket-type")
@Tag(name = "Ticket Type Client Controller")
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @Operation(summary = "Get all active ticket types")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllTicketTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(ticketTypeService.findAll())
                        .build()
        );
    }
}
