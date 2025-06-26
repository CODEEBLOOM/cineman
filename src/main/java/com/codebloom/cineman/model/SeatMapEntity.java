package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.SeatMapStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "seat_maps")
@Check(constraints = "regular_seat_row > 0 and number_of_rows >= 0 and number_of_columns>0 and double_seat_row > 0 and vip_seat_row > 0")
public class SeatMapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 150, columnDefinition = "NVARCHAR(250)")
    private String name;

    @Column(name = "number_of_rows")
    private Integer numberOfRows;

    @Column(name = "number_of_columns")
    private Integer numberOfColumns;

    @Column(name = "regular_seat_row")
    private Integer regularSeatRow;

    @Column(name = "vip_seat_row")
    private Integer vipSeatRow;

    @Column(name = "double_seat_row")
    private Integer doubleSeatRow;

    @Column(name = "description" , length = 250, columnDefinition = "NVARCHAR(250)")
    private String description;

    @Column(name = "status", columnDefinition = "TINYINT")
    @Enumerated(EnumType.ORDINAL)
    private SeatMapStatus status;

    @OneToMany(mappedBy = "seatMap")
    @JsonIgnore
    private List<CinemaTheaterEntity> cinemaTheater;

}
