package com.codebloom.cineman.model;

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
@Table(name = "genres")
public class GenresEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genres_id", columnDefinition = "INT")
    private Integer genresId;

    @Column(name = "name", columnDefinition = "NVARCHAR(100)")
    private String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(200)")
    private String description;

    @OneToMany(mappedBy = "genres")
    private Set<MovieGenresEntity> movieGenres = new HashSet<>();
}
