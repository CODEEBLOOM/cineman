//package com.codebloom.cineman.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//
//import java.io.Serializable;
//import java.util.List;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Builder
//@Entity
//@Table(name = "seats")
//public class SeatEntity implements Serializable {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "seat_id")
//    Long id;
//
//    @Column(name = "seat_number")
//    Integer seatNumber;
//
//    @Column(name = "row")
//    Integer row;
//
//    @ManyToOne
//    @JoinColumn(name = "seat_type_id", nullable = false)
//    SeatTypeEntity seatType;
//
//    @OneToMany(mappedBy = "seat")
//    List<TicketEntity> tickets;
//
//    @ManyToOne
//    @JoinColumn(name = "room_id", nullable = false)
//    CinemaTheaterEntity cinemaTheater;
//
//}

package com.codebloom.cineman.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "seats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    Long id;

    @Column(name = "row_index") // ✅ Đúng theo DB
    Integer rowIndex;

    @Column(name = "column_index") // ✅ Đúng theo DB
    Integer columnIndex;

    @Column(name = "label") // ✅ Optional
    String label;

    @ManyToOne
    @JoinColumn(name = "seat_type_id", nullable = false) // ✅ Chính xác
    SeatTypeEntity seatType;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    CinemaTheaterEntity cinemaTheater;

    @OneToMany(mappedBy = "seat")
    List<TicketEntity> tickets;
}

