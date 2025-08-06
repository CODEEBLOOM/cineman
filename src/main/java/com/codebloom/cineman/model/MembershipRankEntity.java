package com.codebloom.cineman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "membership_ranks")
public class MembershipRankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "required_point", nullable = false)
    private Integer requiredPoint;

    @Column(name = "return_points_ticket", nullable = false)
    private Double return_points_ticket;

    @Column(name = "return_points_snack", nullable = false)
    private Double return_points_snack;

    @Column(name = "priority_level", nullable = false)
    private Integer priorityLevel;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Boolean status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "DATETIME")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", columnDefinition = "DATETIME")
    @CreationTimestamp
    private LocalDateTime updatedAt;

    @OneToMany (mappedBy = "membershipRank")
    @JsonIgnore
    private List<UserEntity> users;
}
