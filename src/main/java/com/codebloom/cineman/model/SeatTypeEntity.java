package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seat_types")
public class SeatTypeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_type_id")
    private Integer seatTypeId;

    @Column(name = "name", columnDefinition = "NVARCHAR(200)")
    private String name;

    @Column(name = "price")
    private Double price;

    @OneToMany(mappedBy = "seatType")
    private List<SeatEntity> seats;
}
