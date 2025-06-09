package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


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

    @Column(name = "name_role", columnDefinition = "NVARCHAR(100)")
    String name;


	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
            name= "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
			inverseJoinColumns = @JoinColumn(name = "permission_id"))
	@JsonManagedReference
    Set<PermissionEntity> permissions;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    Set<UserRoleEntity> userRoles;
}
