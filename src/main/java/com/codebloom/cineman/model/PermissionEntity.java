package com.codebloom.cineman.model;


import com.codebloom.cineman.common.enums.Method;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "permissions")
public class PermissionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    Integer permissionId;

    @Column(name = "title", length = 100, nullable = false)
    String title;

    @Column(name = "description", length = 500)
    String description;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "method", nullable = false)
    Method method;

    @Column(name = "url", length = 200, nullable = false)
    String url;

    @Column(name = "category", length = 255)
    String category;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    Date updatedAt;

	@ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    Set<RoleEntity> roles;
}

