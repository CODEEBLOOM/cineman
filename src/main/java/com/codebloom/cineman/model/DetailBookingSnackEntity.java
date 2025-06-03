package com.codebloom.cineman.model;


import com.codebloom.cineman.model.Id.DetailBookingSnackId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detail_booking_snacks")
public class DetailBookingSnackEntity implements Serializable {

    @EmbeddedId
    private DetailBookingSnackId id;

    @Column(name = "total_money", columnDefinition = "INT")
    private Integer totalMoney;

    @Column(name = "total_snack", columnDefinition = "INT")
    private Integer totalSnack;

    @ManyToOne
    @MapsId("invoiceId")
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;

    @ManyToOne
    @MapsId("snackId")
    @JoinColumn(name = "snack_id")
    private SnackEntity snack;
}


/*
* đây là 1 Entity có 1 khóa chính mà có 2 thuooc
* tính vì vậy nên sử dụng @IdClass để chỉ định khóa chính phức hợp
* */