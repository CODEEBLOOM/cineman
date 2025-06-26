package com.codebloom.cineman.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "cinema_theaters")
public class CinemaTheaterEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_theater_id")
    Integer cinemaTheaterId;

    @Column(name = "name", columnDefinition = "NVARCHAR(100)")
    String name;

    Boolean status;

    @ManyToOne
    @JoinColumn(name = "movie_theater_id")
    MovieTheaterEntity movieTheater;

    @ManyToOne
    @JoinColumn(name = "cinema_type_id")
    CinemaTypeEntity cinemaType;

    @OneToMany(mappedBy = "cinemaTheater")
    @JsonIgnore
    List<SeatEntity> seats;

    @OneToMany(mappedBy = "cinemaTheater")
    @JsonIgnore
    List<ShowTimeEntity> showTimes;

    @ManyToOne
    @JoinColumn(name = "seat_map_id", nullable = false)
    SeatMapEntity seatMap;

}
