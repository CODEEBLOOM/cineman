package com.codebloom.cineman.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoices", uniqueConstraints = { @UniqueConstraint(columnNames = {"customer_id", "promotion_id"})})
public class InvoiceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id", columnDefinition = "BIGINT")
    private Long invoiceId;


    @Column(name = "email", nullable = false, unique = true, columnDefinition = "NVARCHAR(100)")
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, columnDefinition = "NVARCHAR(100)")
    private String phoneNumber;

    @Column(name = "status", columnDefinition = "TINYINT")
    private Integer status;

    @Column(name = "total_ticket", columnDefinition = "INT")
    private Integer totalTicket;

    @Column(name = "payment_method", columnDefinition = "TINYINT")
    private Integer paymentMethod;

    @Column(name = "total_price", columnDefinition = "FLOAT")
    private Double totalPrice;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "DATETIME")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private Date updatedAt;

    @Column(name = "is_active", columnDefinition = "BIT")
    private boolean IsActive ;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private UserEntity customer;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private UserEntity staff;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private PromotionEntity promotion;


    @OneToMany(mappedBy = "invoice")
    private List<DetailBookingSnackEntity> detailBookingSnacks;

    @OneToMany(mappedBy = "invoice")
    private List<TicketEntity> tickets;
}

