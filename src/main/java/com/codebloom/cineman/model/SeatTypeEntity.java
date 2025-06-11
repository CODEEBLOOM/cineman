package com.codebloom.cineman.model;

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
@Table(name = "seat_types")
@Check(constraints = "price >= 0")
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
