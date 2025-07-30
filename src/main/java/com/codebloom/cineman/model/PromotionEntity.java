package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.StatusPromotion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "promotions")
public class PromotionEntity implements Serializable  {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long id;

    @Column(columnDefinition = "NVARCHAR(100)", nullable = false)
    private String name;

    @Column( columnDefinition = "NVARCHAR(500)")
    private String content;

    @Column(name = "code", length=100, nullable = false)
    private String code;

    @Column(name = "start_day", nullable = false)
    private LocalDateTime startDay;

    @Column(name = "end_day", nullable = false)
    private LocalDateTime endDay;

    @Column(name = "discount", nullable = false)
    private Double discount;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "limit_amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double limitAmount;

    @Column(name = "status", columnDefinition = "TINYINT", nullable = false)
    private StatusPromotion status;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonIgnore
    private UserEntity staff;

    @OneToMany(mappedBy = "promotion")
    @JsonIgnore
    private List<InvoiceEntity> invoices;


}
