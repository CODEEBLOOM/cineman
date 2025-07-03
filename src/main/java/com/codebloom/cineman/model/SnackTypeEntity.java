package com.codebloom.cineman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "snack_types")
public class SnackTypeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snack_type_id")
    Integer id;

    @Column(name = "name", columnDefinition = "NVARCHAR(100)", nullable = false)
    String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(200)")
    String description;

    @OneToMany(mappedBy = "snackType")
    @JsonIgnore
    List<SnackEntity> snacks;

    @Column(name = "is_active", nullable = false)
    Boolean isActive;
}
