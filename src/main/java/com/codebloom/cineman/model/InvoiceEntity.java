package com.codebloom.cineman.model;


import com.codebloom.cineman.common.enums.InvoiceStatus;
import com.codebloom.cineman.common.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

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
@Check(constraints = "total_price >= 0 AND total_ticket > 0")
@Table(name = "invoices", uniqueConstraints = { @UniqueConstraint(columnNames = {"customer_id", "promotion_id"})})
public class InvoiceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;


    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "status", columnDefinition = "TINYINT")
    private InvoiceStatus status;

    @Column(name = "total_ticket")
    private Integer totalTicket;

    @Column(name = "payment_method", columnDefinition = "TINYINT")
    private PaymentMethod paymentMethod;

    @Column(name = "total_price")
    private Double totalPrice;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "DATETIME")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private Date updatedAt;

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

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<TicketEntity> tickets;
}

