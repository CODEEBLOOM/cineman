package com.codebloom.cineman.model;


import com.codebloom.cineman.model.Id.MovieGenresId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movie_genres")
public class MovieGenresEntity implements Serializable {

    @EmbeddedId
    private MovieGenresId id;

    @ManyToOne
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private MovieEntity movie;

    @ManyToOne
    @MapsId("movieGenreId")
    @JoinColumn(name = "movie_genre_id")
    private GenresEntity genres;

}
