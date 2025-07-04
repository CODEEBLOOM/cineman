package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.ParticipantRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.path}/admin/participant")
@Tag(name = "Participant Controller", description = "Quản lý người tham gia cho bộ phim")
@Validated
public class ParticipantAController {

    private final ParticipantService participantService;

    @GetMapping("/all")
    @Operation(summary = "get participants", description = "API dùng để lấy tất cả đạo diễn chưa thực hiện phân trang!")
    public ResponseEntity<ApiResponse> getAllParticipants() {
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Success")
                        .data(participantService.findAll())
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "get participant by id", description = "API dùng để lấy đạo diễn theo id !")
    public ResponseEntity<ApiResponse> getParticipant(@PathVariable Integer id) {
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Success")
                        .data(participantService.findById(id))
                        .build()
        );
    }

    @PostMapping("/add")
    @Operation(summary = "Create participant", description = "API dùng để tạo mới một đạo diễn của bộ phim !")
    public ResponseEntity<ApiResponse> createParticipant(@RequestBody @Valid ParticipantRequest request) {
        return ResponseEntity.status(CREATED).body(
                ApiResponse.builder()
                        .status(CREATED.value())
                        .message("Success")
                        .data(participantService.save(request))
                        .build()
        );
    }

    @PutMapping("/{id}/update")
    @Operation(summary = "Create participant", description = "API dùng để tạo mới một đạo diễn của bộ phim !")
    public ResponseEntity<ApiResponse> updateParticipant(
            @PathVariable @Min(value = 1, message = "Id's participant is must be greater than 0 !") Integer id,
            @RequestBody @Valid ParticipantRequest request) {
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Success")
                        .data(participantService.update(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete participant", description = "API dùng để xóa một đạo diễn chưa có tồn tại trong bộ phim nào !")
    public ResponseEntity<ApiResponse> deleteParticipant(@PathVariable @Min(value = 1, message = "Id's participant is must be greater than 0 !") Integer id) {
        participantService.delete(id);
        return ResponseEntity.status(OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Delete participant successful")
                        .data(null)
                        .build()
        );

    }

}
