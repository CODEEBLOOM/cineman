package com.codebloom.cineman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
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

    @Column(name = "name_role", columnDefinition = "NVARCHAR(100)")
    String name;


	@ManyToMany()
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
