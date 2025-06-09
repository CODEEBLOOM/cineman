package com.codebloom.cineman.model;


import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.codebloom.cineman.common.enums.Method;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Column(name = "title", columnDefinition = "NVARCHAR(100)")
    String title;

    @Column(name = "description", columnDefinition = "NVARCHAR(500)")
    String description;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "method", columnDefinition = "TINYINT")
    Method method;

    @Column(name = "url", columnDefinition = "VARCHAR(200)")
    String url;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    Date updatedAt;

	@ManyToMany(mappedBy = "permissions")
    @JsonBackReference
    Set<RoleEntity> roles;
}
