package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.common.constant.MovieStatus;
import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.request.MoviePageQueryRequest;
import com.codebloom.cineman.controller.response.MetaResponse;
import com.codebloom.cineman.controller.response.MoviePageableResponse;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.*;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieStatusRepository;
import com.codebloom.cineman.service.MovieService;
import com.codebloom.cineman.service.MovieStatusService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieStatusRepository movieStatusRepository;
    private final MovieStatusService movieStatusService;
    private final ModelMapper modelMapper;


    /**
     * Find all movies
     * @param request MoviePageQueryRequest
     * @return request
     */
    @Override
    public MoviePageableResponse findAllByPage(MoviePageQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        MovieStatusEntity movieStatus = movieStatusRepository.findById(request.getStatus())
                .orElseThrow(() -> new DataNotFoundException("Movie status not found"));
        Page<MovieEntity> moviePage = movieRepository.findAllByStatus(movieStatus, pageable);
        return movieToMoviePageableResponse(moviePage);
    }

    /**
     * Find all movies
     * @param request MoviePageQueryRequest
     * @param movieTheaterId Integer
     * @return MoviePageableResponse
     */
    @Override
    public MoviePageableResponse findAllByPageAndFilter(MoviePageQueryRequest request, Integer movieTheaterId) {
        log.info("movieTheaterId: {}", movieTheaterId);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        MovieStatusEntity movieStatus = movieStatusService.findById(request.getStatus());

        Page<MovieEntity> page;
        if(request.getStatus().equals(MovieStatus.MOVIE_STATUS_SC)){
            page = movieRepository.findAllByReleaseDateGreaterThanAndStatus(new Date(), movieStatus, pageable);
        }else if (request.getStatus().equals(MovieStatus.MOVIE_STATUS_DB)){
            page = movieRepository.findAllByStatus(movieStatus, pageable);
        }else {
            page = movieRepository.findAllByStatusAndMovieTheaterId(movieStatus, movieTheaterId, pageable);
        }
        log.info("end findAllByPageAndFilter");
        return movieToMoviePageableResponse(page);
    }

    /**
     * Find all movies
     * @return List<MovieResponse>
     */
    @Override
    public List<MovieResponse> findAll() {
        List<MovieEntity> movies = movieRepository.findAll();
        List<MovieResponse> movieResponses = new ArrayList<>();
        movies.forEach(movie -> movieResponses.add(movieToMovieResponse(movie)));
        return movieResponses;
    }


    /**
     * Find movie
     * @param id Integer
     * @return MovieResponses
     */
    @Override
    public MovieResponse findById(Integer id) {
        log.info("Movie found: {}", id);
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Movie not found with id: " + id));
        if(movie.getStatus().getStatusId().equals(MovieStatus.MOVIE_STATUS_CNS)){
            throw new IllegalArgumentException("Movie is not available !");
        }
        log.info("Movie director found: {}", movie.getMovieParticipants());
        return movieToMovieResponse(movie);
    }

    /**
     * Find movie
     * @param id Integer
     * @param isEntity boolean
     * @return MovieEntity
     */
    @Override
    public MovieEntity findById(Integer id, boolean isEntity) {
        return movieRepository.findById(id)
                .orElseThrow(() ->new DataNotFoundException("Movie not found with id: " + id));
    }


    /**
     * Create movie
     * @param request MovieCreationRequest
     * @return MovieEntity
     */
    @Override
    @Transactional
    public MovieEntity save(MovieCreationRequest request) {
        MovieStatusEntity status ;
        Optional<MovieStatusEntity> movieStatus = movieStatusRepository.findById(MovieStatus.MOVIE_STATUS_SC);
        if(movieStatus.isEmpty()){
            MovieStatusEntity movieStatusEntity = new MovieStatusEntity();
            movieStatusEntity.setStatusId(MovieStatus.MOVIE_STATUS_DB);
            movieStatusEntity.setName("Đặc biệt");
            movieStatusEntity.setDescription("Trạng thái dành cho các bộ phim chưa có xuất chiếu tai rạp !");
            movieStatusEntity.setActive(true);
            status = movieStatusRepository.save(movieStatusEntity);
        }else {
            status = movieStatus.get();
        }
        MovieEntity movie = modelMapper.map(request, MovieEntity.class);
        movie.setStatus(status);
        log.info("Movie saved: {}", movie);
        return movieRepository.save(movie);
    }


    /**
     * Update movie
     * @param request MovieUpdateRequest
     * @return MovieResponse
     */
    @Override
    public MovieResponse update(MovieUpdateRequest request) {
        MovieEntity movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new DataNotFoundException("Movie not found with id: " + request.getMovieId()));
        MovieStatusEntity movieStatus = movieStatusService.findById(request.getStatus());

        MovieEntity updateMovie = modelMapper.map(request, MovieEntity.class);
        updateMovie.setCreatedAt(movie.getCreatedAt());
        updateMovie.setUpdatedAt(new Date());
        updateMovie.setStatus(movieStatus);

        updateMovie = movieRepository.save(updateMovie);
        return movieToMovieResponse(updateMovie);
    }


    /**
     * Delete movie
     * @param id Integer
     */
    @Override
    public void delete(Integer id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Movie not found with id: " + id));

        MovieStatusEntity cancelledStatus = movieStatusRepository.findById(MovieStatus.MOVIE_STATUS_CNS)
                .orElseGet(() -> {
                    MovieStatusEntity newStatus = new MovieStatusEntity();
                    newStatus.setStatusId(MovieStatus.MOVIE_STATUS_CNS);
                    newStatus.setName("Đã hủy");
                    newStatus.setDescription("Phim đã bị hủy ");
                    return movieStatusRepository.save(newStatus);
                });

        movie.setStatus(cancelledStatus);
        movieRepository.save(movie);
    }


    /**
     * Convert MovieEntity to MovieResponse
     * @param movie MovieEntity
     * @return MovieResponse
     */
    private MovieResponse movieToMovieResponse(MovieEntity movie) {
        List<GenresEntity> genres = new ArrayList<>();
        List<ParticipantEntity> directors = new ArrayList<>();
        List<ParticipantEntity> casts = new ArrayList<>();
        movie.getMovieGenres().forEach(movieGenre -> genres.add(movieGenre.getGenres()));
        movie.getMovieParticipants().forEach((movieParticipant) -> {
            if(movieParticipant.getMovieRole().getName().trim().equalsIgnoreCase("director")){
                directors.add(movieParticipant.getParticipant());
            }else if (movieParticipant.getMovieRole().getName().trim().equalsIgnoreCase("cast")){
                casts.add(movieParticipant.getParticipant());
            }
        });
        MovieResponse movieResponse = new MovieResponse();
        movieResponse.setMovieId(movie.getMovieId());
        movieResponse.setStatus(movie.getStatus().getName());
        movieResponse.setSynopsis(movie.getSynopsis());
        movieResponse.setDetailDescription(movie.getDetailDescription());
        movieResponse.setTitle(movie.getTitle());
        movieResponse.setReleaseDate(movie.getReleaseDate());
        movieResponse.setLanguage(movie.getLanguage());
        movieResponse.setDuration(movie.getDuration());
        movieResponse.setRating(movie.getRating());
        movieResponse.setAge(movie.getAge());
        movieResponse.setTrailerLink(movie.getTrailerLink());
        movieResponse.setPosterImage(movie.getPosterImage());
        movieResponse.setBannerImage(movie.getBannerImage());
        movieResponse.setDirectors(directors);
        movieResponse.setCasts(casts);
        movieResponse.setGenres(genres);
        movieResponse.setMovieVariants(movie.getMovieVariation());
        return movieResponse;
    }

    /**
     * Convert MovieEntity to MoviePageableResponse
     * @param page Page<MovieEntity>
     * @return MoviePageableResponse
     */
    private MoviePageableResponse movieToMoviePageableResponse(Page<MovieEntity> page) {
        List<MovieResponse> movieResponses = new ArrayList<>();
        page.getContent().forEach(movieEntity -> movieResponses.add(movieToMovieResponse(movieEntity)));
        MetaResponse metaResponse = MetaResponse.builder()
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .build();
        metaResponse.setCurrentPage(page.getNumber());
        metaResponse.setTotalPages(page.getTotalPages());
        metaResponse.setPageSize(page.getSize());
        metaResponse.setTotalElements(page.getNumberOfElements());

        MoviePageableResponse moviePageableResponse = new MoviePageableResponse();
        moviePageableResponse.setMeta(metaResponse);
        moviePageableResponse.setMovies(movieResponses);
        return moviePageableResponse;
    }

}
