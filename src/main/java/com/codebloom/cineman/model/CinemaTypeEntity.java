package com.codebloom.cineman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CinemaTypeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_type_id")
    Integer cinemaTypeId;

    @Column(name="code", nullable = false, length = 25)
    String code;

    @Column(columnDefinition = "NVARCHAR(200)", nullable = false)
    String name;

    @Column(columnDefinition = "NVARCHAR(250)")
    String description;

    @Column(name = "status", nullable = false)
    Boolean status;

    @OneToMany(mappedBy = "cinemaType")
    @JsonIgnore
    List<CinemaTheaterEntity> cinemaTheaters;


}
