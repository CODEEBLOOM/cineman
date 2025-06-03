package com.codebloom.cineman.model;

import java.io.Serializable;

import java.util.List;

import jakarta.persistence.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "snack_types")
public class SnackTypeEntity implements Serializable {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)

	    @Column(name = "snack_type_id")

	    @Column(name = "id_snack_type")

	    private Integer id;

	    @Column(name = "name_type", columnDefinition = "NVARCHAR(100)")
	    private String nameType;

	    @Column(name = "description", columnDefinition = "NVARCHAR(200)")
	    private String description;


	@OneToMany(mappedBy = "snackType")
	private List<SnackEntity> snacks;


}
