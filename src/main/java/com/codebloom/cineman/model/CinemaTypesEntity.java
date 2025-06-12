package com.codebloom.cineman.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "cinema_types")
public class CinemaTypesEntity implements Serializable {

    @Id
    @Column(name = "cinema_type_id")
    String cinemaTypeId;

    @Column(columnDefinition = "NVARCHAR(200)")
    String name;

    @Column(columnDefinition = "NVARCHAR(250)")
    String description;

    @OneToMany(mappedBy = "cinemaType")
    @JsonBackReference
    List<CinemaTheatersEntity> cinemaTheaters;

}
