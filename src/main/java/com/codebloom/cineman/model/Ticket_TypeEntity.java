package com.codebloom.cineman.model;

import java.io.Serializable;

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
@Table(name = "ticket_types")
public class Ticket_TypeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket_type")
    private Integer id;

    @Column(name = "name_type", columnDefinition = "NVARCHAR(100)")
    private String nameType;

    @Column(name = "description", columnDefinition = "NVARCHAR(200)")
    private String description;

    @Column(name = "price")
    private Double price;


}
