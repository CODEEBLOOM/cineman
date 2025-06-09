package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.GenderUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "directors")
public class DirectorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "director_id")
    Integer directorId;

    @Column(name = "fullname", columnDefinition = "NVARCHAR(100)")
    String fullname;

    @Column(name = "nickname", columnDefinition = "NVARCHAR(100)")
    String nickname;

    @Column(name = "gender", columnDefinition = "TINYINT")
    @Enumerated(EnumType.ORDINAL)
    GenderUser gender;

    @Column(name = "nationality", columnDefinition = "NVARCHAR(100)")
    String nationality;

    @Column(name = "mini_bio", columnDefinition = "NVARCHAR(500)")
    String miniBio;

    @Column(length = 200)
    String avatar;

    @OneToMany(mappedBy = "director", cascade = CascadeType.ALL)
    Set<MovieDirectorEntity> movieDirectors;
}

