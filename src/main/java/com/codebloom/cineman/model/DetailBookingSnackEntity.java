package com.codebloom.cineman.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "detail_booking_snacks", uniqueConstraints = { @UniqueConstraint(columnNames = {"invoice_id", "snack_id"})})
@Check(constraints = "total_money >=0 AND total_snack >= 0")
public class DetailBookingSnackEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "total_money")
    private Integer totalMoney;

    @Column(name = "total_snack")
    private Integer totalSnack;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;

    @ManyToOne
    @JoinColumn(name =  "snack_id")
    private SnackEntity snack;
}