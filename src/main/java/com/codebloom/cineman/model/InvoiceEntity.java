package com.codebloom.cineman.model;


import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Check(constraints = "total_ticket >= 0 AND customer_id IS NOT NULL OR staff_id IS NOT NULL")
@Table(name = "invoices", uniqueConstraints = { @UniqueConstraint(columnNames = {"customer_id", "promotion_id", "invoice_id", "staff_id"}) })
public class InvoiceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "status", columnDefinition = "TINYINT", nullable = false)
    private InvoiceStatus status;
    @Column(name = "total_price", nullable = false)
    private Double totalPrice;
    @Column(name = "total_ticket", nullable = false)
    private Integer totalTicket;

    @Column(name = "payment_method", columnDefinition = "TINYINT")
    private PaymentMethod paymentMethod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "DATETIME")
    @CreationTimestamp
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", columnDefinition = "DATETIME")
    @CreationTimestamp
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private UserEntity customer;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private UserEntity staff;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private PromotionEntity promotion;

    @OneToMany(mappedBy = "invoice")
    @JsonIgnore
    private List<DetailBookingSnackEntity> detailBookingSnacks;

    @OneToMany(mappedBy = "invoice" )
    @JsonIgnore
    private List<TicketEntity> tickets;
}

