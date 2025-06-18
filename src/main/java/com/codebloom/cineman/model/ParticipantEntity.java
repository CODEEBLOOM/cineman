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

    @Column(name = "active")
    Boolean active;

    @OneToMany(mappedBy = "participant")
    @JsonIgnore
    Set<MovieParticipantEntity> movieParticipants;
}

