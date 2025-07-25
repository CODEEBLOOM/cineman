package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.codebloom.cineman.common.enums.Rating;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @Column(name = "title", columnDefinition = "NVARCHAR(100)", nullable = false)
    private String title;

    @Column(name = "synopsis", columnDefinition = "NVARCHAR(500)")
    private String synopsis;

    @Column(name = "detail_description", columnDefinition = "NVARCHAR(1000)")
    private String detailDescription;

    @Column(name = "release_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "language", columnDefinition = "NVARCHAR(50)", nullable = false)
    private String language;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "rating")
    private Rating rating;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "trailer_link", length = 200, nullable = false)
    private String trailerLink;

    @Column(name = "poster_image", length = 200,  nullable = false)
    private String posterImage;

    @Column(name = "banner_image", length = 200, nullable = false)
    private String bannerImage;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "movie")
    @JsonIgnore
    private List<ShowTimeEntity> showTimes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "movie")
    @JsonIgnore
    private Set<MovieGenresEntity> movieGenres;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "movie")
    @JsonIgnore
    private Set<MovieParticipantEntity> movieParticipants;

    @ManyToOne
    @JoinColumn(name = "movie_variation_id")
    private MovieVariationEntity movieVariation;

}
