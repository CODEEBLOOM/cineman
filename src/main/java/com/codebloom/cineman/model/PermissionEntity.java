package com.codebloom.cineman.model;


import java.io.Serializable;
import java.util.Date;
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
@Table(name = "permissions")
public class PermissionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Integer permissionId;

    @Column(name = "title", columnDefinition = "NVARCHAR(100)")
    private String title;

    @Column(name = "description", columnDefinition = "NVARCHAR(500)")
    private String description;

    @Column(name = "method", columnDefinition = "VARCHAR(10)")
    private String method;

    @Column(name = "url", columnDefinition = "VARCHAR(200)")
    private String url;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;


	@OneToMany(mappedBy = "permission", cascade = CascadeType.ALL)
	private List<RolePermissionEntity> rolePermissions;
}
