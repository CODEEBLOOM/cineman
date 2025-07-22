package com.codebloom.cineman.model;


import com.codebloom.cineman.common.enums.CinemaTheaterStatus;
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
@Check(constraints = "number_of_rows > 0 and number_of_columns > 0 and regular_seat_row > 0 and vip_seat_row > 0 and double_seat_row > 0")
public class CinemaTheaterEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_theater_id")
    Integer cinemaTheaterId;

    @Column(name = "name", columnDefinition = "NVARCHAR(100)")
    String name;

    @Enumerated(EnumType.ORDINAL)
    CinemaTheaterStatus status;

    /* Bổ sung */
    @Column(name = "number_of_rows", nullable = false)
    Integer numberOfRows;

    @Column(name = "number_of_columns", nullable = false)
    Integer numberOfColumns;

    @Column(name = "regular_seat_row", nullable = false)
    Integer regularSeatRow;

    @Column(name = "vip_seat_row", nullable = false)
    Integer vipSeatRow;

    @Column(name = "double_seat_row", nullable = false)
    Integer doubleSeatRow;

    @ManyToOne
    @JoinColumn(name = "movie_theater_id", nullable = false)
    MovieTheaterEntity movieTheater;

    @ManyToOne
    @JoinColumn(name = "cinema_type_id", nullable = false)
    CinemaTypeEntity cinemaType;

    @OneToMany(mappedBy = "cinemaTheater")
    @JsonIgnore
    List<SeatEntity> seats;

    @OneToMany(mappedBy = "cinemaTheater")
    @JsonIgnore
    List<ShowTimeEntity> showTimes;

}
