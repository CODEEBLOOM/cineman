package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MovieCreationRequest;
import com.codebloom.cineman.controller.request.MovieUpdateRequest;
import com.codebloom.cineman.controller.response.MovieResponse;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.repository.MovieRepository;
import com.codebloom.cineman.repository.MovieStatusRepository;
import com.codebloom.cineman.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
   private MovieRepository movieRepository;

    @Autowired
    private MovieStatusRepository movieStatusRepository;


    @Override
    public Page<MovieEntity> findAllByPage(int page, int size, String sortBy, String sortDir, String searchTerm) {
        return null;
    }

    @Override
    public List<MovieEntity> findAll() {
        return List.of();
    }

    @Override
    public MovieResponse findById(Integer id) {
        return null;
    }

    @Override
    public Integer save(MovieCreationRequest request) {
        // nó tìm coi thử có name tên status là "Sắp chiếu" không nếu không thì nó tạo mới
        String statusName = "Sắp chiếu";
        MovieStatusEntity status = movieStatusRepository.findByName(statusName)
                .orElseGet(() -> {
                    MovieStatusEntity newStatus = new MovieStatusEntity();
                    newStatus.setName(statusName);
                    newStatus.setDescription("Phim sẽ chiếu trong thời gian tới");
                    return movieStatusRepository.save(newStatus);
                });

        // Tạo movie mới và gán status
        MovieEntity movie = new MovieEntity();
        movie.setTitle(request.getTitle());
        movie.setStatus(status);  // gán status tìm được
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
        movie.setIsActive(true);
        movie.setCreatedAt(request.getCreatedAt());
        movie.setUpdatedAt(request.getUpdatedAt());

        movieRepository.save(movie);
        return movie.getMovieId();
    }


    @Override
    public void update(MovieUpdateRequest request) {
        MovieEntity movie = new MovieEntity();
        movie.setTitle(request.getTitle());
        movie.setStatus(request.getStatus());
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
        movieRepository.save(movie);

    }

    @Override
    public void delete(Integer id) {
        MovieEntity movie = movieRepository.findById(id).orElse(null);
        if (movie != null) {
            movie.setIsActive(false);
            movieRepository.save(movie);
        }
    }
}
