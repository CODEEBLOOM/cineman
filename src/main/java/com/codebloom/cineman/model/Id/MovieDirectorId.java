package com.codebloom.cineman.model.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class MovieDirectorId implements Serializable {

    @Column(name = "director_id", columnDefinition = "INT")
    private Integer directorId;

    @Column(name = "movie_id", columnDefinition = "INT")
    private Integer movieId;
}
