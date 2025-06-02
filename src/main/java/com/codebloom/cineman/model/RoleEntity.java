package com.codebloom.cineman.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
	    @Column(name = "role_id", columnDefinition = "VARCHAR(25)")
	    private String roleId;

	    @Column(name = "name_role", columnDefinition = "NVARCHAR(100)")
	    private String name;

}
