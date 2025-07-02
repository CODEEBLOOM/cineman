package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "tickets")
@Check(constraints = "price >= 0 and limit_time > 0 ")
public class TicketEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @Column(name = "status", columnDefinition = "TINYINT")
    private TicketStatus status;

    @Column(name = "price")
    private Double price;

    @Column(name = "create_booking")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Date createBooking;

    @Column(name = "limit_time", nullable = false)
    Integer limitTime;

    Boolean expired;

    @ManyToOne
    @JoinColumn(name = "show_time_id", nullable = false)
    private ShowTimeEntity showTime;

    @ManyToOne
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketTypeEntity ticketType;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private SeatEntity seat;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;

    @Column(name = "rating")
    private Integer rating;
}
