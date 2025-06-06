package com.codebloom.cineman.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "directors")
public class DirectorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "director_id", columnDefinition = "INT")
    private Integer directorId;

    @Column(name = "name", columnDefinition = "NVARCHAR(100)")
    private String name;

    @Column(name = "gender", columnDefinition = "BIT")
    private Boolean gender; // bởi vì trong database có kiểu dữ liệu là BIT rồi nên bên java phải gắn boolean

    @Column(name = "nationality", columnDefinition = "NVARCHAR(100)")
    private String nationality;


    @OneToMany(mappedBy = "director", orphanRemoval = true)
    private Set<MovieDirectorEntity> movieDirectors = new HashSet<>();
}

