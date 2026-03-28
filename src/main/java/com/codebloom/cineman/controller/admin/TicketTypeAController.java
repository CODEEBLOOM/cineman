package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.TicketTypeRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.TicketTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/admin/ticket-type")
@Tag(name = "Ticket Type Controller")
@Validated
public class TicketTypeAController {

    private final TicketTypeService ticketTypeService;

    @Operation(summary = "Get all ticket type", description = "API dung de lay ra toan bo loai ve dang hoat dong.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllTicketType() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(ticketTypeService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Get ticket type by id", description = "API dung de lay ra loai ve theo id.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTicketTypeById(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(ticketTypeService.findById(id))
                        .build()
        );
    }

    @Operation(summary = "Create ticket type", description = "API dung de tao moi loai ve.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createTicketType(@RequestBody @Valid TicketTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Success")
                        .data(ticketTypeService.create(request))
                        .build()
        );
    }

    @Operation(summary = "Update ticket type", description = "API dung de cap nhat thong tin loai ve.")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateTicketType(
            @PathVariable Integer id,
            @RequestBody @Valid TicketTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Success")
                        .data(ticketTypeService.update(id, request))
                        .build()
        );
    }

    @Operation(summary = "Delete ticket type", description = "API dung de xoa mem loai ve.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteTicketType(@PathVariable Integer id) {
        ticketTypeService.delete(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Success")
                        .data(null)
                        .build()
        );
    }
}
