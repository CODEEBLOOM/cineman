package com.codebloom.cineman.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "genres")
public class GenresEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genres_id")
    private Integer genresId;

    @Column(name = "name", columnDefinition = "NVARCHAR(100)", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(250)")
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "genres")
    @JsonIgnore
    private List<MovieGenresEntity> movieGenres ;
}
