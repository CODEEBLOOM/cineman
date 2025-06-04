package com.codebloom.cineman.model;


import com.codebloom.cineman.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cinema_theaters")
public class CinemaTheatersEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_theater_id")
    private Integer cinemaTheaterId;




    @Column(name = "room_number")
    private Integer roomNumber;

    @Column(name = "status", columnDefinition = "TINYINT")
    @Enumerated(EnumType.ORDINAL)
    private UserStatus status; // trạng thái phòng chiếu có thể sử enum của statusUser

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "number_of_rows")
    private Integer numberOfRows;

    @Column(name = "number_of_columns")
    private Integer numberOfColumns;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_theater_id")
    private MovieTheatersEntity movieTheater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type", referencedColumnName = "cinema_type_id") // cột type là FK
    private CinemaTypesEntity cinemaType;

}
