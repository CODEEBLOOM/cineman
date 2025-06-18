package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieStatusRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MovieStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/movie-status")
@Tag(name = "Movie Status Controller ( Admin Role )")
@Validated
public class MovieAStatusController {

    private final MovieStatusService movieStatusService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllMovieStatus() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Success")
                        .data(movieStatusService.findAll())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieStatusById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Success")
                        .data(movieStatusService.findById(id))
                        .build()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> handleAddMovieStatus(@RequestBody @Valid MovieStatusRequest request) {
        return ResponseEntity.status(CREATED).body(
                ApiResponse.builder()
                        .status(CREATED.value())
                        .message("Created")
                        .data(movieStatusService.save(request))
                        .build()
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> handleUpdateMovieStatus(@RequestBody @Valid MovieStatusRequest request) {
        return ResponseEntity.status(CREATED).body(
                ApiResponse.builder()
                        .status(OK.value())
                        .message("Updated successfully")
                        .data(movieStatusService.update(request))
                        .build()
        );
    }

    @DeleteMapping("/{id}/remove")
    public ResponseEntity<ApiResponse> handleRemoveMovieStatus(@PathVariable String id) {
        movieStatusService.deleteById(id);
        return ResponseEntity.status(NO_CONTENT).body(
                ApiResponse.builder()
                        .status(NO_CONTENT.value())
                        .message("Delete success with id: " + id)
                        .data(null)
                        .build()
        );
    }

}
