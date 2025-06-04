package com.codebloom.cineman.model;

import com.codebloom.cineman.model.Id.RolePermissionId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "role_permission")
@IdClass(RolePermissionId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionEntity implements Serializable{

    @Id
    @Column(name = "role_id", length = 25)
    private String roleId;

    @Id
    @Column(name = "permission_id")
    private Integer permissionId;

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RoleEntity role;

    @ManyToOne
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    private PermissionEntity permission;
}

