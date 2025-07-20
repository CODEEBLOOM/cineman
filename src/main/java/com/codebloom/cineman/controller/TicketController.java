package com.codebloom.cineman.controller;

import com.codebloom.cineman.controller.request.TicketRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/ticket")
@Validated
@Tag(name = " Controller Ticket", description = "API quản lý vé của các showtime")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/show-time/{showTimeId}/all")
    @Operation(summary = "Find all ticket of show time", description = "API dùng để lấy tất cả vé của show time")
    public ResponseEntity<ApiResponse> findAllTicketByShowTimeId(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0") Long showTimeId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(ticketService.findAllByShowTimeId(showTimeId))
                        .build()
        );
    }

    @Operation(summary = "Create a ticket", description = "API dùng để tạo mới một ticket")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createTicket(@RequestBody @Validated TicketRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Create success")
                        .data(ticketService.create(req))
                        .build()
        );
    }

}
