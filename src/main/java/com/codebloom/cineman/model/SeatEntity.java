package com.codebloom.cineman.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "seats")
public class SeatEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    Long id;

    @Column(name = "seat_number")
    Integer seatNumber;

    @Column(name = "row")
    Integer row;

    @ManyToOne
    @JoinColumn(name = "seat_type_id", nullable = false)
    SeatTypeEntity seatType;

    @OneToMany(mappedBy = "seat")
    List<TicketEntity> tickets;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    CinemaTheatersEntity CinemaTheater;

}
