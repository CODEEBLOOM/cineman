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
@Table(name = "ticket_types")
public class TicketTypeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "ticket_type_id")
    private Integer id;

    @Column(name = "name_type", columnDefinition = "NVARCHAR(100)")
    private String nameType;

    @Column(name = "description", columnDefinition = "NVARCHAR(200)")
    private String description;

    @Column(name = "price")
    private Double price;


    @OneToMany(mappedBy = "ticketType")
    private List<TicketEntity> tickets;


}
