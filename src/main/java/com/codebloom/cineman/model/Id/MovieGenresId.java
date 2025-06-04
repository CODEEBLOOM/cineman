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
public class MovieGenresId implements Serializable {

    @Column(name = "movie_id", columnDefinition = "INT")
    private Integer movieId;

    @Column(name = "movie_genre_id",columnDefinition = "INT")
    private Integer movieGenreId;
}
