package com.codebloom.cineman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "movie_variations")
public class MovieVariationEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", columnDefinition = "NVARCHAR(60)", unique = true, nullable = false)
    private String name;

    @Column(name = "status", columnDefinition = "TINYINT", nullable = false)
    private Boolean status;

    @OneToMany(mappedBy = "movieVariation")
    @JsonIgnore
    private List<MovieEntity> movies;

}
