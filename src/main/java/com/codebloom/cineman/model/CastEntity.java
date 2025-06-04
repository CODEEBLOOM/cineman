package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.GenderUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "casts")
public class CastEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cast_id")
    private Integer castId;

    @Column(name = "birth_name", columnDefinition = "NVARCHAR(100)")
    private String birthName;

    @Column(name = "nickname", columnDefinition = "NVARCHAR(100)")
    private String nickname;

    @Column(name = "gender", columnDefinition = "TINYINT")
    @Enumerated(EnumType.ORDINAL)
    private GenderUser gender; // giới tính của diễn viên, sử dụng enum GenderUser

    @Column(name = "nationality", columnDefinition = "NVARCHAR(100)")
    private String nationality;

    @Column(name = "mini_bio", columnDefinition = "NVARCHAR(500)")
    private String miniBio;

    @OneToMany(mappedBy = "cast")
    private List<MovieCastEntity> movieCasts; // danh sách các bộ phim mà diễn viên tham gia

}
