package com.codebloom.cineman.model;

import com.codebloom.cineman.model.Id.MovieCastId;
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
@Table(name = "movie_casts")
public class MovieCastEntity implements Serializable {
    @EmbeddedId
    private MovieCastId id;

    @ManyToOne
    @MapsId("castId")
    @JoinColumn(name = "cast_id")
    private CastEntity  cast;

    @ManyToOne
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private MovieEntity movie;


    @Column(name = "star")
    private Boolean star;




}
