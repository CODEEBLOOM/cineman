package com.codebloom.cineman.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name = "movie_directors",uniqueConstraints = { @UniqueConstraint(columnNames = {"movie_id", "director_id"})})
public class MovieDirectorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonBackReference
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    @JsonBackReference
    private DirectorEntity director;
}
