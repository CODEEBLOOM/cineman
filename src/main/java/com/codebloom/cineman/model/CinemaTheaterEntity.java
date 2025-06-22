package com.codebloom.cineman.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "cinema_theaters")
@Check(constraints = "total_seats > 0 and number_of_rows >= 0 and number_of_columns>0")
public class CinemaTheaterEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_theater_id")
    Integer cinemaTheaterId;

    @Column(name = "room_number")
    Integer roomNumber;

    Boolean status;

    @Column(name = "total_seats")
    Integer totalSeats;

    @Column(name = "number_of_rows")
    Integer numberOfRows;

    @Column(name = "number_of_columns")
    Integer numberOfColumns;


    @ManyToOne
    @JoinColumn(name = "movie_theater_id")
    MovieTheaterEntity movieTheater;

    @ManyToOne
    @JoinColumn(name = "cinema_type_id")
    CinemaTypesEntity cinemaType;

    @OneToMany(mappedBy = "cinemaTheater")
    @JsonIgnore
    List<SeatEntity> seats;

    @OneToMany(mappedBy = "cinemaTheater")
    @JsonIgnore
    List<ShowTimeEntity> showTimes;

}
