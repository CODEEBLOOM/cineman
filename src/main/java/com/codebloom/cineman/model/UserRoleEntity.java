package com.codebloom.cineman.model;


import com.codebloom.cineman.model.Id.UserRoleId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_roles")
public class UserRoleEntity implements Serializable {

    @EmbeddedId
    private UserRoleId id;

    @Column(name = "name", columnDefinition = "NVARCHAR(50)")
    private String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(500)")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RoleEntity role; // map đến bảng role
}
