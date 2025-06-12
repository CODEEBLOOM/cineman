package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.DirectorEntity;
import com.codebloom.cineman.model.MovieDirectorEntity;
import com.codebloom.cineman.model.MovieEntity;
import com.codebloom.cineman.repository.MovieDirectorRepository;
import com.codebloom.cineman.service.MovieDirectorService;
import com.codebloom.cineman.service.DirectorService;
import com.codebloom.cineman.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieDirectorServiceImpl implements MovieDirectorService {

    private final MovieDirectorRepository directorRepository;
    private final MovieService movieService;
    private final DirectorService directorService;

    @Override
    public MovieDirectorEntity addDirectorMovie( Integer movieId, Integer directorId) {
        DirectorEntity exitingDirector = directorService.findById(directorId);
        MovieEntity existingMovie = movieService.findById(movieId, true);

        MovieDirectorEntity modifiedDirector = new MovieDirectorEntity();
        modifiedDirector.setDirector(exitingDirector);
        modifiedDirector.setMovie(existingMovie);

        return directorRepository.save(modifiedDirector);
    }

    @Override
    public MovieDirectorEntity updateDirectorMovie(Integer id, Integer movieId, Integer directorId) {
        MovieDirectorEntity existMovieDirector = directorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Director of Movie not found with id: " + id));
        return this.addDirectorMovie(directorId, movieId);
    }

    @Override
    public void deleteDirectorMovie(Integer movieId, Integer directorId) {
        DirectorEntity exitingDirector = directorService.findById(directorId);
        MovieEntity existingMovie = movieService.findById(movieId, true);

        MovieDirectorEntity existMovieDirector = directorRepository.findByMovieAndDirector(existingMovie, exitingDirector)
                .orElseThrow(() -> new DataNotFoundException("Director of Movie not found with id's movie: " + movieId + " and  id's director: " + directorId));
        directorRepository.delete(existMovieDirector);
    }
}
