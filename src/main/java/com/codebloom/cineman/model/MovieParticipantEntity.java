package com.codebloom.cineman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name = "movie_participants", uniqueConstraints = { @UniqueConstraint(columnNames = {"movie_id", "participant_id", "movie_role_id"})})
public class MovieParticipantEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonIgnore
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    @JsonIgnore
    private ParticipantEntity participant;

    @ManyToOne
    @JoinColumn(name = "movie_role_id", nullable = false)
    @JsonIgnore
    private MovieRoleEntity movieRole;
}
