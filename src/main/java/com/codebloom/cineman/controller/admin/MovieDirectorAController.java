package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.model.MovieDirectorEntity;
import com.codebloom.cineman.service.MovieDirectorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/movie-director")
@RequiredArgsConstructor
@Tag(name= "Movie Director Controller")
public class MovieDirectorAController {

    private final MovieDirectorService movieDirectorService;
    private Map<String, Object> response;

    @PostMapping("/add/{movieId}/{directorId}")
    public ResponseEntity<Map<String, Object>> addDirectorForMovie(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0")  Integer movieId,
            @PathVariable @Min(value = 1, message = "Id's director is must be greater than 0") Integer directorId) {

        MovieDirectorEntity movieDirector = movieDirectorService.addDirectorMovie(movieId, directorId);
        response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Add director for movie success");
        response.put("data", movieDirector);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}/update/{movieId}/{directorId}")
    public ResponseEntity<Map<String, Object>> updateDirectorForMovie(
            @PathVariable @Min(value = 1, message = "Id of movie director is must be greater than 0") Integer id,
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0")  Integer movieId ,
            @PathVariable @Min(value = 1, message = "Id's director is must be greater than 0") Integer directorId) {

        MovieDirectorEntity movieDirector = movieDirectorService.updateDirectorMovie(id, movieId, directorId);
        response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Update director for movie success");
        response.put("data", movieDirector);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{movieId}/{directorId}")
    public ResponseEntity<Map<String, Object>> deleteDirectorForMovie(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0")  Integer movieId,
            @PathVariable @Min(value = 1, message = "Id's director is must be greater than 0") Integer directorId
    ) {
        movieDirectorService.deleteDirectorMovie(movieId, directorId);
        response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Delete director for movie success");
        response.put("data", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
