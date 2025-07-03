package com.codebloom.cineman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "seat_types")
@Check(constraints = "price >= 0")
public class SeatTypeEntity implements Serializable {

    @Id
    @Column(name = "id", length = 25)
    private String id;

    @Column(name = "name", columnDefinition = "NVARCHAR(150)", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "seat_type", nullable = false)
    private Boolean status;

    @OneToMany(mappedBy = "seatType")
    @JsonIgnore
    private List<SeatEntity> seats;

}
