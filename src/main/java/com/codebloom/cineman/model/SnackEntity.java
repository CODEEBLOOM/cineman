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
@Table(name = "snacks")
public class SnackEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snack_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "snack_type_id", nullable = false)
    private SnackTypeEntity snackType;


    @Column(name = "snack_name", columnDefinition = "NVARCHAR(100)")
    private String snackName;

    @Column(name = "price")
    private Double price;

    @Column(name = "image", columnDefinition = "VARCHAR(100)")
    private String image;

    @Column(name = "description", columnDefinition = "VARCHAR(200)")
    private String description;

    @OneToMany(mappedBy = "snack")
    private List<DetailBookingSnackEntity> detailBookingSnacks;
}
