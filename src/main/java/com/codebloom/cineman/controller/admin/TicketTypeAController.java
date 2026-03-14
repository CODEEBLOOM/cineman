package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.TicketTypeRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.TicketTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/admin/ticket-type")
@Tag(name = "Ticket Type Controller", description = "Quan ly loai ve")
@Validated
public class TicketTypeAController {

    private final TicketTypeService ticketTypeService;

    @Operation(summary = "Get all ticket types")
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

    @Operation(summary = "Get ticket type by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTicketTypeById(@PathVariable @Min(1) Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(ticketTypeService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Create ticket type")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createTicketType(@RequestBody @Valid TicketTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Create ticket type successfully")
                        .data(ticketTypeService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Update ticket type")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateTicketType(
            @PathVariable @Min(1) Integer id,
            @RequestBody @Valid TicketTypeRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update ticket type successfully")
                        .data(ticketTypeService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Delete ticket type")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteTicketType(@PathVariable @Min(1) Integer id) {
        ticketTypeService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete ticket type successfully")
                        .data(null)
                        .build()
        );
    }
}
