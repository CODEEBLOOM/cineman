package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.codebloom.cineman.common.enums.StatusPromotion;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;


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
    private Long promotionId;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String name;

    @Column( columnDefinition = "NVARCHAR(500)")
    private String content;

    @Column(name = "code", length=100)
    private String code;

    @Column(name = "start_day")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date startDay;

    @Column(name = "end_day")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date endDay;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "status", columnDefinition = "TINYINT")
    private StatusPromotion status;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private UserEntity staff;

    @OneToMany(mappedBy = "promotion")
    private List<InvoiceEntity> invoices;


}
