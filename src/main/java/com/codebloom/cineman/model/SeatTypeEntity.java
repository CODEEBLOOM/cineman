package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.SeatType;
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
    @Enumerated(EnumType.STRING)
    private SeatType id;

    @Column(name = "name", columnDefinition = "NVARCHAR(150)", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @OneToMany(mappedBy = "seatType")
    @JsonIgnore
    private List<SeatEntity> seats;

}
