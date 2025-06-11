package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.GenderUser;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "casts")
public class CastEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cast_id")
    Integer castId;

    @Column(name = "birth_name", columnDefinition = "NVARCHAR(100)")
    String birthName;

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

    @OneToMany(mappedBy = "cast")
    @JsonBackReference
    List<MovieCastEntity> movieCasts;

}
