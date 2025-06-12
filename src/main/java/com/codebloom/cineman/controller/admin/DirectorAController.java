package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.response.DirectorRequest;
import com.codebloom.cineman.model.DirectorEntity;
import com.codebloom.cineman.service.DirectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/director")
@Tag(name = "Movie Status Controller ( Admin Role )")
@Validated
public class DirectorAController {

    private final DirectorService directorService;
    private Map<String, Object> response;

    @GetMapping("/all")
    @Operation(summary = "get directors", description = "API dùng để lấy tất cả đạo diễn!")
    public ResponseEntity<Map<String, Object>> getDirectors() {
        List<DirectorEntity> directors = directorService.findAll();
        response = new LinkedHashMap<>();
        response.put("message", "successful");
        response.put("status", OK.value());
        response.put("data", directors);
        return ResponseEntity.status(OK).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "get director by id", description = "API dùng để lấy đạo diễn theo id !")
    public ResponseEntity<Map<String, Object>> getDirector(@PathVariable Integer id) {
        DirectorEntity director = directorService.findById(id);
        response = new LinkedHashMap<>();
        response.put("message", "successful");
        response.put("status", OK.value());
        response.put("data", director);
        return ResponseEntity.status(OK).body(response);
    }

    @PostMapping("/add")
    @Operation(summary = "Create director", description = "API dùng để tạo mới một đạo diễn của bộ phim !")
    public ResponseEntity<Map<String, Object>> createDirector(@RequestBody @Valid DirectorRequest request) {
        DirectorEntity saveDirector = directorService.save(request);
        response = new LinkedHashMap<>();
        response.put("message", "Create director successful");
        response.put("status", CREATED.value());
        response.put("data", saveDirector);
        return ResponseEntity.status(CREATED).body(response);
    }

    @PutMapping("/{id}/update")
    @Operation(summary = "Create director", description = "API dùng để tạo mới một đạo diễn của bộ phim !")
    public ResponseEntity<Map<String, Object>> updateDirector(@PathVariable @Min(value = 1, message = "Id's director is must be greater than 0 !") Integer id, @RequestBody @Valid DirectorRequest request) {
        DirectorEntity updateDirector = directorService.update(id, request);
        response = new LinkedHashMap<>();
        response.put("message", "Update director successful");
        response.put("status", OK.value());
        response.put("data", updateDirector);
        return ResponseEntity.status(OK).body(response);
    }

    @PutMapping("/{id}/delete")
    @Operation(summary = "Delete director", description = "API dùng để xóa một đạo diễn chưa có tồn tại trong bộ phim nào !")
    public ResponseEntity<Map<String, Object>> deleteDirector(@PathVariable @Min(value = 1, message = "Id's director is must be greater than 0 !") Integer id) {
        response = new LinkedHashMap<>();
        if(directorService.delete(id) == 1) {
            response.put("message", "Delete director successful");
            response.put("status", OK.value());
            return ResponseEntity.status(OK).body(response);
        }
        response.put("message", "Director cannot be deleted !");
        response.put("status", BAD_REQUEST.value());
        response.put("data", null);
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

}
