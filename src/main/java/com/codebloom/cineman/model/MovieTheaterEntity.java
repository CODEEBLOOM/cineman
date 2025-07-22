package com.codebloom.cineman.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "movie_theaters")
public class MovieTheaterEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_theater_id")
    Integer movieTheaterId;

    @Column(columnDefinition = "nvarchar(200)", nullable = false)
    String name;

    @Column(columnDefinition = "nvarchar(200)", nullable = false)
    String address;

    @Column(name = "hotline", length = 20, nullable = false)
    String hotline;

    @Column(name = "status", nullable = false)
    Boolean status;

    @Column(name = "iframe_code", length = 300, nullable = false)
    String iframeCode;

    @ManyToOne
    @JoinColumn(name = "province_id", nullable = false)
    @JsonIgnore
    ProvinceEntity province;

    @OneToMany(mappedBy = "movieTheater")
    @JsonIgnore
    List<CinemaTheaterEntity> cinemaTheaters;
}

