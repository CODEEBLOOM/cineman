package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.TicketType;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "name", columnDefinition = "NVARCHAR(100)", nullable = false)
    @Enumerated(EnumType.STRING)
    TicketType name;

    @Column(name = "description", columnDefinition = "NVARCHAR(200)")
    String description;

    @Column(name = "price", nullable = false)
    Double price;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    Boolean status;

    @OneToMany(mappedBy = "ticketType")
    @JsonIgnore
    List<TicketEntity> tickets;


}
