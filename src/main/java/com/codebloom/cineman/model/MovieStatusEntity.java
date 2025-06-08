package com.codebloom.cineman.model;


import com.codebloom.cineman.common.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movie_status")
public class MovieStatusEntity implements Serializable {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "status_id", length = 25)
    private MovieStatus statusId;

    @Column(name = "name", columnDefinition = "NVARCHAR(100)")
    private String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(250)")
    private String description;

    @OneToMany(mappedBy = "status")
    private List<MovieEntity> movies;


}

