package com.codebloom.cineman.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movie_status")
public class MovieStatusEntity implements Serializable {

    @Id
    @Column(name = "status_id", length = 25)
    private String statusId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "status")
    @JsonIgnore
    private List<MovieEntity> movies;


}


