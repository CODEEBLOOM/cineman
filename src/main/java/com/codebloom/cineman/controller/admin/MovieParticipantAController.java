package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieParticipantRequest;
import com.codebloom.cineman.model.MovieParticipantEntity;
import com.codebloom.cineman.service.MovieParticipantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/movie-participant")
@RequiredArgsConstructor
@Tag(name= "Movie Director Controller")
@Validated
public class MovieParticipantAController {

    private final MovieParticipantService movieDirectorService;
    private Map<String, Object> response;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addDirectorForMovie(@RequestBody @Valid MovieParticipantRequest request) {
        response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Add participant for movie success");
        response.put("data", movieDirectorService.addParticipantMovie(request));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Map<String, Object>> updateDirectorForMovie(
            @PathVariable @Min(value = 1, message = "Id of movie participant is must be greater than 0") Integer id,
            @RequestBody @Valid MovieParticipantRequest request) {

        response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Update participant for movie success");
        response.put("data", movieDirectorService.updateParticipantMovie(id, request));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{movieId}/{directorId}")
    public ResponseEntity<Map<String, Object>> deleteDirectorForMovie(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0")  Integer movieId,
            @PathVariable @Min(value = 1, message = "Id's director is must be greater than 0") Integer directorId
    ) {
        movieDirectorService.deleteParticipantMovie(movieId, directorId);
        response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Delete director for movie success");
        response.put("data", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
