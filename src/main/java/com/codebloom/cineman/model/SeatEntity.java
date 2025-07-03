package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.SeatStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

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
@Check(constraints = "row_index > 0 and column_index > 0" )
public class SeatEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    Long id;

    @Column(name = "row_index", nullable = false)
    Integer rowIndex;

    @Column(name = "column_index", nullable = false)
    Integer columnIndex;

    @Column(name = "label",length = 10)
    String label;

    @ManyToOne
    @JoinColumn(name = "seat_type_id", nullable = false)
    SeatTypeEntity seatType;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    SeatStatus status;

    @OneToMany(mappedBy = "seat")
    @JsonIgnore
    List<TicketEntity> tickets;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    CinemaTheaterEntity cinemaTheater;

}
