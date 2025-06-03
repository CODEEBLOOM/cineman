package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class RoleEntity implements Serializable {
	@Id
	@Column(name = "role_id", length = 25, columnDefinition = "VARCHAR(25)")
	private String roleId;

	    @Column(name = "name_role", columnDefinition = "NVARCHAR(100)")
	    private String name;
	@OneToMany(mappedBy = "role") // Tên biến trong UserRoleEntity
	private List<UserRoleEntity> userRoles; // cái này thì map đến  userroles

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<RolePermissionEntity> rolePermissions;
}
