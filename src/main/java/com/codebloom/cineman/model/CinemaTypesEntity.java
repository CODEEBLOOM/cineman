package com.codebloom.cineman.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cinema_types")
public class CinemaTypesEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_type_id")
    private Integer cinemaTypeId;

    @Column(name = "code_type", columnDefinition = "NVARCHAR(200)")
    private String codeType;

    @Column(name = "description", columnDefinition = "NVARCHAR(255)")
    private String description;

    @OneToMany(mappedBy = "cinemaType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CinemaTheatersEntity> cinemaTheaters;

}
