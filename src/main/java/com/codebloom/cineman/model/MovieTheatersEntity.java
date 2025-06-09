package com.codebloom.cineman.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "movie_theaters")
@Check(constraints = "total_cinema_theater > 0")
public class MovieTheatersEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_theater_id")
    Integer movieTheaterId;

    @Column(columnDefinition = "nvarchar(500)")
    String name;

    @Column(columnDefinition = "nvarchar(200)")
    String address;

    @Column(name = "total_cinema_theater")
    Integer numbersOfCinemaTheater;

    @Column(name = "hotline", length = 20)
    String hotline;

    @Column(name = "status")
    Boolean status;

    @Column(name = "iframe_code", length = 300)
    String iframeCode;

    @ManyToOne
    @JoinColumn(name = "province_id", nullable = false)
    ProvinceEntity province;

    @OneToMany(mappedBy = "movieTheater")
    List<CinemaTheatersEntity> cinemaTheaters;
}

