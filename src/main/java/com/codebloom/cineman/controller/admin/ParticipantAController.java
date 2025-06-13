package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.ParticipantRequest;
import com.codebloom.cineman.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/participant")
@Tag(name = "Movie Status Controller ( Admin Role )")
@Validated
public class ParticipantAController {

    private final ParticipantService participantService;
    private Map<String, Object> response;

    @GetMapping("/all")
    @Operation(summary = "get participants", description = "API dùng để lấy tất cả đạo diễn!")
    public ResponseEntity<Map<String, Object>> getDirectors() {
        response = new LinkedHashMap<>();
        response.put("message", "successful");
        response.put("status", OK.value());
        response.put("data", participantService.findAll());
        return ResponseEntity.status(OK).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "get participant by id", description = "API dùng để lấy đạo diễn theo id !")
    public ResponseEntity<Map<String, Object>> getDirector(@PathVariable Integer id) {
        response = new LinkedHashMap<>();
        response.put("message", "successful");
        response.put("status", OK.value());
        response.put("data", participantService.findById(id));
        return ResponseEntity.status(OK).body(response);
    }

    @PostMapping("/add")
    @Operation(summary = "Create participant", description = "API dùng để tạo mới một đạo diễn của bộ phim !")
    public ResponseEntity<Map<String, Object>> createDirector(@RequestBody @Valid ParticipantRequest request) {

        response = new LinkedHashMap<>();
        response.put("message", "Create participant successful");
        response.put("status", CREATED.value());
        response.put("data", participantService.save(request));
        return ResponseEntity.status(CREATED).body(response);
    }

    @PutMapping("/{id}/update")
    @Operation(summary = "Create participant", description = "API dùng để tạo mới một đạo diễn của bộ phim !")
    public ResponseEntity<Map<String, Object>> updateDirector(
            @PathVariable @Min(value = 1, message = "Id's participant is must be greater than 0 !") Integer id,
            @RequestBody @Valid ParticipantRequest request) {
        response = new LinkedHashMap<>();
        response.put("message", "Update participant successful");
        response.put("status", OK.value());
        response.put("data", participantService.update(id, request));
        return ResponseEntity.status(OK).body(response);
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete participant", description = "API dùng để xóa một đạo diễn chưa có tồn tại trong bộ phim nào !")
    public ResponseEntity<Map<String, Object>> deleteDirector(@PathVariable @Min(value = 1, message = "Id's participant is must be greater than 0 !") Integer id) {
        response = new LinkedHashMap<>();
        response.put("data", null);
        if(participantService.delete(id) == 1) {
            response.put("message", "Delete participant successful");
            response.put("status", OK.value());
            return ResponseEntity.status(OK).body(response);
        }
        response.put("message", "Director cannot be deleted !");
        response.put("status", BAD_REQUEST.value());
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

}
