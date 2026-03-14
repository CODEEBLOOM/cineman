package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.GenderUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "participants")
public class ParticipantEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    Integer participantId;

    @Column(name = "birth_name", length = 100, nullable = false)
    String birthName;

    @Column(name = "nickname", length = 100, nullable = false)
    String nickname;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    GenderUser gender;

    @Column(name = "nationality", length = 100, nullable = false)
    String nationality;

    @Column(name = "mini_bio", length = 500)
    String miniBio;

    @Column(name = "avatar", length = 200, nullable = false)
    String avatar;

    @Column(name = "active", nullable = false)
    Boolean active;

    @OneToMany(mappedBy = "participant")
    @JsonIgnore
    Set<MovieParticipantEntity> movieParticipants;
}


