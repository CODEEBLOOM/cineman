package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MovieParticipantRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
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

@RestController
@RequestMapping("/admin/movie-participant")
@RequiredArgsConstructor
@Tag(name= "Movie Director Controller")
@Validated
public class MovieParticipantAController {

    private final MovieParticipantService movieDirectorService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addDirectorForMovie(@RequestBody @Valid MovieParticipantRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Create participant success")
                        .data(movieDirectorService.addParticipantMovie(request))
                        .build()
        );
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateDirectorForMovie(
            @PathVariable @Min(value = 1, message = "Id of movie participant is must be greater than 0") Integer id,
            @RequestBody @Valid MovieParticipantRequest request) {

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update participant success")
                        .data(movieDirectorService.updateParticipantMovie(id, request))
                        .build()
        );
    }

    @DeleteMapping("/delete/{movieId}/{directorId}")
    public ResponseEntity<ApiResponse> deleteDirectorForMovie(
            @PathVariable @Min(value = 1, message = "Id's movie is must be greater than 0")  Integer movieId,
            @PathVariable @Min(value = 1, message = "Id's director is must be greater than 0") Integer directorId
    ) {
        movieDirectorService.deleteParticipantMovie(movieId, directorId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Delete director success")
                        .data(null)
                        .build()
        );
    }


}
