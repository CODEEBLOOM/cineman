package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.Exception.NotFoundException;
import com.codebloom.cineman.common.enums.MovieStatus;
import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.request.PageQueryRequest;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieStatusRepository;
import com.codebloom.cineman.service.MovieService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieStatusRepository movieStatusRepository;
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public Page<MovieResponse> findAllByPage(PageQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<MovieEntity> moviePage = movieRepository.findAll(pageable);
        return moviePage.map(movie -> modelMapper.map(movie, MovieResponse.class));
    }

    @Override
    public List<MovieResponse> findAll() {
        List<MovieEntity> movies = movieRepository.findAll();

        return movies.stream()
                .map(movie -> {
                    MovieResponse response = new MovieResponse();
                    response.setMovieId(movie.getMovieId());
                    response.setStatus(movie.getStatus().getName());
                    response.setTitle(movie.getTitle());
                    response.setSynopsis(movie.getSynopsis());
                    response.setDetailDescription(movie.getDetailDescription());
                    response.setReleaseDate(movie.getReleaseDate());
                    response.setLanguage(movie.getLanguage());
                    response.setDuration(movie.getDuration());
                    response.setRating(movie.getRating());
                    response.setAge(movie.getAge());
                    response.setTrailerLink(movie.getTrailerLink());
                    response.setPosterImage(movie.getPosterImage());
                    response.setBannerImage(movie.getBannerImage());

                    return response;
                })
                .toList();
    }


    @Override
    public MovieResponse findById(Integer id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim có ID: " + id));

        MovieResponse response = new MovieResponse();
        response.setMovieId(movie.getMovieId());
        response.setStatus(movie.getStatus().getName());
        response.setTitle(movie.getTitle());
        response.setSynopsis(movie.getSynopsis());
        response.setDetailDescription(movie.getDetailDescription());
        response.setReleaseDate(movie.getReleaseDate());
        response.setLanguage(movie.getLanguage());
        response.setDuration(movie.getDuration());
        response.setRating(movie.getRating());
        response.setAge(movie.getAge());
        response.setTrailerLink(movie.getTrailerLink());
        response.setPosterImage(movie.getPosterImage());
        response.setBannerImage(movie.getBannerImage());
        //response.setIsActive(movie.getIsActive();
        return response;
    }


    @Override
    public Integer save(MovieCreationRequest request) {
        // Enum đại diện cho status
        MovieStatus enumStatus = MovieStatus.COMING_SOON;
        String statusName = "Sắp chiếu";

        // Tìm status theo ID (enum)
        MovieStatusEntity status = movieStatusRepository.findById(enumStatus)
                .orElseGet(() -> {
                    MovieStatusEntity newStatus = new MovieStatusEntity();
                    newStatus.setStatusId(enumStatus); // enum, ví dụ COMING_SOON
                    newStatus.setName(statusName);     // hiển thị: Sắp chiếu
                    newStatus.setDescription("Phim sẽ chiếu trong thời gian tới");
                    return movieStatusRepository.save(newStatus);
                });

        // Tạo phim mới và gán status
        MovieEntity movie = new MovieEntity();
        movie.setTitle(request.getTitle());
        movie.setStatus(status);
        movie.setSynopsis(request.getSynopsis());
        movie.setDetailDescription(request.getDetailDescription());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setLanguage(request.getLanguage());
        movie.setDuration(request.getDuration());
        movie.setRating(request.getRating());
        movie.setAge(request.getAge());
        movie.setTrailerLink(request.getTrailerLink());
        movie.setPosterImage(request.getPosterImage());
        movie.setBannerImage(request.getBannerImage());
        movie.setCreatedAt(request.getCreatedAt());
        movie.setUpdatedAt(request.getUpdatedAt());

        movieRepository.save(movie);
        return movie.getMovieId();
    }


    @Override
    public MovieResponse update(Integer movieId, MovieUpdateRequest request) {
        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        movie.setTitle(request.getTitle());
        movie.setSynopsis(request.getSynopsis());
        movie.setDetailDescription(request.getDetailDescription());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setLanguage(request.getLanguage());
        movie.setDuration(request.getDuration());
        movie.setRating(request.getRating());
        movie.setAge(request.getAge());
        movie.setTrailerLink(request.getTrailerLink());
        movie.setPosterImage(request.getPosterImage());
        movie.setBannerImage(request.getBannerImage());
        movie.setCreatedAt(request.getCreatedAt());
        movie.setUpdatedAt(request.getUpdatedAt());

        if (request.getStatus() != null) {
            MovieStatusEntity status = movieStatusRepository.findById(request.getStatus())
                    .orElseThrow(() -> new RuntimeException("Trạng thái không hợp lệ"));
            movie.setStatus(status);
        }

        MovieEntity updated = movieRepository.save(movie);
        return modelMapper.map(updated, MovieResponse.class);
    }


    @Override
    public void delete(Integer id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phim với ID = " + id));

        MovieStatusEntity cancelledStatus = movieStatusRepository.findById(MovieStatus.CANCELLED)
                .orElseGet(() -> {
                    MovieStatusEntity newStatus = new MovieStatusEntity();
                    newStatus.setStatusId(MovieStatus.CANCELLED);
                    newStatus.setName("Đã hủy");
                    newStatus.setDescription("Phim đã bị hủy ");
                    return movieStatusRepository.save(newStatus);
                });

        movie.setStatus(cancelledStatus);
        movieRepository.save(movie);
    }


}
