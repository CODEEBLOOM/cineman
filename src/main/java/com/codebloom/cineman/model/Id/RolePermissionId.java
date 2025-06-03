package com.codebloom.cineman.model.Id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RolePermissionId implements Serializable {
    private String roleId;
    private Integer permissionId;
}
