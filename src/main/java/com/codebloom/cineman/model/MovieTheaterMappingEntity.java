package com.codebloom.cineman.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movie_theater_mappings")
public class MovieTheaterMappingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_theater_mapping_id")
    private Integer movieTheaterMappingId;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(name = "movie_theater_id", nullable = false)
    private MovieTheaterEntity movieTheater;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
