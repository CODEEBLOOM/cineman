package com.codebloom.cineman.model;

import com.codebloom.cineman.model.Id.MovieDirectorId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name = "movie_directors")
public class MovieDirectorEntity implements Serializable {
    @EmbeddedId
    private MovieDirectorId id;

    @ManyToOne
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private MovieEntity movie;

    @ManyToOne
    @MapsId("directorId")
    @JoinColumn(name = "director_id")
    private DirectorEntity director;
}
