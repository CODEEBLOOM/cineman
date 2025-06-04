package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "promotions")
public class PromotionEntity implements Serializable  {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotionId;

    @Column(name = "name_promotion", columnDefinition = "NVARCHAR(100)")
    private String name;

    @Column(name = "promotion_content", columnDefinition = "NVARCHAR(1000)")
    private String content;

    @Column(name = "code_id", columnDefinition = "VARCHAR(100)")
    private String code;

    @Column(name = "start_day")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDay;

    @Column(name = "end_day")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDay;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "status", columnDefinition = "TINYINT")
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private UserEntity staff;

    @OneToMany(mappedBy = "promotion")
    private List<InvoiceEntity> invoices;


}
