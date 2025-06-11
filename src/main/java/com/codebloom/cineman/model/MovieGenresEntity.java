package com.codebloom.cineman.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "movie_genres", uniqueConstraints = { @UniqueConstraint(columnNames = {"movie_id", "movie_genre_id"})})
public class MovieGenresEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @JsonBackReference
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(name = "movie_genre_id")
    private GenresEntity genres;

}
