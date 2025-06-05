package com.codebloom.cineman.controller;


import com.codebloom.cineman.common.enums.Rating;
import com.codebloom.cineman.controller.request.PageQueryRequest;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.service.MovieService;
import com.codebloom.cineman.service.MovieStatusService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mockup/movie")
public class MovieController {

    private final MovieStatusService movieStatusService;

    @Operation(summary = "Get all movies", description = "API dùng để lấy ra toàn bộ phim có trong hệ thống.")
    @GetMapping("/list")
    public Map<String, Object> getList(PageQueryRequest pageQueryRequest) {
        MovieStatusEntity movieStatusEntity = movieStatusService.findById("COMING_SOON");
        MovieResponse movieResponse1 = new MovieResponse();
        movieResponse1.setTitle("Phim doraemon 1");
        movieResponse1.setAge(19);
        movieResponse1.setDuration(120);
        movieResponse1.setDetailDescription("Phim hoạt hình vui nhộn");
        movieResponse1.setLanguage("Java");
        movieResponse1.setRating(Rating.EXCELLENT);
        movieResponse1.setStatus(movieStatusEntity);
        movieResponse1.setSynopsis("Phim hoạt hình vui nhộn về chú mèo máy đến từ tương lai");
        movieResponse1.setBannerImage("https://example.com/banner1.jpg");
        movieResponse1.setPosterImage("https://example.com/poster2.jpg");
        movieResponse1.setTrailerLink("https://example.com/trailer1.mp4");
        movieResponse1.setReleaseDate(new Date());
        List<MovieResponse> movieList = List.of(movieResponse1);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", movieList);
        result.put("message", "Movie List");
        result.put("status", HttpStatus.OK.value());

        return result;
    }

}
