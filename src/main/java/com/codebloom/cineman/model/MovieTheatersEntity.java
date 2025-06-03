package com.codebloom.cineman.model;


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
@Table(name = "movie_theaters")
public class MovieTheatersEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_theater_id")
    private Integer movieTheaterId;

    @Column(name = "name", columnDefinition = "nvarchar(500)")
    private String name;

    @Column(name = "address", columnDefinition = "nvarchar(200)")
    private String address;

    @Column(name = "numbers_of_cinema_theater")
    private Integer numbersOfCinemaTheater;

    @Column(name = "hotline", columnDefinition = "varchar(20)")
    private String hotline;

    @Column(name = "status", columnDefinition = "varchar(25)")
    private String status;


    @Column(name = "iframe_code", columnDefinition = "varchar(300)")
    private String iframeCode;

    @OneToMany(mappedBy = "movieTheater", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CinemaTheatersEntity> cinemaTheaters;
}

