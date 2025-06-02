package com.codebloom.cineman.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "snack")
public class SnackEntity implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_snack")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_type", nullable = false)
    private Snack_TypeEntity snackType;

    @Column(name = "snack_name", columnDefinition = "NVARCHAR(100)")
    private String snackName;

    @Column(name = "price")
    private Double price;

    @Column(name = "image", columnDefinition = "VARCHAR(100)")
    private String image;

    @Column(name = "description", columnDefinition = "VARCHAR(200)")
    private String description;
}
