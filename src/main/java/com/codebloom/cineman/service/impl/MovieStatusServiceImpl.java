package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.controller.request.MovieStatusRequest;
import com.codebloom.cineman.exception.DataExistingException;
import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.MovieStatusEntity;
import com.codebloom.cineman.repository.MovieStatusRepository;
import com.codebloom.cineman.service.MovieStatusService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieStatusServiceImpl implements MovieStatusService {

    private final MovieStatusRepository movieStatusRepository;

    @Override
    public List<MovieStatusEntity> findAll() {
        return movieStatusRepository.findAll();
    }

    @Override
    public MovieStatusEntity findById(String movieStatusId) {
        return movieStatusRepository.findById(movieStatusId)
                .orElseThrow(() -> new DataNotFoundException("Status of movie not found with ID: " + movieStatusId));
    }

    @Override
    public MovieStatusEntity save(MovieStatusRequest movieStatusRequest) {
        Optional<MovieStatusEntity> movieStatusEntity = movieStatusRepository.findById(movieStatusRequest.getId());
        if(movieStatusEntity.isPresent()){
            throw new DataExistingException("Movie status already existing with ID: " + movieStatusRequest.getId());
        }else {
            MovieStatusEntity newMovieStatus = MovieStatusEntity.builder()
                    .statusId(movieStatusRequest.getId())
                    .name(movieStatusRequest.getName())
                    .description(movieStatusRequest.getDescription())
                    .build();
            return movieStatusRepository.save(newMovieStatus);
        }
    }

    @Override
    public MovieStatusEntity update(MovieStatusRequest request) {
        MovieStatusEntity updatedMovieStatusEntity = this.findById(request.getId());
        updatedMovieStatusEntity.setName(request.getName());
        updatedMovieStatusEntity.setDescription(request.getDescription());

        return movieStatusRepository.save(updatedMovieStatusEntity);
    }

    /**
     * Hàm thực hiện xóa mềm movie status
     * @param id : id duy nhât của movie status
     */
    @Override
    public void deleteById(String id) {
        MovieStatusEntity existMovieStatus = this.findById(id);
        existMovieStatus.setActive(false);
        movieStatusRepository.delete(existMovieStatus);
    }


}

