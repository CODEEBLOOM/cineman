package com.codebloom.cineman.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
    @Column(name = "id_promotion")
    private Long promotionId;

    @Column(name = "name_promotion", columnDefinition = "NVARCHAR(100)")
    private String name;

    @Column(name = "promotion_content", columnDefinition = "NVARCHAR(1000)")
    private String content;

    @Column(name = "id_code", columnDefinition = "VARCHAR(100)")
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
    @JoinColumn(name = "id_staff", referencedColumnName = "id_account")
    private UserEntity staff;


}
