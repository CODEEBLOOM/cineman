package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieStatusRequest;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.service.MovieStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/movie-status")
@Tag(name = "Movie Status Controller ( Admin Role )")
@Validated
public class MovieAStatusController {

    private final MovieStatusService movieStatusService;
    private Map<String, Object> response;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMovieStatus() {
        List<MovieStatusEntity> list = movieStatusService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("data", list);
        response.put("status", 200);
        response.put("message", "success");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMovieStatusById(@PathVariable String id) {
        MovieStatusEntity status = movieStatusService.findById(id);
        response = new HashMap<>();
        response.put("data", status);
        response.put("status", OK.value());
        response.put("message", "success");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> handleAddMovieStatus(@RequestBody @Valid MovieStatusRequest request) {
        MovieStatusEntity status = movieStatusService.save(request);
        response = new HashMap<>();
        response.put("data", status);
        response.put("status", CREATED.value());
        response.put("message", "success");
        return ResponseEntity.status(CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> handleUpdateMovieStatus(@RequestBody @Valid MovieStatusRequest request) {
        MovieStatusEntity status = movieStatusService.update(request);
        response = new HashMap<>();
        response.put("data", status);
        response.put("status", CREATED.value());
        response.put("message", "success");
        return ResponseEntity.status(CREATED).body(response);
    }

    @DeleteMapping("/{id}/remove")
    public ResponseEntity<Map<String, Object>> handleRemoveMovieStatus(@PathVariable String id) {
        movieStatusService.deleteById(id);
        response = new HashMap<>();
        response.put("data", null);
        response.put("status", NO_CONTENT.value());
        response.put("message", "Delete success with id: " + id);
        return ResponseEntity.status(NO_CONTENT).body(response);
    }

}
