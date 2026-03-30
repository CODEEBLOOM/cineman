package com.codebloom.cineman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "roles")
public class RoleEntity implements Serializable {
    @Id
    @Column(name = "role_id", length = 25)
    String roleId;

    @Column(name = "name_role", length = 100, nullable = false)
    String name;

    @Column(name = "status", nullable = false)
    @Builder.Default
    Boolean status = Boolean.TRUE;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    Date updatedAt;


	@ManyToMany
	@JoinTable(
            name= "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
			inverseJoinColumns = @JoinColumn(name = "permission_id"))
	@JsonIgnore
    Set<PermissionEntity> permissions;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    Set<UserRoleEntity> userRoles;
}

