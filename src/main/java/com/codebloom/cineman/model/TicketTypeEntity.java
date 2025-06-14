package com.codebloom.cineman.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "ticket_types")
@Check(constraints = "price >= 0")
public class TicketTypeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_type_id")
    Integer id;

    @Column(name = "name", columnDefinition = "NVARCHAR(100)")
    String nameType;

    @Column(name = "description", columnDefinition = "NVARCHAR(200)")
    String description;

    @Column(name = "price")
    Double price;

    @OneToMany(mappedBy = "ticketType")
    List<TicketEntity> tickets;


}
