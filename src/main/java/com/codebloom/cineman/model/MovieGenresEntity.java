package com.codebloom.cineman.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movie_genres", uniqueConstraints = { @UniqueConstraint(columnNames = {"movie_id", "movie_genre_id"})})
public class MovieGenresEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn( name = "movie_id")
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(name = "movie_genre_id")
    private GenresEntity genres;

}
