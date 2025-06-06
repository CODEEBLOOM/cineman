package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.codebloom.cineman.common.enums.Rating;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movies")
public class MovieEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Integer movieId;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private MovieStatusEntity status;

    @Column(name = "title_movie", columnDefinition = "NVARCHAR(100)")
    private String title;

    @Column(name = "synopsis", columnDefinition = "NVARCHAR(500)")
    private String synopsis;

    @Column(name = "detail_description", columnDefinition = "NVARCHAR(500)")
    private String detailDescription;

    @Column(name = "release_date")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    @Column(name = "language", columnDefinition = "NVARCHAR(50)")
    private String language;

    @Column(name = "duration")
    private Integer duration;

    @Enumerated(EnumType.ORDINAL)  // hoặc EnumType.ORDINAL
    @Column(name = "rating")
    private Rating rating;

    @Column(name = "age")
    private Integer age;

    @Column(name = "trailer_link", columnDefinition = "VARCHAR(200)")
    private String trailerLink;

    @Column(name = "poster_image", columnDefinition = "VARCHAR(100)")
    private String posterImage;

    @Column(name = "banner_image", columnDefinition = "VARCHAR(100)")
    private String bannerImage;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "is_active", columnDefinition = "BIT")
    private boolean IsActive ;

    @OneToMany(mappedBy = "movie")
    private List<ShowTimeEntity> showTimes;


    @OneToMany(mappedBy = "movie", orphanRemoval = true)
    private Set<MovieDirectorEntity> movieDirectors ;

    @OneToMany(mappedBy = "movie")
    private Set<MovieGenresEntity> movieGenres ;

	@OneToMany(mappedBy = "movie")
	private  List<MovieCastEntity> movieCastEntities;
}
