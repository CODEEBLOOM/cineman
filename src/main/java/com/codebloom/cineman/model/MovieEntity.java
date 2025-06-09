package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.codebloom.cineman.common.enums.Rating;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "movies")
public class MovieEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    Integer movieId;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    MovieStatusEntity status;

    @Column(name = "title", columnDefinition = "NVARCHAR(100)")
    String title;

    @Column(name = "synopsis", columnDefinition = "NVARCHAR(500)")
    String synopsis;

    @Column(name = "detail_description", columnDefinition = "NVARCHAR(500)")
    String detailDescription;

    @Column(name = "release_date")
    @Temporal(TemporalType.DATE)
    Date releaseDate;

    @Column(name = "language", columnDefinition = "NVARCHAR(50)")
    String language;

    @Column(name = "duration")
    Integer duration;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "rating")
    Rating rating;

    Integer age;

    @Column(name = "trailer_link", length = 200)
    String trailerLink;

    @Column(name = "poster_image", length = 200)
    String posterImage;

    @Column(name = "banner_image", length = 200)
    String bannerImage;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Date updatedAt;

    @OneToMany(mappedBy = "movie")
    List<ShowTimeEntity> showTimes;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    Set<MovieDirectorEntity> movieDirectors;

    @OneToMany(mappedBy = "movie")
    Set<MovieGenresEntity> movieGenres;

    @OneToMany(mappedBy = "movie")
    List<MovieCastEntity> movieCastEntities;
}
