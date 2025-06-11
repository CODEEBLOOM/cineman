package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.Exception.NotFoundException;
import com.codebloom.cineman.common.enums.MovieStatus;
import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.request.PageQueryRequest;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.controller.response.MoviePageableResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.GenresEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieStatusRepository;
import com.codebloom.cineman.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final  MovieStatusRepository movieStatusRepository;
    private final ModelMapper modelMapper;


    @Override
    public MoviePageableResponse findAllByPage(PageQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<MovieEntity> moviePage = movieRepository.findAll(pageable);
        return movieToMoviePageableResponse(moviePage);
    }

    @Override
    public List<MovieResponse> findAll() {
        List<MovieEntity> movies = movieRepository.findAll();
        List<MovieResponse> movieResponses = new ArrayList<>();
        movies.forEach(movie -> movieResponses.add(movieToMovieResponse(movie)));
        return movieResponses;
    }


    @Override
    public MovieResponse findById(Integer id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Movie not found with id: " + id));
        return movieToMovieResponse(movie);
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
                    newStatus.setStatusId(enumStatus.toString()); // enum, ví dụ COMING_SOON
                    newStatus.setName(statusName);     // hiển thị: Sắp chiếu
                    newStatus.setDescription("Phim sẽ chiếu trong thời gian tới");
                    return movieStatusRepository.save(newStatus);
                });

        /* Tạo phim mới và gán status */
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

        modelMapper.map(request, movie);

        if (request.getStatus() != null) {
            MovieStatusEntity status = movieStatusRepository.findById(MovieStatus.valueOf(request.getStatus()))
                    .orElseThrow(() -> new RuntimeException("Trạng thái không hợp lệ"));
            movie.setStatus(status);
        }

        MovieEntity updated = movieRepository.save(movie);
        MovieResponse response = modelMapper.map(updated, MovieResponse.class);
        response.setStatus(updated.getStatus().getName());
        return response;
    }


    @Override
    public void delete(Integer id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phim với ID = " + id));

        MovieStatusEntity cancelledStatus = movieStatusRepository.findById(MovieStatus.CANCELLED)
                .orElseGet(() -> {
                    MovieStatusEntity newStatus = new MovieStatusEntity();
                    newStatus.setStatusId(MovieStatus.CANCELLED.toString());
                    newStatus.setName("Đã hủy");
                    newStatus.setDescription("Phim đã bị hủy ");
                    return movieStatusRepository.save(newStatus);
                });

        movie.setStatus(cancelledStatus);
        movieRepository.save(movie);
    }

    @Override
    public List<MovieResponse> findByTitle(String title) {
        List<MovieEntity> movies = movieRepository.findByTitleContainingIgnoreCase(title);
        List<MovieResponse> movieResponses = new ArrayList<>();
        movies.forEach(movie -> movieResponses.add(movieToMovieResponse(movie)));
        return movieResponses;
    }

    private MovieResponse movieToMovieResponse(MovieEntity movie) {
        List<GenresEntity> genres = new ArrayList<>();
        movie.getMovieGenres().forEach(movieGenres -> genres.add(movieGenres.getGenres()));
        MovieResponse movieResponse = new MovieResponse();
        movieResponse.setMovieId(movie.getMovieId());
        movieResponse.setStatus(movie.getStatus().getName());
        movieResponse.setTitle(movie.getTitle());
        movieResponse.setReleaseDate(movie.getReleaseDate());
        movieResponse.setLanguage(movie.getLanguage());
        movieResponse.setDuration(movie.getDuration());
        movieResponse.setRating(movie.getRating());
        movieResponse.setTrailerLink(movie.getTrailerLink());
        movieResponse.setPosterImage(movie.getPosterImage());
        movieResponse.setBannerImage(movie.getBannerImage());
        movieResponse.setGenres(genres);
        return movieResponse;
    }

    private MoviePageableResponse movieToMoviePageableResponse(Page<MovieEntity> page) {
        List<MovieResponse> movieResponses = new ArrayList<>();
        page.getContent().forEach(movieEntity -> movieResponses.add(movieToMovieResponse(movieEntity)));
        MetaResponse metaResponse = new MetaResponse();
        metaResponse.setCurrentPage(page.getNumber() + 1);
        metaResponse.setTotalPages(page.getTotalPages());
        metaResponse.setPageSize(page.getSize());
        metaResponse.setTotalElements(page.getNumberOfElements());

        MoviePageableResponse moviePageableResponse = new MoviePageableResponse();
        moviePageableResponse.setMeta(metaResponse);
        moviePageableResponse.setMovies(movieResponses);
        return moviePageableResponse;
    }

}
